<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
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
  <xsl:variable
      name="download"
      select="/n:composium/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[1]='DOWNLOAD' ]/n:cell[ 2 ]" 
  />

  <xsl:template match="*[n:style='parameters']" />
  

</xsl:stylesheet>