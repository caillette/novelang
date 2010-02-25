<?xml version="1.0"?>
<!DOCTYPE stylesheet [

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

]>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
    xmlns:nlx="xalan://novelang.rendering.xslt"
    xmlns:colormapper="novelang.rendering.xslt.color.ColorMapper"
    extension-element-prefixes="colormapper"
    exclude-result-prefixes="nlx"
>
  <xsl:import href="punctuation-US-EN.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>

  <xsl:output method="xml" />

  <xsl:template match="/" >

    <html> 
      <head>
        <link rel="stylesheet" type="text/css" href="/display2.css" />
        <link rel="stylesheet" type="text/css" href="/custom.css" />
        <link rel="stylesheet" type="text/css" href="/jquery-theme/jquery-ui-1_7_2_custom.css" />
        <style type="text/css" /> <!-- Placeholder for dynamically-created tag classes. -->

        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$charset" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" >
          <xsl:attribute name="novelang-word-count" ><xsl:value-of select="//n:meta/n:word-count" /></xsl:attribute>          
        </meta>

        <title><xsl:value-of select="$filename"/></title>

        <script type="text/javascript" src="/javascript/jquery-1_3_2_min.js" />
        <script type="text/javascript" src="/javascript/jquery-rule-1_0_1_min.js" />
        <script type="text/javascript" src="/javascript/jquery-ui-1_7_2_custom_min.js" />
        <script type="text/javascript" src="/javascript/color-palette.js" />
        <script type="text/javascript" src="/javascript/tags.js" />
        <script type="text/javascript" src="/javascript/tabs.js" />
        <!--<script type="text/javascript" src="/javascript/descriptors.js" />-->
        <script type="text/javascript"> //<![CDATA[

          $( document ).ready( function() {
            initializeTagSystem( "/javascript/colors.htm" ) ;
            <!--initializeDescriptors() ;-->
            initializeTabs() ;
          } ) ;
        //]]></script>


      </head>
    <body>



      <div id="rendered-document">
        <xsl:apply-templates />
      </div>

      <div id="right-sidebar">


        <div id="navigation" class="fixed-position" >

          <ul>
            <li><a href="#tag-list-content">Tags</a></li>
            <!--<li><a href="#identifier-list">Ids</a></li>-->
            <!--<li><a href="#debug-messages">Debug</a></li>-->
          </ul>

          <form name="tag-list" id="tag-list" >
            <!-- Dynamically filled. -->
            <div id="tag-list-content" />
            <br/>
          </form>

          <div id="identifier-list" />

          <p id="debug-messages" />

        </div>



        <div id="pin-navigation" >
          <ul>
            <li onclick="resetNavigation()" >reset</li>
            <li onclick="togglePinNavigation()">unpin</li>
          </ul>
        </div>
      </div>



      <div id="externalColorDefinitionsPlaceholder" style="display:none;" />

      <xsl:if test="//n:meta/n:tags" >
        <dl id="tag-definitions" style="display : none ;">
          <xsl:for-each select="//n:meta/n:tags/*">
            <dt><xsl:value-of select="." /></dt>
          </xsl:for-each>
        </dl>
      </xsl:if>


    </body>
    </html>
  </xsl:template>

  <xsl:template match="n:level" >
    <div class="level" >
      <xsl:call-template name="descriptor-vanilla"/>
      <xsl:apply-templates/>
    </div>

  </xsl:template>

  <xsl:template match="//n:level/n:level-title" >
    <h1><xsl:apply-templates /></h1>
  </xsl:template>

  <xsl:template match="//n:level/n:level/n:level-title" >
    <h2><xsl:apply-templates /></h2>
  </xsl:template>

  <xsl:template match="//n:level/n:level/n:level/n:level-title" >
    <h3><xsl:apply-templates /></h3>
  </xsl:template>

  <xsl:template match="n:paragraphs-inside-angled-bracket-pairs" >
    <blockquote>
      <xsl:call-template name="descriptor-for-blockquote" />
      <xsl:apply-templates />
    </blockquote>
  </xsl:template>

  <xsl:template match="n:lines-of-literal" >
    <div class="pre" ><xsl:call-template name="descriptor-for-pre" /><pre><xsl:apply-templates/></pre></div>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-regular" >
    <div class="p" >
      <xsl:call-template name="descriptor-vanilla" />
      <p>
        <xsl:apply-templates/>
      </p>
    </div>

  </xsl:template>

  <xsl:template match="n:url" >
    <a><xsl:attribute name="href"><xsl:value-of select="n:url-literal" /></xsl:attribute>
      <xsl:choose>
        <xsl:when test="n:block-inside-double-quotes" >
          <xsl:apply-templates select="n:block-inside-double-quotes/node()" />
        </xsl:when>
        <xsl:when test="n:block-inside-square-brackets" >
          <xsl:apply-templates select="n:block-inside-square-brackets/node()" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="n:url-literal" />
        </xsl:otherwise>
      </xsl:choose>
    </a>
  </xsl:template>


  <xsl:template match="n:paragraph-as-list-item" >
    <xsl:call-template name="descriptor-vanilla" />
    <div class="p" >
      <p>
        &mdash;&nbsp;
        <xsl:apply-templates/>
      </p>
    </div>

  </xsl:template>
  
  <xsl:template match="n:block-inside-solidus-pairs" >
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="n:block-of-literal-inside-grave-accents" >
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="n:block-of-literal-inside-grave-accent-pairs" >
    <code><xsl:apply-templates/></code>
  </xsl:template>

  <xsl:template match="n:word-after-circumflex-accent" >
    <sup><xsl:apply-templates/></sup>
  </xsl:template>

  <xsl:template match="n:block-after-tilde/n:subblock[position() > 1]" >&#x200b;<xsl:apply-templates/></xsl:template>



  <xsl:template match="n:cell-rows-with-vertical-line" >
    <xsl:call-template name="descriptor-vanilla" />
    <table>
      <xsl:apply-templates />
    </table>
  </xsl:template>

  <xsl:template match="n:cell-row" >
    <tr>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>

  <xsl:template match="n:cell" >
    <td>
      <xsl:apply-templates/>
    </td>
  </xsl:template>
  
  <xsl:template match="n:raster-image/n:resource-location" >
    <img>
      <xsl:attribute name="src" ><xsl:value-of select="." /></xsl:attribute>
      <xsl:attribute name="title" ><xsl:value-of select="." /> [<xsl:value-of select="../n:image-height"/>x<xsl:value-of select="../n:image-height"/>]</xsl:attribute>
    </img>
  </xsl:template>  

  <xsl:template match="n:vector-image/n:resource-location" >
    <object type="text/xml" > <!--type="image/svg+xml"-->
      <xsl:attribute name="data" ><xsl:value-of select="." /></xsl:attribute>
      <xsl:attribute name="width"><xsl:value-of select="../n:image-width"/></xsl:attribute>
      <xsl:attribute name="height"><xsl:value-of select="../n:image-height"/></xsl:attribute>
    </object>
  </xsl:template>
  
  <xsl:template match="n:embedded-list-with-hyphen" >
    <ul>
      <xsl:apply-templates/>
    </ul>
  </xsl:template> 

  <xsl:template match="n:embedded-list-item" >
    <li>
      <xsl:apply-templates/>
    </li>
  </xsl:template> 


  <xsl:template name="descriptor-for-blockquote" >
    <xsl:call-template name="descriptor-body" >
      <xsl:with-param name="icon" >/icons/OpeningQuote.png</xsl:with-param>
      <xsl:with-param name="css-class" >opening-quote</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="descriptor-for-pre" >
    <xsl:call-template name="descriptor-body" >
      <xsl:with-param name="icon" >/icons/Gears.png</xsl:with-param>
      <xsl:with-param name="css-class" >opening-pre</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="descriptor-vanilla" >
    <xsl:call-template name="descriptor-body" >
      <xsl:with-param name="icon" />
      <xsl:with-param name="css-class" />
    </xsl:call-template>
  </xsl:template>


  <xsl:template name="descriptor-body" >
    <xsl:param name="icon" />
    <xsl:param name="css-class" />

    <div class="descriptor" >
      <xsl:if test="n:implicit-tag or n:implicit-identifier or n:explicit-identifier or n:location">
        <img class="descriptor-disclosure" src="/icons/Descriptor.png"/>
        <div class="collapsable-descriptor">
          <xsl:if test="n:location">
            <span class="location">
              <xsl:value-of select="n:location"/>
            </span>
          </xsl:if>
          <xsl:if test="n:explicit-identifier">
            <p class="explicit-identifier">
              <xsl:value-of select="n:explicit-identifier"/>
            </p>
          </xsl:if>
          <xsl:if test="n:implicit-identifier">
            <p class="implicit-identifier">
              <xsl:value-of select="n:implicit-identifier"/>
            </p>
          </xsl:if>
          <xsl:if test="n:implicit-tag">
            <ul class="tags">
              <xsl:for-each select="n:implicit-tag">
                <li class="implicit-tag">
                  <xsl:value-of select="."/>
                </li>
              </xsl:for-each>
            </ul>
          </xsl:if>
        </div>

        <xsl:if test="n:explicit-tag">
          <ul class="tags">
            <xsl:for-each select="n:explicit-tag">
              <li class="explicit-tag">
                <xsl:value-of select="."/>
              </li>
            </xsl:for-each>
          </ul>
        </xsl:if>
      </xsl:if>


      <xsl:if test="$icon" >
        <xsl:element name="img" >
          <xsl:attribute name="src" ><xsl:value-of select="$icon" /></xsl:attribute>
          <xsl:attribute name="class" ><xsl:value-of select="$css-class" /></xsl:attribute>
          <xsl:attribute name="alt" />
        </xsl:element>
      </xsl:if>

    </div>
  </xsl:template>


</xsl:stylesheet>
