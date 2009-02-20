<?xml version="1.0"?>
<!DOCTYPE foo [

    <!ENTITY % ISOnum PUBLIC
        "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML"
        "../ISOnum.pen"
    >
    %ISOnum;

    <!ENTITY % ISOpub PUBLIC
        "ISO 8879:1986//ENTITIES Publishing//EN//XML"
        "../ISOpub.pen"
    >
    %ISOpub;

    <!ENTITY % ISOlat1 PUBLIC
        "ISO 8879:1986//ENTITIES Added Latin 1//EN//XML"
        "../ISOlat1.pen"
    >
    %ISOlat1;

]>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:import href="default/punctuation-US-EN.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>


  <xsl:output method="xml" />

  <xsl:template match="/" >

    <html> 
      <head>
        <link rel="stylesheet" type="text/css" href="/display.css" />
        <link rel="stylesheet" type="text/css" href="/custom.css" />

        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$charset" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" >
          <xsl:attribute name="novelang-word-count" ><xsl:value-of select="//n:meta/n:word-count" /></xsl:attribute>          
        </meta>

        <title><xsl:value-of select="$filename"/></title>

        <style type="text/css" />


      </head>
    <body>

    <xsl:apply-templates />

    </body>
    </html>
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
      <xsl:apply-templates/>
    </blockquote>
  </xsl:template>

  <xsl:template match="n:lines-of-literal" >
    <pre><xsl:apply-templates/></pre>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-regular" >
    <p>
    <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="n:url" >
    <a><xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute><xsl:value-of select="." /></a>
  </xsl:template>


  <xsl:template match="n:paragraph-as-list-item" >
    <p>
    &mdash;&nbsp;<xsl:apply-templates/>
    </p>
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
    <table>
      <xsl:apply-templates/>
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

</xsl:stylesheet>