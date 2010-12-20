<?xml version="1.0"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xslmeta="http://novelang.org/xsl-meta/1.0"
>
  <xsl:import href="default-novella.xsl" />

  <xsl:param name="timestamp" />
  <xsl:param name="filename" />
  <xsl:param name="charset" />

  
  <xslmeta:multipage>

    <xsl:template match="/" >

      <n:pages>
        <xsl:for-each select="/n:opus/n:level">
          <n:page>
            <n:page-identifier>
              <xsl:value-of select="n:level-title"/>
            </n:page-identifier>
            <n:page-path>/opus/level[<xsl:value-of select="position()"/>]</n:page-path>
          </n:page>
        </xsl:for-each>
      </n:pages>

    </xsl:template>

  </xslmeta:multipage>


 <xsl:template match="/" >

   <!--Doesn't try to simulate real navigation bar.-->

   [Navigation]
   <xsl:for-each select="/n:meta/n:page">
     Page:<xsl:value-of select="n:page-identifier"/>
   </xsl:for-each>

   [Whole opus]
   <xsl:apply-templates/>
   
 </xsl:template>




</xsl:stylesheet>
