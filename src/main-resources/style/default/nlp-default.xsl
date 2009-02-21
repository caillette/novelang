<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:output method="text" omit-xml-declaration="yes" indent="no" />

  <xsl:param name="timestamp"/>
  <xsl:param name="wordcount"/>
  <xsl:param name="filename"/>




  <xsl:template match="/" >
%% Generated on: <xsl:value-of select="$timestamp" />    
%%         from: <xsl:value-of select="$filename" />
%%   word count: <xsl:value-of select="//n:meta/n:word-count" />
    <xsl:apply-templates />
  </xsl:template>




  <xsl:template match="n:level" >
<xsl:text> 

</xsl:text>
== <xsl:choose>
      <xsl:when test="n:identifier" ><xsl:text> </xsl:text>
<xsl:apply-templates select="n:identifier" mode="header" /></xsl:when>
      <xsl:when test="n:level-title" > <xsl:apply-templates select="n:level-title" mode="header" /></xsl:when>
    </xsl:choose>
<xsl:apply-templates />
  </xsl:template>



  <xsl:template match="n:level/n:level" >
<xsl:text> 

</xsl:text>
=== <xsl:choose>
    <xsl:when test="n:identifier" ><xsl:text> </xsl:text><xsl:apply-templates select="n:identifier" mode="header" /></xsl:when>
    <xsl:when test="n:level-title" > <xsl:apply-templates select="n:level-title" mode="header" /></xsl:when>
    </xsl:choose>
<xsl:apply-templates />
    </xsl:template>

  <xsl:template match="*" mode="header" >
    <xsl:apply-templates/>
  </xsl:template>


  <xsl:template match="n:paragraphs-inside-angled-bracket-pairs" >

&lt;&lt;<xsl:apply-templates/>

>>

  </xsl:template>

  <xsl:template match="n:lines-of-literal" >

&lt;&lt;&lt;
<xsl:apply-templates/>
>>>
  
  </xsl:template>

  <xsl:template match="n:block-of-literal-inside-grave-accents" >`<xsl:apply-templates/>`</xsl:template>

  <xsl:template match="n:paragraph-regular" >
<xsl:text> 

</xsl:text>
<xsl:apply-templates/>
  </xsl:template>




  <xsl:template match="n:url" >
<xsl:text/>
<xsl:value-of select="text()" />
  </xsl:template>


  <xsl:template match="n:paragraph-as-list-item" >
--- <xsl:apply-templates/></xsl:template>



  <xsl:template match="n:identifier" />
  <xsl:template match="n:level-title" />
  <xsl:template match="n:style" />
  
  <xsl:template match="n:block-inside-double-quotes" >"<xsl:apply-templates/>"</xsl:template>

  <xsl:template match="n:block-inside-solidus-pairs" >//<xsl:apply-templates/>//</xsl:template>

  <xsl:template match="n:block-inside-parenthesis" >(<xsl:apply-templates/>)</xsl:template>

  <xsl:template match="n:block-inside-square-brackets" >[<xsl:apply-templates/>]</xsl:template>

  <xsl:template match="n:block-inside-hyphen-pairs" >-- <xsl:apply-templates/> --</xsl:template>

  <xsl:template match="n:block-inside-two-hyphens-then-hyphen-low-line" >-- <xsl:apply-templates/> -_</xsl:template>

  <xsl:template match="n:apostrophe-wordmate" >'</xsl:template>

  <xsl:template match="n:sign-colon" > :</xsl:template>
  <xsl:template match="n:sign-semicolon" > ;</xsl:template>
  <xsl:template match="n:sign-comma" >,</xsl:template>
  <xsl:template match="n:sign-ellipsis" >...</xsl:template>
  <xsl:template match="n:sign-exclamationmark" > !</xsl:template>
  <xsl:template match="n:sign-fullstop" >.</xsl:template>
  <xsl:template match="n:sign-questionmark" > ?</xsl:template>

  <xsl:template match="text()" >
    <xsl:value-of select="." />
  </xsl:template>


</xsl:stylesheet>

