<?xml version="1.0"?>


<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
>
  <xsl:output method="text" omit-xml-declaration="yes" standalone="yes" />

  <xsl:template match="/HTML/BODY/FORM/TABLE/TBODY/TR[3]/TD[2]/TABLE/TBODY/TR/TD/TABLE/TBODY/TR[ not( TD/B ) and ( TD[ * ] != '' ) ]" >
    <!--<xsl:if test="not (TD/B)" >-->
      <xsl:value-of select="TD[ 2 ]/A" />=<xsl:value-of select="TD[ 1 ]" /><xsl:text>
</xsl:text>
    <!--</xsl:if>-->
  </xsl:template>

  <xsl:template match="text()|@*" />

  
</xsl:stylesheet>
