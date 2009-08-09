package novelang.book.function.builtin;

import novelang.book.function.Command;
import novelang.book.function.AbstractFunctionCall;
import novelang.book.function.CommandParameterException;
import novelang.book.CommandExecutionContext;
import novelang.common.SyntacticTree;
import novelang.common.Problem;
import novelang.common.Renderable;
import novelang.common.SimpleTree;
import novelang.common.FileTools;
import novelang.common.StructureKind;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreeTools;
import novelang.common.tree.TreepathTools;
import novelang.part.Part;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.WORD_;
import novelang.system.LogFactory;
import novelang.system.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.Map;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

/**
 * @author Laurent Caillette
 */
public class InsertCommand implements Command {
  
  private static final Log LOG = LogFactory.getLog( InsertCommand.class ) ;  
  
  private final String fileName ;
  private final boolean recurse ;
  private final boolean createLevel ;
  private final String styleName ;

  public InsertCommand( 
      final String fileUrl, 
      final boolean recurse, 
      final boolean createLevel, 
      final String styleName 
  ) {
    this.fileName = fileUrl.substring( "file:".length() ) ; 
    this.recurse = recurse ;
    this.createLevel = createLevel ;
    this.styleName = styleName ;
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

    if( recurse ) {
      return evaluateRecursive( environment, insertedFile ) ;
    } else {
      return evaluateFlat( environment, insertedFile ) ;
    }
  }

  private CommandExecutionContext evaluateFlat( 
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
      return environment.update( Lists.newArrayList( Problem.createProblem( e ) ) ) ;
    }

    final Renderable partWithRelocation =
        rawPart.relocateResourcePaths( environment.getBaseDirectory() ) ;

    final SyntacticTree partTree = partWithRelocation.getDocumentTree() ;
    final SyntacticTree styleTree = createStyleTree( styleName ) ;
    
    Treepath< SyntacticTree > book = Treepath.create( environment.getBookTree() ) ;

    if( null != partTree ) {
      if( createLevel ) {
          book = createChapterFromPartFilename( 
              book, insertedFile, partTree, styleTree ) ;
      } else {
        for( SyntacticTree partChild : partTree.getChildren() ) {
          if( styleTree != null ) {
            partChild = TreeTools.addFirst( partChild, styleTree ) ;
          }
          book = TreepathTools.addChildLast( book, partChild ) ;
        }
      }
    }

    return environment.update( book.getTreeAtStart() ).update( rawPart.getProblems() ) ;
  }

  private static SyntacticTree createStyleTree( String styleName ) {
    if( null == styleName ) {
      return null ;
    } else {
      return new SimpleTree( NodeKind._STYLE.name(), new SimpleTree( styleName ) ) ;
    }
  }

  private CommandExecutionContext evaluateRecursive(
      final CommandExecutionContext environment,
      final File insertedFile
  ) {
    LOG.debug( "Command %s evaluating recursively on %s", this, insertedFile  ) ;
   
    
    final List< Problem > problems = Lists.newArrayList() ;
    final SyntacticTree styleTree = createStyleTree( styleName ) ;
    Treepath< SyntacticTree > book = Treepath.create( environment.getBookTree() ) ;

    try {
      final Iterable< File > partFiles = scanPartFiles( insertedFile ) ;
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
            book = createChapterFromPartFilename( book, partFile, partTree, styleTree );

          } else {
            for( SyntacticTree partChild : partTree.getChildren() ) {
              if( styleTree != null ) {
                partChild = TreeTools.addFirst( partChild, styleTree ) ;
              }
              final Treepath< SyntacticTree > updatedBook =
                  TreepathTools.addChildLast( book, partChild ) ;
              book = Treepath.create( updatedBook.getTreeAtStart() ) ;
            }
          }


        }
      }
    } catch( CommandParameterException e ) {
      problems.add( Problem.createProblem( e ) ) ;
    }

    return environment.update( book.getTreeAtStart() ).update( problems ) ;
  }

  private static Treepath< SyntacticTree > createChapterFromPartFilename(
      Treepath< SyntacticTree > book,
      final File partFile,
      final SyntacticTree partTree,
      final SyntacticTree styleTree
  ) {
    final SyntacticTree word = new SimpleTree(
        WORD_.name(),
        new SimpleTree( FilenameUtils.getBaseName( partFile.getName() ) )
    ) ;
    final SyntacticTree title = new SimpleTree( NodeKind.LEVEL_TITLE.name(), word ) ;

    SyntacticTree chapterTree = TreeTools.addFirst(
        new SimpleTree( NodeKind._LEVEL.name(), partTree.getChildren() ),
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

  private static Iterable< File > scanPartFiles( File directory )
      throws CommandParameterException
  {
    if( directory.isDirectory() ) {
      final List< File > files = Ordering.from( FileTools.ABSOLUTEPATH_COMPARATOR ).sortedCopy(
          FileTools.scanFiles( directory, StructureKind.PART.getFileExtensions() )
      );

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
        '}'
    ;
  }
}
