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
<!-- SVG 1.1 Animation Events Attribute Module ............................. -->
<!-- Animation Events Attribute

        onbegin, onend, onrepeat, onload

     This module defines the AnimationEvents attribute set.
-->

<!ENTITY % SVG.onbegin.attrib
    "onbegin %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onend.attrib
    "onend %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onrepeat.attrib
    "onrepeat %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.onload.attrib
    "onload %Script.datatype; #IMPLIED"
>

<!ENTITY % SVG.AnimationEvents.extra.attrib "" >

<!ENTITY % SVG.AnimationEvents.attrib
    "%SVG.onbegin.attrib;
     %SVG.onend.attrib;
     %SVG.onrepeat.attrib;
     %SVG.onload.attrib;
     %SVG.AnimationEvents.extra.attrib;"
>

<!-- end of svg-animevents-attrib.mod -->
