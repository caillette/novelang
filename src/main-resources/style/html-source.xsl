<?xml version="1.0"?>
<!DOCTYPE foo
[
  <!-- Tweaked entities: we want them to appear verbatim in resulting document. -->

  <!ENTITY mdash  "&amp;amp;mdash;" >
  <!ENTITY ndash  "&amp;amp;ndash;" >
  <!ENTITY hellip "&amp;amp;hellip;" >
  <!ENTITY raquo  "&amp;amp;raquo;" >
  <!ENTITY ldquo  "&amp;amp;ldquo;" >
  <!ENTITY rdquo  "&amp;amp;rdquo;" >
  <!ENTITY rsquo  "&amp;amp;rsquo;" >
  <!ENTITY nbsp   "&amp;amp;nbsp;" >
]
>

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
        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$charset" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" />
        <title><xsl:value-of select="$filename" /></title>
        <style type="text/css" />

      </head>
    <body>

    <xsl:apply-templates />

    </body>
    </html>
  </xsl:template>

  <xsl:template match="n:level" >
    <div class="chapter" >
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="n:level/n:level-title" >
    <h1><xsl:apply-templates /></h1>
  </xsl:template>

  <xsl:template match="n:level/n:level" >
    <div class="section" >
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="n:level/n:level/n:level-title" >
    <h2><xsl:apply-templates /></h2>
  </xsl:template>

  <xsl:template match="n:paragraphs-inside-angled-bracket-pairs" >
<tt>&amp;lt;blockquote&amp;gt;</tt><xsl:apply-templates/><tt>&amp;lt;/blockquote&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:lines-of-literal" >
<tt>&amp;lt;pre&amp;gt;</tt><pre><xsl:value-of select="."/></pre><tt>&amp;lt;/pre&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-regular" >
<tt>&amp;lt;p&amp;gt;</tt><p><xsl:apply-templates/></p><tt>&amp;lt;/p&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:url" >
    <a><xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute><tt>&amp;lt;a href="<xsl:value-of select="." />"&amp;gt;<xsl:value-of select="." />&amp;lt;/a&amp;gt;</tt></a>
  </xsl:template>

  <xsl:template match="n:paragraph-as-list-item" >
<tt>&amp;lt;p&amp;gt;</tt><p><tt>&mdash;&nbsp;</tt><xsl:apply-templates/></p><tt>&amp;lt;/p&amp;gt;</tt>
  </xsl:template>
  


  
  <xsl:template match="n:block-inside-double-quotes" ><tt>&ldquo;</tt><xsl:apply-templates/><tt>&rdquo;</tt></xsl:template>

  <xsl:template match="n:block-inside-solidus-pairs" ><tt>&amp;lt;em&amp;gt;</tt><xsl:apply-templates/><tt>&amp;lt;/em&amp;gt;</tt></xsl:template>

  <xsl:template match="n:word-after-circumflex-accent" >
    <tt>&amp;lt;sup&amp;gt;</tt><xsl:apply-templates/><tt>&amp;lt;/sup&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:block-inside-hyphen-pairs" ><tt>&ndash;&nbsp;</tt><xsl:apply-templates/><tt>&nbsp;&ndash;</tt></xsl:template>

  <xsl:template match="n:block-inside-two-hyphens-then-hyphen-low-line" ><tt>&ndash;&nbsp;</tt><xsl:apply-templates/></xsl:template>

  <xsl:template match="n:block-of-literal-inside-grave-accent-pairs" ><tt>&amp;lt;code&amp;gt;</tt><code><xsl:apply-templates/></code><tt>&amp;lt;/code&amp;gt;</tt></xsl:template>

  <xsl:template match="n:apostrophe-wordmate" ><tt>&rsquo;</tt></xsl:template>


</xsl:stylesheet>
