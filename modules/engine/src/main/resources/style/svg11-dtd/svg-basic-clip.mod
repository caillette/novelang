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
<!-- SVG 1.1 Basic Clip Module ............................................. -->
<!-- Basic Clip

        clipPath

     This module declares markup to provide support for clipping.
-->

<!-- 'clip-path' property/attribute value (e.g., 'none', <uri>) -->
<!ENTITY % ClipPathValue.datatype "CDATA" >

<!-- Qualified Names (Default) ......................... -->

<!ENTITY % SVG.clipPath.qname "clipPath" >

<!-- Attribute Collections (Default) ................... -->

<!ENTITY % SVG.Core.attrib "" >
<!ENTITY % SVG.Conditional.attrib "" >
<!ENTITY % SVG.Style.attrib "" >
<!ENTITY % SVG.Text.attrib "" >
<!ENTITY % SVG.TextContent.attrib "" >
<!ENTITY % SVG.Font.attrib "" >
<!ENTITY % SVG.Paint.attrib "" >
<!ENTITY % SVG.Color.attrib "" >
<!ENTITY % SVG.Opacity.attrib "" >
<!ENTITY % SVG.Graphics.attrib "" >
<!ENTITY % SVG.Mask.attrib "" >
<!ENTITY % SVG.Filter.attrib "" >
<!ENTITY % SVG.Cursor.attrib "" >
<!ENTITY % SVG.External.attrib "" >

<!-- SVG.Clip.class .................................... -->

<!ENTITY % SVG.Clip.extra.class "" >

<!ENTITY % SVG.Clip.class
    "| %SVG.clipPath.qname; %SVG.Clip.extra.class;"
>

<!-- SVG.Clip.attrib ................................... -->

<!ENTITY % SVG.Clip.extra.attrib "" >

<!ENTITY % SVG.Clip.attrib
    "clip-path %ClipPathValue.datatype; #IMPLIED
     clip-rule %ClipFillRule.datatype; #IMPLIED
     %SVG.Clip.extra.attrib;"
>

<!-- clipPath: Clip Path Element ....................... -->

<!ENTITY % SVG.clipPath.extra.content "" >

<!ENTITY % SVG.clipPath.element "INCLUDE" >
<![%SVG.clipPath.element;[
<!ENTITY % SVG.clipPath.content
    "(( %SVG.Description.class; )*, (( %SVG.Animation.class; %SVG.Use.class;
        %SVG.clipPath.extra.content; )*, ( %SVG.Animation.class; %SVG.Use.class;
        %SVG.Shape.class; )?, ( %SVG.Animation.class; %SVG.Use.class;
        %SVG.clipPath.extra.content; )*))"
>
<!ELEMENT %SVG.clipPath.qname; %SVG.clipPath.content; >
<!-- end of SVG.clipPath.element -->]]>

<!ENTITY % SVG.clipPath.attlist "INCLUDE" >
<![%SVG.clipPath.attlist;[
<!ATTLIST %SVG.clipPath.qname;
    %SVG.Core.attrib;
    %SVG.Conditional.attrib;
    %SVG.Style.attrib;
    %SVG.Text.attrib;
    %SVG.TextContent.attrib;
    %SVG.Font.attrib;
    %SVG.Paint.attrib;
    %SVG.Color.attrib;
    %SVG.Opacity.attrib;
    %SVG.Graphics.attrib;
    %SVG.Clip.attrib;
    %SVG.Mask.attrib;
    %SVG.Filter.attrib;
    %SVG.Cursor.attrib;
    %SVG.External.attrib;
    transform %TransformList.datatype; #IMPLIED
    clipPathUnits ( userSpaceOnUse | objectBoundingBox ) #IMPLIED
>
<!-- end of SVG.clipPath.attlist -->]]>

<!-- end of svg-basic-clip.mod -->
