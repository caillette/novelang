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

<!DOCTYPE foo [

  <!ENTITY % ISOnum PUBLIC
      "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML"
      "ISOnum.pen"
  >
  %ISOnum;

  <!ENTITY % ISOpub PUBLIC
      "ISO 8879:1986//ENTITIES Publishing//EN//XML"
      "ISOpub.pen"
  >
  %ISOpub;

  <!ENTITY % ISOlat1 PUBLIC
      "ISO 8879:1986//ENTITIES Added Latin 1//EN//XML"
      "ISOlat1.pen"
  >
  %ISOlat1;
    
] >

<!--
    This stylesheet is designed to work along with some Xalan restrictions when ran as 
    an Ant task.
    
    Because of some obscure Xalan bug, match="//something" doesn't always work and therefore 
    should be avoided.
    http://forums.sun.com/thread.jspa?threadID=5134880
    
    Other strange thing may happen with imports and so on.
    http://www.mail-archive.com/cocoon-dev@xml.apache.org/msg14438.html
    
-->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:import href="simple-html.xsl" />
  <xsl:import href="shared.xsl" />
  
  <xsl:output method="xml" />

  <xsl:template match="/" >
    <html>
      <body>
        <p>Just released Novelang-<xsl:value-of select="$version" />!</p>
        <p>Summary of changes:</p>
        <xsl:apply-templates />
        <p>Download it from <a><xsl:attribute name="href" ><xsl:value-of select="$download" /></xsl:attribute>here</a>.</p>
        <p>Enjoy!</p>
      </body>
    </html>

  </xsl:template>


  <xsl:template match="n:paragraph-regular" >
    <p>
      <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="n:list-with-triple-hyphen" >
    <ul>
      <xsl:for-each select="n:paragraph-as-list-item" >
        <li><xsl:apply-templates/></li>
      </xsl:for-each>
    </ul>
  </xsl:template>




  <xsl:template match="n:level" >
    <p>
      <strong><xsl:apply-templates/></strong>
    </p>
  </xsl:template>

  <xsl:template match="//n:block-inside-square-brackets/n:block-of-literal-inside-grave-accent-pairs" >
    <code><xsl:value-of select="." /></code>
  </xsl:template>  
  
  
  <!--This doesn't work when put in shared.xsl .-->
  <xsl:template match="*[n:style='parameters']" />
</xsl:stylesheet>
