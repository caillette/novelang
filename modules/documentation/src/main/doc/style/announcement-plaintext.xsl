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

<!--
    Because of some obscure Xalan bug, match="//something" doesn't work and therefore should
    be avoided.
    http://forums.sun.com/thread.jspa?threadID=5134880
    
    Other strange thing may happen
    http://www.mail-archive.com/cocoon-dev@xml.apache.org/msg14438.html
    
-->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:import href="default-novella.xsl" />
  <xsl:import href="shared.xsl" />
  
  <xsl:output method="text" omit-xml-declaration="yes" standalone="yes" />


  <xsl:template name="metadata-header" />
  
  <xsl:template match="n:sign-colon" >:</xsl:template>
  <xsl:template match="n:sign-semicolon" >;</xsl:template>
  <xsl:template match="n:sign-exclamationmark" >!</xsl:template>
  <xsl:template match="n:sign-fullstop" >.</xsl:template>
  <xsl:template match="n:sign-questionmark" >?</xsl:template>

  <!--This doesn't work when put in shared.xsl .-->
  <xsl:template match="*[n:style='parameters']" />

  <xsl:template match="/" >
Just released Novelang-<xsl:value-of select="$version" />!

Summary of changes:<xsl:apply-templates />

Download it from "here"
<xsl:value-of select="$download" />
.

Enjoy!

The Novelang Team

(This email is written in Novelang.)
</xsl:template>

</xsl:stylesheet>
