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
  <xsl:import href="default-pdf.xsl" />
  <xsl:import href="shared.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  
  
  
  <xsl:variable 
      name="title"      
      select="/n:composium/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[1]='TITLE'    ]/n:cell[ 2 ]"
  />
  <xsl:variable 
      name="subtitle"   
      select="/n:composium/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[1]='SUBTITLE' ]/n:cell[ 2 ]"
  />
  <xsl:variable 
      name="version"    
      select="/n:composium/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[1]='VERSION'  ]/n:cell[ 2 ]"
  />
  <xsl:variable 
      name="author"     
      select="/n:composium/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[1]='AUTHOR'   ]/n:cell[ 2 ]"
  />
  <xsl:variable 
      name="newsFeed"   
      select="/n:composium/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[1]='NEWSFEED' ]/n:cell[ 2 ]" 
  />
  
  <xsl:template match="*[n:style='parameters']" />
  

  <xsl:template match="/" >
    <xsl:apply-imports />
  </xsl:template>
  
  <!--Overriding the one in default-pdf.xsl-->
  <xsl:template name="custom-document-start" >
    <fo:block 
        text-align="center"
        padding-top="40pt"
        padding-bottom="30pt"
    >
      <fo:block
          font-size="28pt"
          padding-bottom="6pt"
      >
        <xsl:value-of select="$title"/>
      </fo:block>
      <fo:block
          font-size="16pt"
          padding-bottom="5pt"
      >
        <xsl:value-of select="$subtitle"/>
      </fo:block>
      <fo:block
          font-size="11pt"
          >
        Written by
        <xsl:value-of select="$author"/>
      </fo:block>
      <fo:block
          font-size="13pt"
      >
        Version <xsl:value-of select="$version"/>
      </fo:block>
    </fo:block>

  </xsl:template>


  <xsl:template match="n:cell-rows-with-vertical-line[../n:style='character-escapes']" >
    
    <fo:table
        border-collapse="collapse"    
    >
      <fo:table-column column-width="70mm" />
      <fo:table-column column-width="30mm" />
      <fo:table-column column-width="20mm" />
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

