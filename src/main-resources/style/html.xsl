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
        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$encoding" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" />

        <title>Standard HTML skin</title>

        <style type="text/css">


body {
  width : 400pt ;
  background : #dddddd ;
#  font-family : Georgia ;
}

h1 {
  font-size : 21pt ;
}

h2 {
  font-size : 15pt ;
}

div.chapter {
  margin-top : 25pt ;
  margin-bottom : 5pt ;
}

div.section {
  margin-top : 15pt ;
  margin-bottom : 3pt ;
}

p {
  font-size : 13.5pt ;
	text-indent : 1em ;
	text-align : justify ;
	line-height : 1.30 ;
	margin : 1pt 0pt 0pt 15pt;
	padding : 0pt ;
}

blockquote > p {
  font-size : 12.5pt ;
	text-indent : 0em ;
	text-align : justify ;
	line-height : 1.25 ;
	margin : 1pt 0pt 0pt 10pt;
	padding : 0pt ;
}
        </style>

      </head>
    <body>

    <!--<p>Encoding = <xsl:value-of select="$encoding" /></p>-->

    <xsl:apply-templates />

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
    <xsl:value-of select="$speech-symbol" />&nbsp;
    <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="n:emphasis" >
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="n:hard-inline-literal" >
    <tt><xsl:apply-templates/></tt>
  </xsl:template>

</xsl:stylesheet>
