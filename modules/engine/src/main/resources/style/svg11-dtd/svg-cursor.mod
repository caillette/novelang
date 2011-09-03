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
<!-- SVG 1.1 Cursor Module ................................................. -->
<!-- Cursor

        cursor

     This module declares markup to provide support for cursor.
-->

<!-- 'cursor' property/attribute value (e.g., 'crosshair', <uri>) -->
<!ENTITY % CursorValue.datatype "CDATA" >

<!-- Qualified Names (Default) ......................... -->

<!ENTITY % SVG.cursor.qname "cursor" >

<!-- Attribute Collections (Default) ................... -->

<!ENTITY % SVG.Core.attrib "" >
<!ENTITY % SVG.Conditional.attrib "" >
<!ENTITY % SVG.XLinkRequired.attrib "" >
<!ENTITY % SVG.External.attrib "" >

<!-- SVG.Cursor.class .................................. -->

<!ENTITY % SVG.Cursor.extra.class "" >

<!ENTITY % SVG.Cursor.class
    "| %SVG.cursor.qname; %SVG.Cursor.extra.class;"
>

<!-- SVG.Cursor.attrib ................................. -->

<!ENTITY % SVG.Cursor.extra.attrib "" >

<!ENTITY % SVG.Cursor.attrib
    "cursor %CursorValue.datatype; #IMPLIED
     %SVG.Cursor.extra.attrib;"
>

<!-- cursor: Cursor Element ............................ -->

<!ENTITY % SVG.cursor.extra.content "" >

<!ENTITY % SVG.cursor.element "INCLUDE" >
<![%SVG.cursor.element;[
<!ENTITY % SVG.cursor.content
    "( %SVG.Description.class; %SVG.cursor.extra.content; )*"
>
<!ELEMENT %SVG.cursor.qname; %SVG.cursor.content; >
<!-- end of SVG.cursor.element -->]]>

<!ENTITY % SVG.cursor.attlist "INCLUDE" >
<![%SVG.cursor.attlist;[
<!ATTLIST %SVG.cursor.qname;
    %SVG.Core.attrib;
    %SVG.Conditional.attrib;
    %SVG.XLinkRequired.attrib;
    %SVG.External.attrib;
    x %Coordinate.datatype; #IMPLIED
    y %Coordinate.datatype; #IMPLIED
>
<!-- end of SVG.cursor.attlist -->]]>

<!-- end of svg-cursor.mod -->
