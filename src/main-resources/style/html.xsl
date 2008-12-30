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
>
  <xsl:import href="punctuation-US-EN.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="encoding"/>


  <xsl:output method="xml" />

  <xsl:template match="/" >

    <html> 
      <head>
        <link rel="stylesheet" type="text/css" href="/display.css" />
        <link rel="stylesheet" type="text/css" href="/custom.css" />

        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$encoding" /></xsl:attribute>
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
    <pre><xsl:apply-templates/></pre>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-plain | n:paragraph-speech-escaped" >
    <p>
    <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="n:url" >
    <a><xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute><xsl:value-of select="." /></a>
  </xsl:template>


  <xsl:template match="n:paragraph-speech" >
    <p>
    &mdash;&nbsp;<xsl:apply-templates/>
    </p>
  </xsl:template>
  
  <xsl:template match="n:emphasis" >
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="n:hard-inline-literal" >
    <code><xsl:apply-templates/></code>
  </xsl:template>

</xsl:stylesheet>
