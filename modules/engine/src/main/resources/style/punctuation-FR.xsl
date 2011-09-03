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

    <!ENTITY % ISOpub PUBLIC
        "ISO 8879:1986//ENTITIES Publishing//EN//XML"
        "ISOpub.pen"
    >
    %ISOpub;

    <!ENTITY % ISOnum PUBLIC
        "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML"
        "ISOnum.pen"
    >
    %ISOnum;


] >
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>

  <xsl:import href="general-punctuation.xsl" />

  <xsl:template match="n:block-inside-hyphen-pairs" >&mdash;&nbsp;<xsl:apply-templates/>&nbsp;&mdash;</xsl:template>

  <xsl:template match="n:block-inside-two-hyphens-then-hyphen-low-line" >&mdash;&nbsp;<xsl:apply-templates/></xsl:template>

  <xsl:template match="n:block-inside-double-quotes" >&laquo;&nbsp;<xsl:apply-templates/>&nbsp;&raquo;</xsl:template>

  <xsl:template match="n:sign-colon" >&nbsp;:</xsl:template>
  <xsl:template match="n:sign-semicolon" >&nbsp;;</xsl:template>
  <xsl:template match="n:sign-ellipsis" >&hellip;</xsl:template>
  <xsl:template match="n:sign-exclamationmark" >&nbsp;!</xsl:template>
  <xsl:template match="n:sign-questionmark" >&nbsp;?</xsl:template>


</xsl:stylesheet>