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
<!-- SVG 1.1 Graphics Attribute Module ..................................... -->
<!-- Graphics Attribute

        display, image-rendering, pointer-events, shape-rendering,
        text-rendering, visibility

     This module defines the Graphics attribute set.
-->

<!ENTITY % SVG.display.attrib
    "display ( inline | block | list-item | run-in | compact | marker |
               table | inline-table | table-row-group | table-header-group |
               table-footer-group | table-row | table-column-group |
               table-column | table-cell | table-caption | none | inherit )
               #IMPLIED"
>

<!ENTITY % SVG.image-rendering.attrib
    "image-rendering ( auto | optimizeSpeed | optimizeQuality | inherit )
                       #IMPLIED"
>

<!ENTITY % SVG.pointer-events.attrib
    "pointer-events ( visiblePainted | visibleFill | visibleStroke | visible |
                      painted | fill | stroke | all | none | inherit )
                      #IMPLIED"
>

<!ENTITY % SVG.shape-rendering.attrib
    "shape-rendering ( auto | optimizeSpeed | crispEdges | geometricPrecision |
                       inherit ) #IMPLIED"
>

<!ENTITY % SVG.text-rendering.attrib
    "text-rendering ( auto | optimizeSpeed | optimizeLegibility |
                      geometricPrecision | inherit ) #IMPLIED"
>

<!ENTITY % SVG.visibility.attrib
    "visibility ( visible | hidden | inherit ) #IMPLIED"
>

<!ENTITY % SVG.Graphics.extra.attrib "" >

<!ENTITY % SVG.Graphics.attrib
    "%SVG.display.attrib;
     %SVG.image-rendering.attrib;
     %SVG.pointer-events.attrib;
     %SVG.shape-rendering.attrib;
     %SVG.text-rendering.attrib;
     %SVG.visibility.attrib;
     %SVG.Graphics.extra.attrib;"
>

<!-- end of svg-graphics-attrib.mod -->
