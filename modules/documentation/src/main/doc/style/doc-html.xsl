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
  <xsl:import href="multipage-identifier.xsl" />
  
  
  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>

  <xsl:variable name="page-name" select="/n:opus/n:meta/n:page/n:page-identifier" />
  <xsl:variable name="page-path" select="/n:opus/n:meta/n:page/n:page-path" />
  <xsl:variable name="page-nodeset" select="dyn:evaluate( $page-path )" />
  <xsl:variable name="page-id" select="generate-id( $page-nodeset )" />

  <xsl:output method="xml" />

  <xslmeta:multipage>

    <xsl:import href="multipage-identifier.xsl" />

    <xsl:template match="/" >

      <n:pages>
        <xsl:for-each select="/n:opus/n:level">
          <!--
              This test makes sense because chosing n:level[ position() > 1 ]
               introduces a shift in the elements.
          -->
          <xsl:if test="position() > 1" > 
            <n:page>
              <n:page-identifier>
                <xsl:call-template name="extract-page-identifier"/>
              </n:page-identifier>
              <n:page-path>/n:opus/n:level[<xsl:value-of select="position()"/>]
              </n:page-path>
            </n:page>
          </xsl:if>
        </xsl:for-each>
      </n:pages>

    </xsl:template>

  </xslmeta:multipage>


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

        <!-- Ugly: support both relative (http://.../doc/) and absolute stylesheet (batch). --> 
        <link rel="stylesheet" type="text/css" href="/reset.css" />
        <link rel="stylesheet" type="text/css" href="reset.css" />
        <link rel="stylesheet" type="text/css" href="/layout.css" />
        <link rel="stylesheet" type="text/css" href="layout.css" />
        <link rel="stylesheet" type="text/css" href="/text.css" />
        <link rel="stylesheet" type="text/css" href="text.css" />

        <link rel="alternate" type="application/atom+xml" title="News feed (Atom)" >
        <xsl:attribute name="href" ><xsl:value-of select="$newsFeed" /></xsl:attribute>
        </link>


      </head>
    <body>
      <div id="maincontainer">

        <div id="topsection">
          <div class="innertube">
            <h1><xsl:value-of select="$title" /></h1>
            <!-- Commented as long as it conflicts with top-level titles. -->
            <h2> <xsl:value-of select="$subtitle" /></h2> 
          </div>
        </div>

        <div id="contentwrapper">
          <div id="contentcolumn">
            <div class="innertube">
              <xsl:apply-templates />
            </div>
          </div>
        </div>

        <div id="rightcolumn">
          <div class="innertube">
            <ul>
              <xsl:for-each select="/n:opus/n:level[ n:level-title != 'LINKS' ]">
                <li>
                  <a>
                    <xsl:choose>
                      <xsl:when test="position() > 1">
                        <xsl:attribute name="href">
                          novelang--<xsl:call-template name="extract-page-identifier"/>.html
                        </xsl:attribute>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:attribute name="href">novelang.html</xsl:attribute>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:apply-templates select="n:level-title" mode="navigation"/>
                  </a>
                </li>
              </xsl:for-each>
            </ul>
          </div>
        </div>


        <div id="leftcolumn">
          <div class="innertube">

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
                <img src="http://sflogo.sourceforge.net/sflogo.php?group_id=227480&amp;type=9"
                     border="0" alt="SourceForge.net Logo" />
              </a>
            </div>

          </div>
        </div>


      </div>




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

  <xsl:template match="/n:opus" >
    <xsl:choose>
      <xsl:when test="n:meta/n:page/n:page-identifier" >
        <xsl:apply-templates select="$page-nodeset" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="/n:opus/n:level[ 1 ]" />
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="/n:opus/n:level-title" />

  <xsl:template match="n:level/n:level-title" mode="navigation" >
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="//n:level/n:level-title" >
    <h1><xsl:apply-templates /></h1>
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
