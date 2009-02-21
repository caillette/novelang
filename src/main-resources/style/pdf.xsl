<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
>
  <xsl:import href="default/pdf-default.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>

  <xsl:template match="/" >
    <xsl:apply-imports/>
  </xsl:template>

</xsl:stylesheet>