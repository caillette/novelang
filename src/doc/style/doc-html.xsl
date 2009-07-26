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
  <!ENTITY download           "http://sourceforge.net/projects/novelang/files" >
  <!ENTITY github             "http://github.com/caillette/novelang/tree/master" >
  <!ENTITY issues             "http://github.com/caillette/novelang/issues" >
  <!ENTITY pdfDocument        "http://novelang.sf.net/novelang.pdf" >
  <!ENTITY license            "http://www.gnu.org/licenses/gpl-3.0.txt" >

] >

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:import href="default-html.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>

  <xsl:output method="xml" />

  <xsl:template match="/" >

    <html>
      <head>
        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$charset" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" />
        <meta name= "Copyright" content="Laurent Caillette 2008" />
        <title>Novelang</title>

        <meta name="viewport" content="width=700, initial-scale=0.45, minimum-scale=0.45" />
        <link rel="stylesheet" type="text/css" href="screen.css" />

        <link
            rel="alternate" type="application/atom+xml" title="News feed (Atom)"
            href="&newsFeed;"
        />


      </head>
    <body>
      <div id="Box">

        <div id="Title" ><xsl:value-of select="/n:book/n:level-title[1]" /></div>

        <div class="chapter" >
          <h1><xsl:value-of select="/n:book/n:level-title[2]" /></h1>
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
            <li><a href="&issues;">Issue tracker</a></li>
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


<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-4856677-3");
pageTracker._trackPageview();
} catch(err) {}</script>      
    </body>
    </html>
  </xsl:template>

  <xsl:template match="/n:book/n:level-title" />

  <xsl:template match="//n:level" >
    <div class="chapter" >
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="//n:level/n:level-title" >
    <h1><xsl:apply-templates /></h1>
  </xsl:template>

  <xsl:template match="//n:level/n:level" >
    <div class="section" >
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="//n:level/n:level/n:level-title" >
    <h2><xsl:apply-templates /></h2>
  </xsl:template>

  <xsl:template match="//n:block-inside-square-brackets/n:block-of-literal-inside-grave-accent-pairs" >
    <code><xsl:value-of select="." /></code>
  </xsl:template>  

  <xsl:template match="n:paragraph-as-list-item" >
    <ul class="paragraph-as-list" >
      <li>
        <xsl:apply-templates/>
      </li>
    </ul>
  </xsl:template>

</xsl:stylesheet>
