<?xml version="1.0"?>
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
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:nlx="xalan://org.novelang.rendering.xslt"
>

  <xsl:variable name="webColor" select="nlx:WebColor.new()" />

  <xsl:template match="/" >



    <html>
      <body>
        <p>
          Foreground: <xsl:value-of select="nlx:foreground( $webColor )" /><br/>
          Background: <xsl:value-of select="nlx:background( $webColor )" />
          <xsl:value-of select="nlx:next( $webColor )" />
        </p>
        <p>
          Foreground: <xsl:value-of select="nlx:foreground( $webColor )" /><br/>
          Background: <xsl:value-of select="nlx:background( $webColor )" />
          <xsl:value-of select="nlx:next( $webColor )" />
        </p>
      </body>

    </html>

  </xsl:template>


</xsl:stylesheet>
