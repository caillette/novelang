<?xml version="1.0"?>
<!DOCTYPE foo [

  <!ENTITY % Fo PUBLIC
      "http://www.w3.org/1999/XSL/Format"
      "fo.dtd"
  >
  %Fo;

] >

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:import href="default/pdf-default.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>

  <xsl:template match="/" >
    <xsl:apply-imports/>
  </xsl:template>


  <xsl:template match="n:cell-rows-with-vertical-line[../n:style='character-escapes']" >
    
    <fo:table
        border-collapse="collapse"    
    >
      <fo:table-column column-width="90mm" />
      <fo:table-column column-width="30mm" />
      <fo:table-column column-width="20mm" />
      <fo:table-column column-width="20mm" />

      <fo:table-header
          font-weight="bold"
          text-align="center"
      >
        <xsl:apply-templates select="*[position() = 1]" mode="header"/>
      </fo:table-header>

      <fo:table-body>
        <xsl:apply-templates select="*[position() > 1]"/>
      </fo:table-body>

    </fo:table>
  </xsl:template>

  <xsl:template match="n:cell-row" >
    <fo:table-row>
      <xsl:apply-templates/>
    </fo:table-row>
  </xsl:template>

  <xsl:template match="n:cell" mode="header" >
    <fo:table-cell padding="2pt" border="1pt solid black">
      <fo:block text-align="center" >
        <xsl:apply-templates />
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <xsl:template match="n:cell" >
    <fo:table-cell padding="2pt" border="1pt solid black">
      <fo:block>
        <xsl:choose>
          <xsl:when test="position()>2" >
            <xsl:attribute name="text-align" >center</xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="text-align" >left</xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates />
      </fo:block>
    </fo:table-cell>
  </xsl:template>


</xsl:stylesheet>

