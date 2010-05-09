<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
>
  <xsl:import href="default-nlp.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>

  <xsl:template match="/" >
    <xsl:call-template name="metadata-header" />
    <xsl:apply-imports/>    
  </xsl:template>


</xsl:stylesheet>

