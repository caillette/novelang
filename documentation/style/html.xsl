<?xml version="1.0"?>
<!DOCTYPE foo
[
  <!-- Tweaked entities: we want them to appear verbatim in resulting document. -->
  <!-- See HtmlSink class. -->

  <!ENTITY mdash  "&amp;mdash;" >
  <!ENTITY ndash  "&amp;ndash;" >
  <!ENTITY hellip "&amp;hellip;" >
  <!ENTITY raquo  "&amp;raquo;" >
  <!ENTITY ldquo  "&amp;ldquo;" >
  <!ENTITY rdquo  "&amp;rdquo;" >
  <!ENTITY rsquo  "&amp;rsquo;" >
  <!ENTITY nbsp   "&amp;nbsp;" >
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
        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$encoding" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" />
        <meta name= "Copyright" content="Laurent Caillette 2008" />
        <title>Novelang, the simplest word processor</title>

        <meta name="viewport" content="width=700, initial-scale=0.45, minimum-scale=0.45" />
        <link rel="stylesheet" type="text/css" href="screen.css" />
      </head>
    <body>
    <div id="Box">

      <div id="Title" >Novelang</div>

      <xsl:apply-templates />


      <div id="Sidebar">

      <p>By <strong>Laurent Caillette</strong></p>

      <ul><!--&#9733;-->
      <li><a href="http://www.gnu.org/licenses/gpl-3.0.txt">License</a></li>
      <li><a href="http://novelang.sf.net">Sourceforge</a></li>
      <li><a href="http://github.com/caillette/novelang/tree/master">Sources</a></li>
      <li><a href="/feeds/">Atom / RSS</a></li>
      </ul>


      </div> <!-- Sidebar -->
    </div> <!-- Box -->


    </body>
    </html>
  </xsl:template>

  <xsl:template match="n:chapter" >
    <div class="chapter" >
    <xsl:choose>
      <xsl:when test="n:style[text()='standalone']" >
        <xsl:call-template name="standalone" />
      </xsl:when>
      <xsl:when test="n:style[text()='all-emphasized']" >
        <xsl:call-template name="all-emphasized" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates />
      </xsl:otherwise>
    </xsl:choose>
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

  <xsl:template name="standalone" >
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template name="all-emphasized" >
    <i>
      <xsl:apply-templates />
    </i>
  </xsl:template>

  <xsl:template match="n:blockquote" >
    <blockquote>
      <xsl:apply-templates/>
    </blockquote>
  </xsl:template>

  <xsl:template match="n:litteral" >
    <pre>
      <xsl:apply-templates/>
    </pre>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-plain | n:paragraph-speech-escaped" >
    <p>
    <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="n:url" >
    <p>
    <a><xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute><xsl:value-of select="." /></a>
    </p>
  </xsl:template>

  <xsl:template match="n:paragraph-speech" >
    <xsl:call-template name="speech" >
      <xsl:with-param name="speech-symbol" >&mdash;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="n:paragraph-speech-continued" >
    <xsl:call-template name="speech" >
      <xsl:with-param name="speech-symbol" >&raquo;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="speech" >
    <xsl:param name = "speech-symbol" />
    <p>
    <xsl:if test="n:locutor" >
      <xsl:value-of select="n:locutor" />&nbsp;:
    </xsl:if>
    <xsl:value-of select="$speech-symbol" />&nbsp;
    <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="n:locutor" />

  
  <xsl:template match="n:quote" >&ldquo;<xsl:apply-templates/>&rdquo;</xsl:template>

  <xsl:template match="n:emphasis" >
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="n:parenthesis" >(<xsl:apply-templates/>)</xsl:template>

  <xsl:template match="n:square-brackets" >[<xsl:apply-templates/>]</xsl:template>

  <xsl:template match="n:interpolatedclause" >&ndash;&nbsp;<xsl:apply-templates/>&nbsp;&ndash;</xsl:template>

  <xsl:template match="n:interpolatedclause-silentend" >&ndash;&nbsp;<xsl:apply-templates/></xsl:template>

  <xsl:template match="n:ellipsis-opening" >&hellip;</xsl:template>

  <xsl:template match="n:apostrophe-wordmate" >&rsquo;</xsl:template>

  <xsl:template match="n:sign-colon" >&nbsp;:</xsl:template>
  <xsl:template match="n:sign-semicolon" >&nbsp;;</xsl:template>
  <xsl:template match="n:sign-comma" >,</xsl:template>
  <xsl:template match="n:sign-ellipsis" >&hellip;</xsl:template>
  <xsl:template match="n:sign-exclamationmark" >&nbsp;!</xsl:template>
  <xsl:template match="n:sign-fullstop" >.</xsl:template>
  <xsl:template match="n:sign-questionmark" >&nbsp;?</xsl:template>


  
</xsl:stylesheet>
