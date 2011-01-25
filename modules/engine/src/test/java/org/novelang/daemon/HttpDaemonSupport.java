/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.novelang.daemon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.http.client.methods.HttpGet;
import org.novelang.ResourceTools;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Directory;
import org.novelang.common.filefixture.Resource;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.parse.DaemonParameters;
import org.novelang.configuration.parse.GenericParametersConstants;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.TcpPortBooker;
import org.novelang.outfit.loader.CompositeResourceLoader;
import org.novelang.testing.junit.MethodSupport;

import static com.google.common.base.Charsets.UTF_8;

/**
 * A JUnit {@link org.junit.Rule} supporting concurrent test execution.
 *
 * @author Laurent Caillette
 */
public class HttpDaemonSupport extends MethodSupport {

  private static final Logger LOGGER = LoggerFactory.getLogger( HttpDaemonSupport.class ) ;

  static {
    ResourcesForTests.initialize() ;
  }

  private final int daemonPort = TcpPortBooker.THIS.find() ;
  protected final ResourceInstaller resourceInstaller;

  private HttpDaemon httpDaemon = null ;


  public HttpDaemonSupport() {
    resourceInstaller = new ResourceInstaller( this ) ;
  }

  public HttpDaemonSupport( final Object executionLock ) {
    super( executionLock ) ;
    resourceInstaller = new ResourceInstaller( this ) ; 
  }

  @Override
  protected void afterStatementEvaluation() throws Exception {
    httpDaemon.stop() ;
    // Don't nullify. This prevents from calling a setup method a second time.
  }



// ============
// Daemon setup
// ============

  protected final void setup() throws Exception {
    daemonSetup( UTF_8 ) ;
  }

  protected final String setup( final Resource resource ) throws Exception {
    resourceInstaller.copy( resource ) ;
    daemonSetup( DefaultCharset.RENDERING ) ;
    final String novellaSource = resource.getAsString( DefaultCharset.SOURCE ) ;
    return novellaSource ;
  }

  protected final void setup(
      final File styleDirectory,
      final Charset renderingCharset
  ) throws Exception {
    daemonSetup( styleDirectory, renderingCharset ) ;
  }

  public final String alternateSetup( // TODO rename into setupAndLoadSourceDocument
       final Resource resource,
       final Charset sourceCharset,
       final Charset renderingCharset
  ) throws Exception {
    resourceInstaller.copy( resource ) ;
    daemonSetup( renderingCharset ) ;
    final String novellaSource = resource.getAsString( sourceCharset ) ;
    return novellaSource ;
  }

  private void daemonSetup( final File styleDirectory, final Charset renderingCharset )
      throws Exception
  {
    httpDaemon = new HttpDaemon( ResourceTools.createDaemonConfiguration(
        daemonPort,
        resourceInstaller.getTargetDirectory(),
        CompositeResourceLoader.create( ConfigurationTools.BUNDLED_STYLE_DIR, styleDirectory )
    ) ) ;
    httpDaemon.start() ;
  }

  private void daemonSetup( final Charset renderingCharset )
      throws Exception
  {
    httpDaemon = new HttpDaemon( ResourceTools.createDaemonConfiguration(
        daemonPort,
        resourceInstaller.getTargetDirectory(),
        renderingCharset
    ) ) ;
    httpDaemon.start() ;
  }

  protected final void setupWithFonts( final Directory fontDirectory )
      throws Exception
  {
    final File directoryAsFile = resourceInstaller.copy( fontDirectory ) ;


    final DaemonParameters daemonParameters = new DaemonParameters(
        resourceInstaller.getTargetDirectory(),
        GenericParametersConstants.OPTIONPREFIX + DaemonParameters.OPTIONNAME_HTTPDAEMON_PORT,
        "" + daemonPort,
        GenericParametersConstants.OPTIONPREFIX + GenericParametersConstants.OPTIONNAME_FONT_DIRECTORIES,
        directoryAsFile.getAbsolutePath()
    ) ;

    httpDaemon = new HttpDaemon(
        ConfigurationTools.createDaemonConfiguration( daemonParameters ) ) ;

    httpDaemon.start() ;
  }

  
// ========
// Requests
// ========

  private final AtomicInteger documentWriteCounter = new AtomicInteger( 0 ) ;

  public HttpGet createHttpGet( final String documentRequestAsString ) {
    return new HttpGet( "http://localhost:" + daemonPort + documentRequestAsString ) ;
  }

  public byte[] readAsBytes( final String documentRequestAsString )
      throws IOException
  {
    return readAsBytes( new URL( "http://localhost:" + daemonPort + documentRequestAsString ) ) ;
  }


  public String readAsString( final Resource resource ) throws IOException {
    return readAsString( "/" + resource.getName(), UTF_8 ) ;
  }

  public String readAsString( final Resource resource, final Charset charset ) throws IOException {
    return readAsString( "/" + resource.getName(), charset ) ;
  }

  public String readAsString( final String documentRequestAsString ) throws IOException {
    return new String( readAsBytes( documentRequestAsString ), UTF_8 ) ;
  }

  public String readAsString( final String documentRequestAsString, final Charset charset )
      throws IOException
  {
    return new String( readAsBytes( documentRequestAsString ), charset ) ;
  }



  private byte[] readAsBytes( final URL url ) throws IOException {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
    IOUtils.copy( url.openStream(), outputStream ) ;
    final byte[] bytes = outputStream.toByteArray() ;
    saveResponseContent( bytes ) ;
    return bytes ;
  }

  /**
   * TODO: keep documents in memory, dump them only if the test fails.
   * This will avoid a few disk-based operations.
   */
  private void saveResponseContent( final byte[] bytes ) throws IOException {
    final File responseContentDirectory = new File( getDirectory(), "saved" ) ;
    responseContentDirectory.mkdirs() ;
    final File savedContent = new File(
        responseContentDirectory, "saved-" + documentWriteCounter.getAndIncrement() ) ;
    FileUtils.writeByteArrayToFile( savedContent, bytes ) ;
    LOGGER.info( "Wrote file '", savedContent.getAbsolutePath(), "'" ) ;    
  }


}
