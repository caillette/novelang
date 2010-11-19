package org.novelang.build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.antlr.stringtemplate.CommonGroupLoader;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.StringTemplateGroupLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for Java code generation.
 *
 * @author Laurent Caillette
 */
public abstract class JavaGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger( GrammarBasedJavaGenerator.class ) ;
  protected static final String JAVA_EXTENSION = ".java" ;
  protected final String packageName ;
  protected final String className ;
  protected final File targetFile ;
  protected final String generatorName ;
  protected final String generationTimestamp ;

  private static final StringTemplateGroup STRINGTEMPLATEGROUP = loadStringTemplateGroup() ;

  private static final StringTemplateErrorListener STRING_TEMPLATE_ERROR_LISTENER =
      new StringTemplateErrorListener() {
        @Override
        public void error( final String s, final Throwable throwable ) {
          throw new RuntimeException( s, throwable ) ;
        }
        @Override
        public void warning( final String s ) {
          throw new RuntimeException( s ) ;
        }
      }
  ;

  protected JavaGenerator(
      final String className,
      final String packageName,
      final File targetDirectory
  ) throws IOException {
    this.className = className;
    this.packageName = packageName;
    generatorName = getClass().getName();

    this.targetFile = CodeGenerationTools.resolveTargetFile(
        targetDirectory, packageName, className + JAVA_EXTENSION ) ;

    this.generationTimestamp = new Date().toString() ;

  }

  public final File getTargetFile() {
    return targetFile ;
  }

  public final String getPackageName() {
    return packageName ;
  }

  public String getClassName() {
    return className ;
  }

  public void generate() throws IOException {
    final String code = generateCode() ;
    createDirectory( targetFile ) ;
    IOUtils.write(
        code,
        new FileOutputStream( targetFile )
    ) ;
    LOGGER.info( "Wrote '{}'", targetFile.getAbsolutePath() ) ;
  }

  /**
   * Creates given directory, or the directory of a "leaf" file.
   *
   * @param target non-null object representing a leaf file or a directory.
   *
   * @throws java.io.IOException
   */
  protected final void createDirectory( final File target ) throws IOException {
    final File targetDirectory ;
    if( target.isDirectory() ) {
      targetDirectory = target ;
    } else {
      targetDirectory = target.getParentFile().getCanonicalFile() ;
    }
    if( ! targetDirectory.exists() && ! targetDirectory.mkdirs() ) {
      throw new IOException( "Could not create: '" + targetDirectory.getAbsolutePath() + "'" ) ;
    }
  }

  protected abstract String generateCode() throws IOException ;

  private static StringTemplateGroup loadStringTemplateGroup() {
    final String templateDirectory =
        ClassUtils.getPackageName( JavaGenerator.class ).replace( '.', '/' ) ;
    LOGGER.info( "Loading StringTemplates from classpath directory: '{}'", templateDirectory ) ;

    final StringTemplateGroupLoader loader =
        new CommonGroupLoader( templateDirectory, STRING_TEMPLATE_ERROR_LISTENER ) ;
    StringTemplateGroup.registerGroupLoader( loader ) ;
    return StringTemplateGroup.loadGroup( "java" ) ;
  }

  protected final StringTemplate createStringTemplate( final String templateName ) {
    final StringTemplate template = STRINGTEMPLATEGROUP.getInstanceOf( templateName ) ;

    template.setAttribute( "package", packageName ) ;
    template.setAttribute( "name", className ) ;
    template.setAttribute( "generatorName", generatorName ) ;
    template.setAttribute( "generationTimestamp", generationTimestamp ) ;

    return template ;

  }
}
