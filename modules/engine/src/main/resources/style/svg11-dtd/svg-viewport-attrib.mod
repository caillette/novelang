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
<!-- SVG 1.1 Viewport Attribute Module ..................................... -->
<!-- Viewport Attribute

        clip, overflow

     This module defines the Viewport attribute set.
-->

<!-- 'clip' property/attribute value (e.g., 'auto', rect(...)) -->
<!ENTITY % ClipValue.datatype "CDATA" >

<!ENTITY % SVG.clip.attrib
    "clip %ClipValue.datatype; #IMPLIED"
>

<!ENTITY % SVG.overflow.attrib
    "overflow ( visible | hidden | scroll | auto | inherit ) #IMPLIED"
>

<!ENTITY % SVG.Viewport.extra.attrib "" >

<!ENTITY % SVG.Viewport.attrib
    "%SVG.clip.attrib;
     %SVG.overflow.attrib;
     %SVG.Viewport.extra.attrib;"
>

<!-- end of svg-viewport-attrib.mod -->
