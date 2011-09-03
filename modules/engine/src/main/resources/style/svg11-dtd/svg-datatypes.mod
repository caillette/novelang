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
<!-- SVG 1.1 Datatypes Module .............................................. -->
<!-- Datatypes

     This module declares common data types for properties and attributes.
-->

<!-- feature specification -->
<!ENTITY % Boolean.datatype "( false | true )" >

<!-- 'clip-rule' or 'fill-rule' property/attribute value -->
<!ENTITY % ClipFillRule.datatype "( nonzero | evenodd | inherit )" >

<!-- media type, as per [RFC2045] -->
<!ENTITY % ContentType.datatype "CDATA" >

<!-- a <coordinate> -->
<!ENTITY % Coordinate.datatype "CDATA" >

<!-- a list of <coordinate>s -->
<!ENTITY % Coordinates.datatype "CDATA" >

<!-- a <color> value -->
<!ENTITY % Color.datatype "CDATA" >

<!-- a <integer> -->
<!ENTITY % Integer.datatype "CDATA" >

<!-- a language code, as per [RFC3066] -->
<!ENTITY % LanguageCode.datatype "NMTOKEN" >

<!-- comma-separated list of language codes, as per [RFC3066] -->
<!ENTITY % LanguageCodes.datatype "CDATA" >

<!-- a <length> -->
<!ENTITY % Length.datatype "CDATA" >

<!-- a list of <length>s -->
<!ENTITY % Lengths.datatype "CDATA" >

<!-- a <number> -->
<!ENTITY % Number.datatype "CDATA" >

<!-- a list of <number>s -->
<!ENTITY % Numbers.datatype "CDATA" >

<!-- opacity value (e.g., <number>) -->
<!ENTITY % OpacityValue.datatype "CDATA" >

<!-- a path data specification -->
<!ENTITY % PathData.datatype "CDATA" >

<!-- 'preserveAspectRatio' attribute specification -->
<!ENTITY % PreserveAspectRatioSpec.datatype "CDATA" >

<!-- script expression -->
<!ENTITY % Script.datatype "CDATA" >

<!-- An SVG color value (RGB plus optional ICC) -->
<!ENTITY % SVGColor.datatype "CDATA" >

<!-- arbitrary text string -->
<!ENTITY % Text.datatype "CDATA" >

<!-- list of transforms -->
<!ENTITY % TransformList.datatype "CDATA" >

<!-- a Uniform Resource Identifier, see [URI] -->
<!ENTITY % URI.datatype "CDATA" >

<!-- 'viewBox' attribute specification -->
<!ENTITY % ViewBoxSpec.datatype "CDATA" >

<!-- end of svg-datatypes.mod -->
