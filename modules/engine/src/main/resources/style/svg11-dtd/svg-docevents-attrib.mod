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
<!-- SVG 1.1 Document Events Attribute Module .............................. -->
<!-- Document Events Attribute

        onunload, onabort, onerror, onresize, onscroll, onzoom

     This module defines the DocumentEvents attribute set.
-->

<!ENTITY % SVG.onunload.attrib
    "onunload %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onabort.attrib
    "onabort %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onerror.attrib
    "onerror %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onresize.attrib
    "onresize %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onscroll.attrib
    "onscroll %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onzoom.attrib
    "onzoom %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.DocumentEvents.extra.attrib "" >

<!ENTITY % SVG.DocumentEvents.attrib
    "%SVG.onunload.attrib;
     %SVG.onabort.attrib;
     %SVG.onerror.attrib;
     %SVG.onresize.attrib;
     %SVG.onscroll.attrib;
     %SVG.onzoom.attrib;
     %SVG.DocumentEvents.extra.attrib;"
>

<!-- end of svg-docevents-attrib.mod -->
