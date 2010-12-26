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
    xmlns:dynamic="http://exslt.org/dynamic"
    extension-element-prefixes="dynamic"
>
  <xsl:import href="default-html.xsl"/>
  <xsl:import href="shared.xsl"/>
  <xsl:import href="multipage-identifier.xsl"/>


  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>

  <xsl:variable name="page-name" select="/n:opus/n:meta/n:page/n:page-identifier"/>
  <xsl:variable name="page-path" select="/n:opus/n:meta/n:page/n:page-path"/>

  <!-- Couldn't find how to get the page-nodeset for identifierless page. -->
  <xsl:variable name="page-nodeset" select="dynamic:evaluate( $page-path )"/>

  <xsl:variable name="page-id" select="generate-id( $page-nodeset )"/>
  <xsl:variable name="page-undefined" select="count( /n:opus/n:meta/n:page ) = 0"/>

  <xsl:output method="html"/>

  <xslmeta:multipage>

    <xsl:import href="multipage-identifier.xsl"/>

    <xsl:template match="/">

      <n:pages>
        <xsl:for-each select="/n:opus/n:level">
          <!--
              This test makes sense because chosing n:level[ position() > 1 ]
               introduces a shift in the elements.
          -->
          <xsl:if test="position() > 1 and n:level-title != 'LINKS' ">
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


  <xsl:template match="/">

    <html>
      <head>
        <xsl:element name="meta">
          <xsl:attribute name="http-equiv">content-type</xsl:attribute>
          <xsl:attribute name="content">text/html;charset=<xsl:value-of select="$charset"/>
          </xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang"/>
        <meta name="Copyright">
          <xsl:attribute name="content">
            <xsl:apply-templates select="$author"/>
            <xsl:text> </xsl:text>
            <xsl:apply-templates select="$copyright-year"/>
          </xsl:attribute>
        </meta>
        <title>Novelang</title>


        <!-- Ugly: support both relative (http://.../doc/) and absolute stylesheet (batch). -->
        <link rel="stylesheet" type="text/css" href="/reset.css"/>
        <link rel="stylesheet" type="text/css" href="reset.css"/>
        <link rel="stylesheet" type="text/css" href="/layout.css"/>
        <link rel="stylesheet" type="text/css" href="layout.css"/>
        <link rel="stylesheet" type="text/css" href="/text.css"/>
        <link rel="stylesheet" type="text/css" href="text.css"/>

        <link rel="alternate" type="application/atom+xml" title="News feed (Atom)">
          <xsl:attribute name="href">
            <xsl:value-of select="$newsFeed"/>
          </xsl:attribute>
        </link>


      </head>
      <body bgcolor="#C0C0C0" >

        <div class="colmask threecol">
          <div class="colmid">
            <div class="colleft">
              <div class="col1">

                <div class="header">
                  <h1>
                    <xsl:value-of select="$title"/>
                  </h1>
                  <h2>
                    <xsl:value-of select="$subtitle"/>
                  </h2>
                </div>
                

                <div class="co1inside" >
                  <xsl:apply-templates/>
                </div>
              </div>

              <div class="col2">

                <div id="Version">version
                  <xsl:value-of select="$version"/>
                </div>

                <div id="Links">
                  <p>
                    <a>
                      <xsl:attribute name="href">
                        <xsl:value-of select="$download"/>
                      </xsl:attribute>
                      Download
                    </a>
                  </p>
                  <xsl:for-each
                      select="/n:opus/n:level[ n:style='parameters' and n:level-title='LINKS' ]/n:paragraph-regular"
                  >
                    <p><xsl:apply-templates/></p>
                  </xsl:for-each>
                </div>

                <div id="Sponsors">
                  <a href="http://sourceforge.net">
                    <img border="0" alt="SourceForge.net Logo">
                      <xsl:attribute name="src">
                        <xsl:value-of select="$sf-logo"/>
                      </xsl:attribute>
                    </img>
                  </a>
                </div>

              </div>

              <div class="col3">
                <ul>
                  <xsl:for-each select="/n:opus/n:level[ n:level-title != 'LINKS' ]">
                    <li>
                      <xsl:choose>
                        <!-- Ugly, because we can't get $page-id for identifierless page. -->
                        <xsl:when test="generate-id( . ) = $page-id or ( $page-undefined and position() = 1 )">
                          <b>
                            <xsl:call-template name="level-link"/>
                          </b>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:call-template name="level-link"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </li>
                  </xsl:for-each>
                </ul>
              </div>



            </div>
          </div>
        </div>

        <div id="footer">
          <div id="ProjectAuthor">
            &copy; <xsl:apply-templates select="$copyright-year"/>
              <xsl:text> </xsl:text>
            <xsl:apply-templates select="$author"/>
          </div>
          <div id="CssAuthor">
            CSS layout inspired by <a href="http://matthewjamestaylor.com/blog/perfect-3-column.htm">Perfect 'Holy Grail' 3 Column Liquid Layout</a> by <a href="http://matthewjamestaylor.com">Matthew James Taylor</a>.
          </div>

        </div>


        <script type="text/javascript">
          var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." :
          "http://www.");
          document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
        </script>
        <script type="text/javascript">
          try {
          var pageTracker = _gat._getTracker("UA-4856677-3");
          pageTracker._trackPageview();
          } catch(err) {}
        </script>
      </body>
    </html>
  </xsl:template>


  <xsl:template match="/n:opus">
    <xsl:choose>
      <xsl:when test="n:meta/n:page/n:page-identifier">
        <xsl:apply-templates select="$page-nodeset"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="/n:opus/n:level[ 1 ]"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="/n:opus/n:level-title"/>

  <xsl:template match="n:level/n:level-title" mode="navigation">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="//n:level/n:level-title">
    <h1>
      <xsl:apply-templates/>
    </h1>
  </xsl:template>

  <xsl:template match="//n:level/n:level/n:level-title">
    <h2>
      <xsl:apply-templates/>
    </h2>
  </xsl:template>

  <xsl:template
      match="//n:block-inside-square-brackets/n:block-of-literal-inside-grave-accent-pairs">
    <code>
      <xsl:value-of select="."/>
    </code>
  </xsl:template>

  <xsl:template match="n:list-with-triple-hyphen" >
    <ul class="big-list" >
      <xsl:for-each select="n:paragraph-as-list-item" >
        <li><xsl:apply-templates/></li>
      </xsl:for-each>
    </ul>
  </xsl:template>


  <xsl:template name="level-link">
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
  </xsl:template>


  <!--This doesn't work when put in shared.xsl .-->
  <xsl:template match="*[n:style='parameters']"/>


  <!-- Override default, we don't want descriptors to appear. -->
  <xsl:template name="descriptor-body"/>


  <!--
    Unfinished implementation.
    - Needs unique identifiers to link properly.
    - Needs layout parameter.

    About layout parameter:
    When there are a lot of versions, a bulletted list is too long.
    A flat list is probably the best because multi-column layout can easily go wrong
    depending on the layout.
    This could do the job:
    | SUBLEVELS | flat |    
  -->
  <xsl:template match="n:block-inside-square-brackets[ text() = 'SUBLEVELS' ]" >
    <ul>
      <xsl:for-each select="ancestor-or-self::node()[ name()= 'n:level' ]/n:level">
        <li><xsl:value-of select="n:level-title"/> </li>
      </xsl:for-each>
    </ul>
  </xsl:template>


</xsl:stylesheet>
