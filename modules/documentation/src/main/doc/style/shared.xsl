<!--
  ~ Copyright (C) 2011 Laurent Caillette
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

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:variable 
      name="title"      
      select="/n:opus/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[ 1 ]='TITLE'          ]/n:cell[ 2 ]"
  />
  <xsl:variable 
      name="subtitle"   
      select="/n:opus/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[ 1 ]='SUBTITLE'       ]/n:cell[ 2 ]"
  />
  <xsl:variable 
      name="version"    
      select="/n:opus/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[ 1 ]='VERSION'        ]/n:cell[ 2 ]"
  />
  <xsl:variable 
      name="author"     
      select="/n:opus/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[ 1 ]='AUTHOR'         ]/n:cell[ 2 ]/n:block-of-literal-inside-grave-accents"
  />
  <xsl:variable
      name="copyright-year"
      select="/n:opus/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[ 1 ]='COPYRIGHT-YEAR' ]/n:cell[ 2 ]/n:block-of-literal-inside-grave-accents"
  />
  <xsl:variable
      name="sf-logo"
      select="/n:opus/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[ 1 ]='SF-LOGO'        ]/n:cell[ 2 ]"
  />
  <xsl:variable
      name="newsFeed"   
      select="/n:opus/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[ 1 ]='NEWSFEED'       ]/n:cell[ 2 ]"
  />
  <xsl:variable
      name="download"
      select="/n:opus/n:cell-rows-with-vertical-line[ n:style='parameters' ]/n:cell-row[ n:cell[ 1 ]='DOWNLOAD'       ]/n:cell[ 2 ]"
  />

  <xsl:template match="*[n:style='parameters']" />
  

</xsl:stylesheet>