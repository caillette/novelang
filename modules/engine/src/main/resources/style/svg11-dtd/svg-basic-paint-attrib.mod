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

<!-- ....................................................................... -->
<!-- SVG 1.1 Basic Paint Attribute Module .................................. -->
<!-- Basic Paint Attribute

        fill, fill-rule, stroke, stroke-dasharray, stroke-dashoffset,
        stroke-linecap, stroke-linejoin, stroke-miterlimit, stroke-width, color,
        color-rendering

     This module defines the Paint and Color attribute sets.
-->

<!-- a 'fill' or 'stroke' property/attribute value: <paint> -->
<!ENTITY % Paint.datatype "CDATA" >

<!-- 'stroke-dasharray' property/attribute value (e.g., 'none', list of <number>s) -->
<!ENTITY % StrokeDashArrayValue.datatype "CDATA" >

<!-- 'stroke-dashoffset' property/attribute value (e.g., 'none', <legnth>) -->
<!ENTITY % StrokeDashOffsetValue.datatype "CDATA" >

<!-- 'stroke-miterlimit' property/attribute value (e.g., <number>) -->
<!ENTITY % StrokeMiterLimitValue.datatype "CDATA" >

<!-- 'stroke-width' property/attribute value (e.g., <length>) -->
<!ENTITY % StrokeWidthValue.datatype "CDATA" >

<!ENTITY % SVG.fill.attrib
    "fill %Paint.datatype; #IMPLIED"
>

<!ENTITY % SVG.fill-rule.attrib
    "fill-rule %ClipFillRule.datatype; #IMPLIED"
>

<!ENTITY % SVG.stroke.attrib
    "stroke %Paint.datatype; #IMPLIED"
>

<!ENTITY % SVG.stroke-dasharray.attrib
    "stroke-dasharray %StrokeDashArrayValue.datatype; #IMPLIED"
>

<!ENTITY % SVG.stroke-dashoffset.attrib
    "stroke-dashoffset %StrokeDashOffsetValue.datatype; #IMPLIED"
>

<!ENTITY % SVG.stroke-linecap.attrib
    "stroke-linecap ( butt | round | square | inherit ) #IMPLIED"
>

<!ENTITY % SVG.stroke-linejoin.attrib
    "stroke-linejoin ( miter | round | bevel | inherit ) #IMPLIED"
>

<!ENTITY % SVG.stroke-miterlimit.attrib
    "stroke-miterlimit %StrokeMiterLimitValue.datatype; #IMPLIED"
>

<!ENTITY % SVG.stroke-width.attrib
    "stroke-width %StrokeWidthValue.datatype; #IMPLIED"
>

<!ENTITY % SVG.Paint.extra.attrib "" >

<!ENTITY % SVG.Paint.attrib
    "%SVG.fill.attrib;
     %SVG.fill-rule.attrib;
     %SVG.stroke.attrib;
     %SVG.stroke-dasharray.attrib;
     %SVG.stroke-dashoffset.attrib;
     %SVG.stroke-linecap.attrib;
     %SVG.stroke-linejoin.attrib;
     %SVG.stroke-miterlimit.attrib;
     %SVG.stroke-width.attrib;
     %SVG.Paint.extra.attrib;"
>

<!ENTITY % SVG.color.attrib
    "color %Color.datatype; #IMPLIED"
>

<!ENTITY % SVG.color-rendering.attrib
    "color-rendering ( auto | optimizeSpeed | optimizeQuality | inherit )
                       #IMPLIED"
>

<!ENTITY % SVG.Color.extra.attrib "" >

<!ENTITY % SVG.Color.attrib
    "%SVG.color.attrib;
     %SVG.color-rendering.attrib;
     %SVG.Color.extra.attrib;"
>

<!-- end of svg-basic-paint-attrib.mod -->
