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
%%   word count: <xsl:value-of select="$wordcount" />
    <xsl:apply-templates />
  </xsl:template>




  <xsl:template match="n:chapter" >
<xsl:text> 

</xsl:text>
*** <xsl:choose>
      <xsl:when test="n:identifier" ><xsl:text> </xsl:text>
<xsl:apply-templates select="n:identifier" mode="header" /></xsl:when>
      <xsl:when test="n:title" > <xsl:apply-templates select="n:title" mode="header" /></xsl:when>
    </xsl:choose>
<xsl:apply-templates />
  </xsl:template>



  <xsl:template match="n:section" >
<xsl:text> 

</xsl:text>
=== <xsl:choose>
    <xsl:when test="n:identifier" ><xsl:text> </xsl:text><xsl:apply-templates select="n:identifier" mode="header" /></xsl:when>
    <xsl:when test="n:title" > <xsl:apply-templates select="n:title" mode="header" /></xsl:when>
    </xsl:choose>
<xsl:apply-templates />
    </xsl:template>

  <xsl:template match="*" mode="header" >
    <xsl:apply-templates/>
  </xsl:template>


  <xsl:template match="n:blockquote" >

&lt;&lt;<xsl:apply-templates/>

>>

  </xsl:template>

  <xsl:template match="n:literal" >

&lt;&lt;&lt;
<xsl:apply-templates/>
>>>
  
  </xsl:template>

  <xsl:template match="n:soft-inline-literal" >`<xsl:apply-templates/>`</xsl:template>

  <xsl:template match="n:paragraph-plain" >
<xsl:text> 

</xsl:text>
<xsl:apply-templates/>
  </xsl:template>




  <xsl:template match="n:url" >
<xsl:text/>
<xsl:value-of select="text()" />
  </xsl:template>


  <xsl:template match="n:paragraph-speech" >
--- <xsl:apply-templates/></xsl:template>



  <xsl:template match="n:identifier" />
  <xsl:template match="n:title" />
  <xsl:template match="n:style" />
  
  <xsl:template match="n:quote" >"<xsl:apply-templates/>"</xsl:template>

  <xsl:template match="n:emphasis" >//<xsl:apply-templates/>//</xsl:template>

  <xsl:template match="n:parenthesis" >(<xsl:apply-templates/>)</xsl:template>

  <xsl:template match="n:square-brackets" >[<xsl:apply-templates/>]</xsl:template>

  <xsl:template match="n:interpolatedclause" >-- <xsl:apply-templates/> --</xsl:template>

  <xsl:template match="n:interpolatedclause-silentend" >-- <xsl:apply-templates/> -_</xsl:template>

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

