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
<!-- SVG 1.1 Style Module .................................................. -->
<!-- Style

        style

     This module declares markup to provide support for stylesheet.
-->

<!-- list of classes -->
<!ENTITY % ClassList.datatype "CDATA" >

<!-- comma-separated list of media descriptors. -->
<!ENTITY % MediaDesc.datatype "CDATA" >

<!-- style sheet data -->
<!ENTITY % StyleSheet.datatype "CDATA" >

<!-- Qualified Names (Default) ......................... -->

<!ENTITY % SVG.style.qname "style" >

<!-- Attribute Collections (Default) ................... -->

<!ENTITY % SVG.Core.attrib "" >

<!-- SVG.Style.class ................................... -->

<!ENTITY % SVG.Style.extra.class "" >

<!ENTITY % SVG.Style.class
    "| %SVG.style.qname; %SVG.Style.extra.class;"
>

<!-- SVG.Style.attrib .................................. -->

<!ENTITY % SVG.Style.extra.attrib "" >

<!ENTITY % SVG.Style.attrib
    "style %StyleSheet.datatype; #IMPLIED
     class %ClassList.datatype; #IMPLIED
     %SVG.Style.extra.attrib;"
>

<!-- style: Style Element .............................. -->

<!ENTITY % SVG.style.extra.content "" >

<!ENTITY % SVG.style.element "INCLUDE" >
<![%SVG.style.element;[
<!ENTITY % SVG.style.content
    "( #PCDATA %SVG.style.extra.content; )*"
>
<!ELEMENT %SVG.style.qname; %SVG.style.content; >
<!-- end of SVG.style.element -->]]>

<!ENTITY % SVG.style.attlist "INCLUDE" >
<![%SVG.style.attlist;[
<!ATTLIST %SVG.style.qname;
    xml:space ( preserve ) #FIXED 'preserve'
    %SVG.Core.attrib;
    type %ContentType.datatype; #REQUIRED
    media %MediaDesc.datatype; #IMPLIED
    title %Text.datatype; #IMPLIED
>
<!-- end of SVG.style.attlist -->]]>

<!-- end of svg-style.mod -->
