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
<!-- SVG 1.1 Basic Graphics Attribute Module ............................... -->
<!-- Basic Graphics Attribute

        display, visibility

     This module defines the Graphics attribute set.
-->

<!ENTITY % SVG.display.attrib
    "display ( inline | block | list-item | run-in | compact | marker |
               table | inline-table | table-row-group | table-header-group |
               table-footer-group | table-row | table-column-group |
               table-column | table-cell | table-caption | none | inherit )
               #IMPLIED"
>

<!ENTITY % SVG.visibility.attrib
    "visibility ( visible | hidden | inherit ) #IMPLIED"
>

<!ENTITY % SVG.Graphics.extra.attrib "" >

<!ENTITY % SVG.Graphics.attrib
    "%SVG.display.attrib;
     %SVG.visibility.attrib;
     %SVG.Graphics.extra.attrib;"
>

<!-- end of svg-basic-graphics-attrib.mod -->
