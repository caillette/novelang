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
<!-- SVG 1.1 Graphical Element Events Attribute Module ..................... -->
<!-- Graphical Element Events Attribute

        onfocusin, onfocusout, onactivate, onclick, onmousedown, onmouseup,
        onmouseover, onmousemove, onmouseout, onload

     This module defines the GraphicalEvents attribute set.
-->

<!ENTITY % SVG.onfocusin.attrib
    "onfocusin %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onfocusout.attrib
    "onfocusout %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onactivate.attrib
    "onactivate %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onclick.attrib
    "onclick %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onmousedown.attrib
    "onmousedown %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onmouseup.attrib
    "onmouseup %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onmouseover.attrib
    "onmouseover %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onmousemove.attrib
    "onmousemove %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onmouseout.attrib
    "onmouseout %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onload.attrib
    "onload %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.GraphicalEvents.extra.attrib "" >

<!ENTITY % SVG.GraphicalEvents.attrib
    "%SVG.onfocusin.attrib;
     %SVG.onfocusout.attrib;
     %SVG.onactivate.attrib;
     %SVG.onclick.attrib;
     %SVG.onmousedown.attrib;
     %SVG.onmouseup.attrib;
     %SVG.onmouseover.attrib;
     %SVG.onmousemove.attrib;
     %SVG.onmouseout.attrib;
     %SVG.onload.attrib;
     %SVG.GraphicalEvents.extra.attrib;"
>

<!-- end of svg-graphevents-attrib.mod -->
