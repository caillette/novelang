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
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
>

  <xsl:template match="n:block-inside-parenthesis" >(<xsl:apply-templates/>)</xsl:template>

  <xsl:template match="n:block-inside-square-brackets" >[<xsl:apply-templates/>]</xsl:template>

  <xsl:template match="n:apostrophe-wordmate" >&rsquo;</xsl:template>
  
  <xsl:template match="n:zero-width-space" >&#x200b;</xsl:template>
  <xsl:template match="n:preserved-whitespace" xml:space="preserve" > </xsl:template>

  <xsl:template match="n:sign-comma" >,</xsl:template>
  <xsl:template match="n:sign-fullstop" >.</xsl:template>

  <xsl:template match="n:meta" />
  <xsl:template match="n:image-width" />
  <xsl:template match="n:image-height" />
  <xsl:template match="n:tag" />

  
</xsl:stylesheet>