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

  <xalan:component
      prefix="colormapper"
      functions="getColorName getInverseRgbDeclaration"
  >
      <xalan:script lang="javaclass" src="xalan://novelang.rendering.xslt.color.ColorMapper" />
  </xalan:component>


  <xsl:output method="xml" />

  <xsl:template match="/" >

    <html> 
      <head>
        <link rel="stylesheet" type="text/css" href="/display.css" />
        <link rel="stylesheet" type="text/css" href="/custom.css" />
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
        <script type="text/javascript" src="/javascript/color-palette.js" />
        <script type="text/javascript" src="/javascript/tags.js" />
        <script type="text/javascript"> //<![CDATA[

          $( document ).ready( function() {
            initializeTagSystem( "/javascript/colors.htm" ) ;
          } ) ;
        //]]></script>


      </head>
    <body>

      <xsl:apply-templates />

      <xsl:if test="//n:meta/n:tags" >
        <hr/>
        <dl id="tag-definitions" >
          <xsl:for-each select="//n:meta/n:tags/*">
            <dt><xsl:value-of select="." /></dt>
          </xsl:for-each>
        </dl>
      </xsl:if>

      <p id="tag-list" >
        Activate / deactivate tags here:<br/>
      </p>

      <p id="messages" />
      <div id="externalColorDefinitionsPlaceholder" style="visibility:hidden;" />

    </body>
    </html>
  </xsl:template>

  <xsl:template match="n:level" >
    <div class="tag-scope" >
      <xsl:call-template name="tags" />
      <xsl:apply-templates />
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
    <div class="tag-scope" >
      <xsl:call-template name="tags" />
      <blockquote>
        <xsl:apply-templates />
      </blockquote>
    </div>

  </xsl:template>

  <xsl:template match="n:lines-of-literal" >
    <pre><xsl:apply-templates/></pre>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-regular" >
    <div class="tag-scope" >
      <xsl:call-template name="tags" />
      <p>
        <xsl:apply-templates />
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
    <div class="tag-scope" >
      <xsl:call-template name="tags" />
      <p>
        &mdash;&nbsp;
        <xsl:apply-templates />
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



  <xsl:template match="n:cell-rows-with-vertical-line" >
    <div class="tag-scope">
      <xsl:call-template name="tags" />
      <table>
        <xsl:apply-templates />
      </table>
    </div>

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


  <xsl:template name="tags" >
    <xsl:if test="n:tag" >
      <ul class="tags" >
        <xsl:for-each select="n:tag" >
          <!--<xsl:variable name="inverse" select="colormapper:getInverseRgbDeclaration( . )" />-->
          <li>
            <!--<xsl:attribute name="style" >-->
              <!--background-color:<xsl:value-of select="colormapper:getColorName( . )" /> ;-->
              <!--color:<xsl:value-of select="$inverse" /> ;-->
              <!--border-color:<xsl:value-of select="$inverse" />-->
            <!--</xsl:attribute>-->
            <xsl:value-of select="." />
          </li>
        </xsl:for-each>
      </ul>
      <br/>
    </xsl:if>
  </xsl:template>


</xsl:stylesheet>
