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

<!DOCTYPE stylesheet [

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
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
>

  <xsl:template match="n:block-inside-parenthesis" >(<xsl:apply-templates/>)</xsl:template>

  <xsl:template match="n:block-inside-square-brackets" >[<xsl:apply-templates/>]</xsl:template>

  <xsl:template match="n:apostrophe-wordmate" >&rsquo;</xsl:template>
  
  <xsl:template match="n:zero-width-space" >&#x200b;</xsl:template>
  <xsl:template match="n:preserved-whitespace" xml:space="preserve" > </xsl:template>

  <xsl:template match="n:block-after-tilde/n:subblock/n:sign-colon" >:</xsl:template>
  <xsl:template match="n:block-after-tilde/n:subblock/n:sign-semicolon" >;</xsl:template>
  <xsl:template match="n:block-after-tilde/n:subblock/n:sign-ellipsis" >&hellip;</xsl:template>
  <xsl:template match="n:block-after-tilde/n:subblock/n:sign-exclamationmark" >!</xsl:template>
  <xsl:template match="n:block-after-tilde/n:subblock/n:sign-questionmark" >?</xsl:template>


  <xsl:template match="n:meta" />
  <xsl:template match="n:location" />
  <xsl:template match="n:image-width" />
  <xsl:template match="n:image-height" />
  <xsl:template match="n:explicit-tag" />
  <xsl:template match="n:promoted-tag" />
  <xsl:template match="n:implicit-tag" />
  <xsl:template match="n:absolute-identifier" />
  <xsl:template match="n:implicit-identifier" />
  <xsl:template match="n:explicit-identifier" />
  <xsl:template match="n:colliding-explicit-identifier" />

  
</xsl:stylesheet>