<?xml version="1.0"?>
<!DOCTYPE foo [

  <!ENTITY % ISOnum PUBLIC
      "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML"
      "ISOnum.pen"
  >
  %ISOnum;

  <!ENTITY % ISOpub PUBLIC
      "ISO 8879:1986//ENTITIES Publishing//EN//XML"
      "ISOpub.pen"
  >
  %ISOpub;

  <!ENTITY % ISOlat1 PUBLIC
      "ISO 8879:1986//ENTITIES Added Latin 1//EN//XML"
      "ISOlat1.pen"
  >
  %ISOlat1;
    

  <!ENTITY newsFeed           "http://novelang.blogspot.com/feeds/posts/default" >
  <!ENTITY blog               "http://novelang.blogspot.com" >
  <!ENTITY usersGroup         "http://groups.google.com/group/novelang-users/topics" >
  <!ENTITY developersGroup    "http://groups.google.com/group/novelang-developers/topics" >
  <!ENTITY sourceforgeProject "http://sourceforge.net/projects/novelang" >
  <!ENTITY sourceforgeJavadoc "http://novelang.sourceforge.net/javadoc" >
  <!ENTITY download           "http://sourceforge.net/project/showfiles.php?group_id=227480&amp;package_id=275418" >
  <!ENTITY github             "http://github.com/caillette/novelang/tree/master" >
  <!ENTITY pdfDocument        "http://novelang.sf.net/Novelang.pdf" >
  <!ENTITY license            "http://www.gnu.org/licenses/gpl-3.0.txt" >

] >

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:import href="punctuation-US-EN.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="encoding"/>

  <xsl:output method="xml" />

  <xsl:template match="/" >

    <html>
      <head>
        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$encoding" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" />
        <meta name= "Copyright" content="Laurent Caillette 2008" />
        <title>Novelang, the simplest text processor</title>

        <meta name="viewport" content="width=700, initial-scale=0.45, minimum-scale=0.45" />
        <link rel="stylesheet" type="text/css" href="screen.css" />

        <link
            rel="alternate" type="application/atom+xml" title="News feed (Atom)"
            href="&newsFeed;"
        />


      </head>
    <body>
      <div id="Box">

        <div id="Title" ><xsl:value-of select="/n:book/n:title[1]" /></div>

        <div class="chapter" >
          <h1><xsl:value-of select="/n:book/n:title[2]" /></h1>
        </div>

        <xsl:apply-templates />


        <div id="Sidebar">

          <p>By <strong>Laurent Caillette</strong></p>

          <ul><!--&#9733;-->
            <li><a href="&blog;">Blog</a></li>
            <li><a href="&usersGroup;">Users' list</a></li>
            <li><a href="&developersGroup;">Developers' list</a></li>
            <li><a href="&sourceforgeProject;">Sourceforge</a></li>
            <li><a href="&github;">Sources</a></li>
            <li><a href="&sourceforgeJavadoc;">Javadoc</a></li>
            <li><a href="&download;">Download</a></li>
            <li><a href="&pdfDocument;">PDF</a></li>
            <li><a href="&license;">License</a></li>
          </ul>


          <div id="Sponsors" >
            <!--https://sourceforge.net/project/admin/logo.php?group_id=227480  -->
            <a href="http://sourceforge.net">
              <img src="http://sflogo.sourceforge.net/sflogo.php?group_id=227480&amp;type=2"
                   width="125" height="37" border="0" alt="SourceForge.net Logo" />
            </a>

          </div> <!-- Sponsors -->

        </div> <!-- Sidebar -->
      </div> <!-- Box -->



    </body>
    </html>
  </xsl:template>

  <xsl:template match="/n:book/n:title" />

  <xsl:template match="n:chapter" >
    <div class="chapter" >
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="n:chapter/n:title | n:chapter/n:identifier" >
    <h1><xsl:apply-templates /></h1>
  </xsl:template>

  <xsl:template match="n:section" >
    <div class="section" >
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="n:section/n:title | n:section/n:identifier" >
    <h2><xsl:apply-templates /></h2>
  </xsl:template>

  <xsl:template match="n:blockquote" >
    <blockquote>
      <xsl:apply-templates/>
    </blockquote>
  </xsl:template>

  <xsl:template match="n:literal" >
    <pre>
      <xsl:apply-templates/>
    </pre>
  </xsl:template>

  <xsl:template match="n:soft-inline-literal" >
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="n:hard-inline-literal" >
    <tt><xsl:apply-templates/></tt>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-plain | n:paragraph-speech-escaped" >
    <p>
    <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="n:url" >
    <p>
    <a><xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute><xsl:value-of select="." /></a>
    </p>
  </xsl:template>

  <xsl:template match="n:paragraph-speech" >
    <xsl:call-template name="speech" >
      <xsl:with-param name="speech-symbol" >&mdash;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="n:paragraph-speech-continued" >
    <xsl:call-template name="speech" >
      <xsl:with-param name="speech-symbol" >&raquo;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="speech" >
    <xsl:param name = "speech-symbol" />
    <p>
    <xsl:value-of select="$speech-symbol" />&nbsp;
    <xsl:apply-templates/>
    </p>
  </xsl:template>


  <xsl:template match="n:emphasis" >
    <i><xsl:apply-templates/></i>
  </xsl:template>




</xsl:stylesheet>
