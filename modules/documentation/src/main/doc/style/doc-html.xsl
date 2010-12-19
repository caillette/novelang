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
    
] >

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xslmeta="http://novelang.org/xsl-meta/1.0"
    xmlns:dyn="http://exslt.org/dynamic"
    extension-element-prefixes="dyn"    
>
  <xsl:import href="default-html.xsl" />
  <xsl:import href="shared.xsl" />
  
  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>
  

  <xsl:output method="xml" />

<!--
  <xslmeta:multipage>

    <xsl:template match="/" >

      <n:pages>
        <xsl:for-each select="/n:opus/n:level">
          <n:page>
            <n:page-identifier>
              <xsl:value-of select="n:level-title"/>
            </n:page-identifier>
            <n:page-path>/opus/level[<xsl:value-of select="position()"/>]</n:page-path>
          </n:page>
        </xsl:for-each>
      </n:pages>

    </xsl:template>

  </xslmeta:multipage>
-->


  <xsl:template match="/" >

    <html>
      <head>
        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$charset" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" />
        <meta name= "Copyright" ><xsl:attribute name="content" > <xsl:apply-templates select="$author" /><xsl:text> </xsl:text><xsl:apply-templates select="$copyright-year" /></xsl:attribute></meta>
        <title>Novelang</title>

        <meta name="viewport" content="width=700, initial-scale=0.45, minimum-scale=0.45" />
        <link rel="stylesheet" type="text/css" href="/screen.css" />

        <link rel="alternate" type="application/atom+xml" title="News feed (Atom)" >
        <xsl:attribute name="href" ><xsl:value-of select="$newsFeed" /></xsl:attribute>
        </link>


      </head>
    <body>
      <div id="Box">

        <div id="Title" ><xsl:value-of select="$title" /></div>
        
        <div class="chapter" > <h1> <xsl:value-of select="$subtitle" /></h1> </div>

        <xsl:apply-templates />


        <div id="Sidebar">


          <div id="Author" >Written by <xsl:apply-templates select="$author" /></div>
          <div id="Version" >version <xsl:value-of select="$version" /></div>

          <div id="Links" >
            <ul>
              <li><a><xsl:attribute name="href" ><xsl:value-of select="$download" /></xsl:attribute>Download</a> </li>
              <xsl:for-each
                  select="/n:opus/n:level[ n:style='parameters' and n:level-title='LINKS' ]/n:paragraph-regular"
              >
                <li>
                  <xsl:apply-templates/>
                </li>

              </xsl:for-each>
            </ul>
          </div>


          <div id="Sponsors" >
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

  <xsl:template match="/n:opus/n:level-title" />

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
  
  
  <!--This doesn't work when put in shared.xsl .-->
  <xsl:template match="*[n:style='parameters']" />


  <!-- Override default, we don't want descriptors to appear. -->
  <xsl:template name="descriptor-body" />


</xsl:stylesheet>
