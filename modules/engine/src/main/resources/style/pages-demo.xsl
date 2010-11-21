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


<!--
    This stylesheet transforms an Opus into pages as defined in
    http://novelang.blogspot.com/2010/11/xsl-mockup-for-multipage-rendering.html
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xslmeta="http://novelang.org/xsl-meta/1.0"
>

  <xsl:template match="/" >

    <xsl:for-each select="/n:opus/n:level">
      <xslmeta:page>
        <name><xsl:value-of select="n:level-title"/></name>
        <path>/opus/level[<xsl:value-of select="position()"/>]</path>
      </xslmeta:page>
    </xsl:for-each>
    
  </xsl:template>

</xsl:stylesheet>
