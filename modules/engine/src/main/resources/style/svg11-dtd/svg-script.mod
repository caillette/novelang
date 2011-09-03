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
<!-- SVG 1.1 Scripting Module .............................................. -->
<!-- Scripting

        script

     This module declares markup to provide support for scripting.
-->

<!-- Qualified Names (Default) ......................... -->

<!ENTITY % SVG.script.qname "script" >

<!-- Attribute Collections (Default) ................... -->

<!ENTITY % SVG.Core.attrib "" >
<!ENTITY % SVG.XLink.attrib "" >
<!ENTITY % SVG.External.attrib "" >

<!-- SVG.Script.class .................................. -->

<!ENTITY % SVG.Script.extra.class "" >

<!ENTITY % SVG.Script.class
    "| %SVG.script.qname; %SVG.Script.extra.class;"
>

<!-- script: Script Element ............................ -->

<!ENTITY % SVG.script.extra.content "" >

<!ENTITY % SVG.script.element "INCLUDE" >
<![%SVG.script.element;[
<!ENTITY % SVG.script.content
    "( #PCDATA %SVG.script.extra.content; )*"
>
<!ELEMENT %SVG.script.qname; %SVG.script.content; >
<!-- end of SVG.script.element -->]]>

<!ENTITY % SVG.script.attlist "INCLUDE" >
<![%SVG.script.attlist;[
<!ATTLIST %SVG.script.qname;
    %SVG.Core.attrib;
    %SVG.XLink.attrib;
    %SVG.External.attrib;
    type %ContentType.datatype; #REQUIRED
>
<!-- end of SVG.script.attlist -->]]>

<!-- end of svg-script.mod -->
