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
    
] >

<!--
    Because of some obscure Xalan bug, match="//something" doesn't work and therefore should
    be avoided.
    http://forums.sun.com/thread.jspa?threadID=5134880
-->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:import href="default-nlp.xsl" />
  <xsl:import href="shared.xsl" />
  
  <xsl:output method="text" />

  <xsl:template match="/" >
    <xsl:apply-imports />
  </xsl:template>

  <xsl:template name="metadata-header" />
  
  <xsl:template match="n:sign-colon" >:</xsl:template>
  <xsl:template match="n:sign-semicolon" >;</xsl:template>
  <xsl:template match="n:sign-exclamationmark" >!</xsl:template>
  <xsl:template match="n:sign-fullstop" >.</xsl:template>
  <xsl:template match="n:sign-questionmark" >?</xsl:template>

  <!--This doesn't work when put in shared.xsl .-->
  <xsl:template match="*[n:style='parameters']" />
</xsl:stylesheet>
