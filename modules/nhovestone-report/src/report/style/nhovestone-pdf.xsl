<?xml version="1.0"?>
<!--
  ~ Copyright (C) 2010 Laurent Caillette
  ~
  ~ This program is free software; you can redistribute it and/or
  ~ modify it under the terms of the GNU Lesser General Public
  ~ License as published by the Free Software Foundation, either
  ~ version 3 of the License, or (at your option) any later version.
  ~  
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU General Public License for more details.
  ~  
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->

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
  <xsl:import href="default-pdf.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  
  <xsl:variable 
      name="generationtimestamp"      
      select="/n:opus/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[1]='GENERATIONTIMESTAMP'    ]/n:cell[ 2 ]"
  />
  
  <xsl:template match="*[n:style='parameters']" />
  

  <xsl:template match="/" >
    <xsl:apply-imports />
  </xsl:template>
  
  <!--Overriding the one in default-pdf.xsl-->
  <xsl:template name="custom-document-start" >
    <fo:block text-align="center" >
      <fo:block
          font-size="28pt"
          padding-top="40pt"
          padding-bottom="10pt"
      >
        Nhovestone report
      </fo:block>

      <fo:block
          font-size="16pt"
          padding-bottom="2pt"
          >
        Version
        <xsl:for-each
            select="/n:opus/n:level[ n:level-title='VERSIONS' ]/n:paragraph-regular/n:embedded-list-with-hyphen/n:embedded-list-item">
          <xsl:if test="position() > 2">, </xsl:if>
          <fo:inline font-weight="bold" ><xsl:value-of select="."/></fo:inline> 
          <xsl:if test="position() = 1"> against </xsl:if>
        </xsl:for-each>
      </fo:block>
    </fo:block>


  </xsl:template>


  <xsl:template match="n:block-inside-square-brackets[text()='JVMCHARACTERISTICS']" >
    <fo:table>
      <fo:table-column column-width="proportional-column-width(1)" />
      <fo:table-column column-width="proportional-column-width(1)" />
      <fo:table-body>
        <xsl:for-each select="/n:opus/n:level[ n:level-title='JVMCHARACTERISTICS' ]/n:cell-rows-with-vertical-line/n:cell-row" >
          <xsl:call-template name="table-row" >
            <xsl:with-param name="cell1" select="n:cell[ 1 ]" />
            <xsl:with-param name="cell2" select="n:cell[ 2 ]" />
          </xsl:call-template>
        </xsl:for-each>
      </fo:table-body>
    </fo:table>
  </xsl:template>
    
  <xsl:template match="n:block-inside-square-brackets[text()='NHOVESTONEPARAMETERS']" >
    <fo:table>
      <fo:table-column column-width="proportional-column-width(1)" />
      <fo:table-column column-width="proportional-column-width(1)" />
      <fo:table-body>
        <xsl:for-each select="/n:opus/n:level[ n:level-title='NHOVESTONEPARAMETERS' ]/n:cell-rows-with-vertical-line/n:cell-row" >
          <xsl:call-template name="table-row" >
            <xsl:with-param name="cell1" select="n:cell[ 1 ]" />
            <xsl:with-param name="cell2" select="n:cell[ 2 ]" />
          </xsl:call-template>
        </xsl:for-each>
      </fo:table-body>
    </fo:table>
  </xsl:template>
    
  
  <xsl:template name="table-row" >
    <xsl:param name="cell1" />
    <xsl:param name="cell2" />
    <fo:table-row>
      <fo:table-cell padding="1pt" border="1pt solid black">
        <fo:block>
          <xsl:value-of select="$cell1" />
        </fo:block>
      </fo:table-cell>
      <fo:table-cell padding="1pt" border="1pt solid black">
        <fo:block>
          <xsl:value-of select="$cell2" />
        </fo:block>
      </fo:table-cell>
    </fo:table-row>        
  </xsl:template>
  
  
  
  <xsl:template match="n:raster-image/n:resource-location" >
    <fo:block>
      <fo:external-graphic>
        <xsl:attribute name="src">.<xsl:value-of select="."/></xsl:attribute>
        <xsl:attribute name="content-width"><xsl:value-of select="440"/></xsl:attribute>
        <xsl:attribute name="content-height"><xsl:value-of select="220"/></xsl:attribute>
      </fo:external-graphic>
    </fo:block>
  </xsl:template>
  
  <xsl:template match="n:level" >
    <fo:block keep-together="always" >
      <xsl:apply-imports />
    </fo:block>
  </xsl:template>
  
  <xsl:template match="n:paragraph-regular" >
    <fo:block keep-together="1" >
      <xsl:apply-imports/>
    </fo:block>
  </xsl:template>
  


</xsl:stylesheet>

