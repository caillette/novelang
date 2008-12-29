<?xml version="1.0"?>
<!DOCTYPE foo
[
  <!-- Tweaked entities: we want them to appear verbatim in resulting document. -->
  <!-- See HtmlSink class. -->

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
  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="encoding"/>

  <xsl:output method="xml" />

  <xsl:template match="/" >

    <html> 
      <head>
        <link rel="stylesheet" type="text/css" href="/display.css" />
        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$encoding" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" />
        <meta name= "Copyright" content="Laurent Caillette 2008" />

        <title>Standard HTML skin</title>

        <style type="text/css">
        </style>

      </head>
    <body>

    <xsl:apply-templates />

    </body>
    </html>
  </xsl:template>

  <xsl:template match="n:chapter" >
    <div class="chapter" >
      <xsl:apply-templates/>
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
<tt>&amp;lt;blockquote&amp;gt;</tt><xsl:apply-templates/><tt>&amp;lt;/blockquote&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:literal" >
<tt>&amp;lt;pre&amp;gt;</tt><pre><xsl:value-of select="."/></pre><tt>&amp;lt;/pre&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-plain" >
    <p><xsl:apply-templates/></p>
  </xsl:template>

  <xsl:template match="n:url" >
    <a><xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute><tt>&amp;lt;a href="<xsl:value-of select="." />"&amp;gt;<xsl:value-of select="." />&amp;lt;/a&amp;gt;</tt></a>
  </xsl:template>

  <xsl:template match="n:paragraph-speech" >
    <p><tt>&mdash;&nbsp;</tt><xsl:apply-templates/></p>
  </xsl:template>
  


  
  <xsl:template match="n:quote" ><tt>&ldquo;</tt><xsl:apply-templates/><tt>&rdquo;</tt><xsl:apply-imports/></xsl:template>

  <xsl:template match="n:emphasis" ><tt>&amp;lt;em&amp;gt;</tt><xsl:apply-templates/><tt>&amp;lt;/em&amp;gt;</tt></xsl:template>

  <xsl:template match="n:superscript" >
    <tt>&amp;lt;sup&amp;gt;</tt><xsl:apply-templates/><tt>&amp;lt;/sup&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:parenthesis" >(<xsl:apply-templates/>)</xsl:template>

  <xsl:template match="n:square-brackets" >[<xsl:apply-templates/>]</xsl:template>

  <xsl:template match="n:interpolatedclause" ><tt>&ndash;&nbsp;</tt><xsl:apply-templates/><tt>&nbsp;&ndash;</tt></xsl:template>

  <xsl:template match="n:interpolatedclause-silentend" ><tt>&ndash;&nbsp;</tt><xsl:apply-templates/></xsl:template>

  <xsl:template match="n:hard-inline-literal" ><tt>&amp;lt;code&amp;gt;</tt><code><xsl:apply-templates/></code><tt>&amp;lt;/code&amp;gt;</tt></xsl:template>

  <xsl:template match="n:apostrophe-wordmate" ><tt>&rsquo;</tt></xsl:template>

  <xsl:template match="n:sign-colon" ><tt>&nbsp;</tt>:</xsl:template>
  <xsl:template match="n:sign-semicolon" ><tt>&nbsp;</tt>;</xsl:template>
  <xsl:template match="n:sign-comma" >,</xsl:template>
  <xsl:template match="n:sign-ellipsis" ><tt>&hellip;</tt></xsl:template>
  <xsl:template match="n:sign-exclamationmark" ><tt>&nbsp;</tt>!</xsl:template>
  <xsl:template match="n:sign-fullstop" >.</xsl:template>
  <xsl:template match="n:sign-questionmark" ><tt>&nbsp;</tt>?</xsl:template>

</xsl:stylesheet>
