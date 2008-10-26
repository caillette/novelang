<?xml version="1.0"?>
<xsl:stylesheet
    version = "1.0"
    xmlns:xsl = "http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:nf="http://novelang.org/font-list-xml/1.0"
>

  <xsl:output method = "xml" />

  <xsl:template match = "*" >
    <xsl:element name = "{name(.)}" >
      <xsl:for-each select = "@*" >
        <xsl:attribute name = "{name(.)}" >
          <xsl:value-of select = "." />
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>