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


<!-- Used by XslParametersTest, edit with care. -->

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:nlx="xalan://org.novelang.rendering.xslt.Numbering"
 >
  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>
  <xsl:param name="rendition-kinematic"/>
  <xsl:param name="content-directory"/>


  <xsl:template match="/" >
    <p>
    timestamp=<xsl:value-of select="$timestamp" />;
    filename=<xsl:value-of select="$filename" />;
    charset=<xsl:value-of select="$charset" />;
    content-root=<xsl:value-of select="$content-directory" />;
    </p>
  </xsl:template>



</xsl:stylesheet>

