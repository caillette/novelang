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
<!-- SVG 1.1 Core Attribute Module ......................................... -->
<!-- Core Attribute

        id, xml:base, xml:lang, xml:space

     This module defines the core set of attributes that can be present on
     any element.
-->

<!ENTITY % SVG.id.attrib
    "id ID #IMPLIED"
>

<!ENTITY % SVG.base.attrib
    "xml:base %URI.datatype; #IMPLIED"
>

<!ENTITY % SVG.lang.attrib
    "xml:lang %LanguageCode.datatype; #IMPLIED"
>

<!ENTITY % SVG.space.attrib
    "xml:space ( default | preserve ) #IMPLIED"
>

<!ENTITY % SVG.Core.extra.attrib "" >

<!ENTITY % SVG.Core.attrib
    "%SVG.id.attrib;
     %SVG.base.attrib;
     %SVG.lang.attrib;
     %SVG.space.attrib;
     %SVG.Core.extra.attrib;"
>

<!-- end of svg-core-attrib.mod -->
