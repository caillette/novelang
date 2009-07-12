<?xml version="1.0"?>

<!DOCTYPE stylesheet [

    <!ENTITY % ISOpub PUBLIC
        "ISO 8879:1986//ENTITIES Publishing//EN//XML"
        "ISOpub.pen"
    >
    %ISOpub;

    <!ENTITY % ISOnum PUBLIC
        "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML"
        "ISOnum.pen"
    >
    %ISOnum;
    
] >
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>

  <xsl:import href="general-punctuation.xsl" />

  <xsl:template match="n:block-inside-double-quotes" >&ldquo;<xsl:apply-templates/>&rdquo;</xsl:template>

  <xsl:template match="n:block-inside-hyphen-pairs" >&ndash;&nbsp;<xsl:apply-templates/>&nbsp;&ndash;</xsl:template>

  <xsl:template match="n:block-inside-two-hyphens-then-hyphen-low-line" >&ndash;&nbsp;<xsl:apply-templates/></xsl:template>

  <xsl:template match="n:sign-colon" >:</xsl:template>
  <xsl:template match="n:sign-semicolon" >;</xsl:template>
  <xsl:template match="n:sign-ellipsis" >&hellip;</xsl:template>
  <xsl:template match="n:sign-exclamationmark" >!</xsl:template>
  <xsl:template match="n:sign-questionmark" >?</xsl:template>

</xsl:stylesheet>