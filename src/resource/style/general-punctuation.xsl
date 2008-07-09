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


  <xsl:template match="n:quote" >&ldquo;<xsl:apply-templates/>&rdquo;</xsl:template>

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