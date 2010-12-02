<?xml version="1.0"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xslmeta="http://novelang.org/xsl-meta/1.0"
>
  <xsl:import href="default-novella.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>

  
  <xslmeta:multipage>

    <xsl:template match="/" >

      <xsl:for-each select="/n:opus/n:level">
        <xslmeta:page>
          <name><xsl:value-of select="n:level-title"/></name>
          <path>/opus/level[<xsl:value-of select="position()"/>]</path>
        </xslmeta:page>
      </xsl:for-each>

    </xsl:template>

  </xslmeta:multipage>


 <xsl:template match="/" >

   [Navigation]
   <xsl:for-each select="/n:opus/n:level">
     <xslmeta:page>
       <name><xsl:value-of select="n:level-title"/></name>
       <path>/opus/level[<xsl:value-of select="position()"/>]</path>
     </xslmeta:page>
   </xsl:for-each>

   [Page]
   <xsl:apply-imports/>
   
 </xsl:template>
  
</xsl:stylesheet>
