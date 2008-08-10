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

  <!ENTITY % Fo PUBLIC
      "http://www.w3.org/1999/XSL/Format"
      "fo.dtd"
  >
  %Fo;


  <!ENTITY legal "Created with Novelang!">

] >

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
>

  <xsl:template match="/" >
    <fo:root>

      <fo:layout-master-set>

        <fo:simple-page-master
            master-name="A4"
            page-width="210mm"   page-height="297mm"
            margin-top="0mm"     margin-bottom="7mm"
            margin-right="25mm"  margin-left="25mm"
        >
          <fo:region-body margin-top="1.1cm" margin-bottom="1.1cm" />
        </fo:simple-page-master>

      </fo:layout-master-set>

      <fo:page-sequence
          initial-page-number="1"
          master-reference="A4"
      >

        <fo:flow
            flow-name="xsl-region-body"
        >
          <fo:block>
            <xsl:apply-templates />
          </fo:block>
        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  <xsl:template match="n:section" >
    <fo:block
        padding-top="20pt"
        text-align="center"
        padding-bottom="5pt"
    >
      <xsl:value-of select="n:title/n:quote" />&nbsp;
      <xsl:value-of select="n:title/n:parenthesis" />&nbsp;
    </fo:block>
    <fo:block
        text-align="center"
    >
      <xsl:attribute name="font-family" ><xsl:value-of select="n:title/n:quotes" /></xsl:attribute>
      <xsl:attribute name="font-style" ><xsl:value-of select="n:title/n:square-brackets[1]" /></xsl:attribute>
      <xsl:attribute name="font-weight" ><xsl:value-of select="n:title/n:square-brackets[2]" /></xsl:attribute>
      <xsl:apply-templates select="n:paragraph-plain" mode="paragraph" />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:paragraph-plain" mode="paragraph" >
    <fo:block>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

</xsl:stylesheet>

