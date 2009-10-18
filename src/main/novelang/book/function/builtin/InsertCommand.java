package novelang.book.function.builtin;

import novelang.book.CommandExecutionContext;
import novelang.book.function.CommandParameterException;
import novelang.common.FileTools;
import novelang.common.Location;
import novelang.common.Problem;
import novelang.common.Renderable;
import novelang.common.SimpleTree;
import novelang.common.StructureKind;
import novelang.common.SyntacticTree;
import novelang.common.tree.TreeTools;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.WORD_;
import novelang.part.Part;
import novelang.marker.FragmentIdentifier;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.treemangling.DesignatorInterpreter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * @author Laurent Caillette
 */
public class InsertCommand extends AbstractCommand {
  
  private static final Log LOG = LogFactory.getLog( InsertCommand.class ) ;  
  
  private final String fileName ;
  private final boolean recurse ;
  private final FileOrdering fileOrdering ;
  private final boolean createLevel ;
  private final int levelAbove ;
  private final String styleName ;
  private final Iterable< FragmentIdentifier > fragmentIdentifiers ;
  
  public InsertCommand(
      final Location location,
      final String fileUrl, 
      final boolean recurse, 
      final FileOrdering fileOrdering,
      final boolean createLevel,
      final int levelAbove,
      final String styleName,
      final Iterable<FragmentIdentifier> fragmentIdentifiers
  ) {
    super( location ) ;
    this.fileName = fileUrl.substring( "file:".length() ) ; 
    this.recurse = recurse ;
    this.createLevel = createLevel ;
    this.styleName = styleName ;
    
    if( fileOrdering == null ) {
      LOG.debug( "No file ordering set, using default" ) ;
      this.fileOrdering = FileOrdering.DEFAULT ;      
    } else {
      this.fileOrdering = fileOrdering ;  
    }

    Preconditions.checkArgument(
        levelAbove >= 0,
        "'levelabove' must be 0 or greater, is %d",
        levelAbove
    ) ;
    this.levelAbove = levelAbove ;
    
    this.fragmentIdentifiers = Iterables.unmodifiableIterable( fragmentIdentifiers ) ;
  }


  

  public CommandExecutionContext evaluate( CommandExecutionContext environment ) {

    final File insertedFile ;
    {
      final File candidateFile = new File( fileName ) ;
      if( candidateFile.isAbsolute() ) {
        insertedFile = candidateFile ;
      } else {
        insertedFile = new File( environment.getBookDirectory(), fileName ) ;
      }
    }

    if( insertedFile.isDirectory() ) {
      return evaluateMultiple( environment, insertedFile, recurse ) ;
    } else {
      return evaluateSingle( environment, insertedFile ) ;
    }
  }

  private CommandExecutionContext evaluateSingle( 
      final CommandExecutionContext environment,
      final File insertedFile
  ) {
    LOG.debug( "Command %s evaluating flatly on %s", this, insertedFile  ) ;
    
    final Part rawPart;
    try {
      rawPart = new Part( 
          insertedFile,
          environment.getSourceCharset(),
          environment.getRenderingCharset()
      ) ;
    } catch( MalformedURLException e ) {
      return environment.addProblems( Lists.newArrayList( Problem.createProblem( e ) ) ) ;
    }

    final Renderable partWithRelocation =
        rawPart.relocateResourcePaths( environment.getBaseDirectory() ) ;

    final SyntacticTree partTree = partWithRelocation.getDocumentTree() ;
    final SyntacticTree styleTree = createStyleTree( styleName ) ;
    
    Treepath< SyntacticTree > book = Treepath.create( environment.getDocumentTree() ) ;
    try {
      book = findLastLevel( book, levelAbove ) ;
    } catch ( CommandParameterException e ) {
      return environment.addProblem( Problem.createProblem( e ) ) ;
    }

    final List< SyntacticTree > partTrees = Lists.newArrayList() ;

    // TODO handle following cases altogether: fragments, createLevel.

    final boolean hasIdentifiers = fragmentIdentifiers.iterator().hasNext() ;

    if( partTree != null ) {
      if( hasIdentifiers ) {
        final DesignatorInterpreter designatorMapper = 
            new DesignatorInterpreter( Treepath.create( partTree ) ) ;
        final List< Problem > designatorProblems = Lists.newArrayList() ;
        for( final FragmentIdentifier fragmentIdentifier : fragmentIdentifiers ) {
          final Treepath< SyntacticTree > fragmentTreepath =
              designatorMapper.get( fragmentIdentifier ) ;
//              FragmentExtractor.extractFragment( Treepath.create( partTree ), fragmentIdentifier ) ;
          if( fragmentTreepath == null ) {
            designatorProblems.add( 
                Problem.createProblem( 
                    "Cannot find: '" + fragmentIdentifier + "'", getLocation() ) ) ;
//            return environment.addProblem(
//                Problem.createProblem( "Cannot find: '" + fragmentIdentifier + "'" ) ) ;
          } else {
            final SyntacticTree fragment = fragmentTreepath.getTreeAtEnd() ;
            partTrees.add( fragment ) ;
          }
        }
        Iterables.addAll( designatorProblems, designatorMapper.getProblems() ) ;
        if( designatorProblems.iterator().hasNext() ) {
          return environment.addProblems( designatorProblems ) ;
        }
      } else {
        Iterables.addAll( partTrees, partTree.getChildren() ) ;
      }

      if( createLevel ) {
        book = createChapterFromPartFilename(
              book,
              insertedFile,
              partTrees,
              styleTree
          ) ;
      } else {
        for( SyntacticTree partChild : partTrees ) {
          if( styleTree != null ) {
            partChild = TreeTools.addFirst( partChild, styleTree ) ;
          }
          book = TreepathTools.addChildLast( book, partChild ).getStart() ;
        }
      }

    }

    return environment.update( book.getTreeAtStart() ).addProblems( rawPart.getProblems() ) ;
  }

  private static SyntacticTree createStyleTree( String styleName ) {
    if( null == styleName ) {
      return null ;
    } else {
      return new SimpleTree( NodeKind._STYLE.name(), new SimpleTree( styleName ) ) ;
    }
  }

  private CommandExecutionContext evaluateMultiple(
      final CommandExecutionContext environment,
      final File insertedFile,
      final boolean recurse
  ) {
    LOG.debug( "Command %s evaluating recursively on %s", this, insertedFile  ) ;
   
    
    final List< Problem > problems = Lists.newArrayList() ;
    final SyntacticTree styleTree = createStyleTree( styleName ) ;
    Treepath< SyntacticTree > book = Treepath.create( environment.getDocumentTree() ) ;

    try {
      book = findLastLevel( book, levelAbove ) ;
      final Iterable< File > partFiles = scanPartFiles( insertedFile, recurse ) ;
      for( File partFile : partFiles ) {
        Part part = null ;
        try {
          part = new Part( 
              partFile, 
              environment.getSourceCharset(), 
              environment.getRenderingCharset() 
          ) ;
          Iterables.addAll( problems, part.getProblems() ) ;
        } catch( MalformedURLException e ) {
          problems.add( Problem.createProblem( e ) ) ;
        }
        if( null != part && null != part.getDocumentTree() ) {
          final SyntacticTree partTree =
              part.relocateResourcePaths( environment.getBaseDirectory() ).getDocumentTree() ;

          if( createLevel ) {
            book = createChapterFromPartFilename(
                book,
                partFile,
                partTree.getChildren(),
                styleTree
            ) ;
            book = findLastLevel( book, levelAbove ) ;

          } else {
            for( SyntacticTree partChild : partTree.getChildren() ) {
              if( styleTree != null ) {
                partChild = TreeTools.addFirst( partChild, styleTree ) ;
              }
              final Treepath< SyntacticTree > updatedBook =
                  TreepathTools.addChildLast( book, partChild ) ;
              book = Treepath.create( updatedBook.getTreeAtStart() ) ;
              book = findLastLevel( book, levelAbove ) ;
            }
          }


        }
      }
    } catch( CommandParameterException e ) {
      problems.add( Problem.createProblem( e ) ) ;
    }

    return environment.update( book.getTreeAtStart() ).addProblems( problems ) ;
  }

  private static Treepath< SyntacticTree > findLastLevel(
      Treepath< SyntacticTree > document,
      final int depth
  ) throws CommandParameterException {
    if( depth == 0 ) {
      return document ;
    }
    final SyntacticTree tree = document.getTreeAtEnd() ;
    final int lastChildIndex = tree.getChildCount() - 1 ;
    if( lastChildIndex < 0 ) {
      throw new CommandParameterException( "Found no child tree while seeking level " + depth ) ;
    }
    final SyntacticTree lastChild = tree.getChildAt( lastChildIndex ) ;
    if( lastChild.isOneOf( NodeKind._LEVEL ) ) {
      return findLastLevel( Treepath.create( document, lastChildIndex ), depth - 1 ) ;
    } else {
      throw new CommandParameterException( "Found no LEVEL as child tree" ) ;
    }
  }


  private static Treepath< SyntacticTree > createChapterFromPartFilename(
      Treepath< SyntacticTree > book,
      final File partFile,
      final Iterable< ? extends SyntacticTree > partTrees,
      final SyntacticTree styleTree
  ) {
    final SyntacticTree word = new SimpleTree(
        WORD_.name(),
        new SimpleTree( FilenameUtils.getBaseName( partFile.getName() ) )
    ) ;
    final SyntacticTree title = new SimpleTree( NodeKind.LEVEL_TITLE.name(), word ) ;

    SyntacticTree chapterTree = TreeTools.addFirst(
        new SimpleTree( NodeKind._LEVEL.name(), partTrees ),
        title
    ) ;

    if( styleTree != null ) {
      chapterTree = TreeTools.addFirst( chapterTree, styleTree ) ;
    }
    final Treepath< SyntacticTree > updatedBook = TreepathTools.addChildLast( book, chapterTree ) ;
    final SyntacticTree start = updatedBook.getTreeAtStart() ;
    book = Treepath.create( start ) ;
    return book ;
  }

  private Iterable< File > scanPartFiles( File directory, boolean recurse )
      throws CommandParameterException
  {
    if( directory.isDirectory() ) {
      final Iterable< File > files ;
      try {
        final List< File > scannedFiles = FileTools.scanFiles( 
            directory, StructureKind.PART.getFileExtensions(), recurse );
        files = fileOrdering.sort( scannedFiles ) ;
      } catch ( FileOrdering.CriteriaException e ) {
        LOG.info( "Could not sort files from '" + directory.getAbsolutePath() + "'", e ) ;
        throw new CommandParameterException( "Could not sort files: " + e.getMessage() ) ;
      }

      if( LOG.isDebugEnabled() ) {
        StringBuffer buffer = new StringBuffer(
            "Scan of '" + directory.getAbsolutePath() + "' found those files:" ) ;
        for( File file : files ) {
          try {
            buffer.append( "\n  " ).append( file.getCanonicalPath() ) ;
          } catch( IOException e ) {
            throw new RuntimeException( e ) ;
          }
        }
        LOG.debug( buffer.toString() ) ;
      }

      return files ;

    } else {
      throw new CommandParameterException(
          "Not a directory: '" + directory.getAbsolutePath() + "'" ) ;
    }
  }
  
// ==============  
// Generated code
// ==============

  @Override
  public String toString() {
    return "InsertCommand{" +
        "fileName='" + fileName + '\'' +
        ", recurse=" + recurse +
        ", createLevel=" + createLevel +
        ", styleName='" + styleName + '\'' +
        ", fragmentIdentifiers=" + fragmentIdentifiers +
        '}'
    ;
  }
}
