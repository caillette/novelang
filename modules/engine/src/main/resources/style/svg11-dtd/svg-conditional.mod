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
<!-- SVG 1.1 Conditional Processing Module ................................. -->
<!-- Conditional Processing

        switch

     This module declares markup to provide support for conditional processing.
-->

<!-- extension list specification -->
<!ENTITY % ExtensionList.datatype "CDATA" >

<!-- feature list specification -->
<!ENTITY % FeatureList.datatype "CDATA" >

<!-- Qualified Names (Default) ......................... -->

<!ENTITY % SVG.switch.qname "switch" >

<!-- Attribute Collections (Default) ................... -->

<!ENTITY % SVG.Core.attrib "" >
<!ENTITY % SVG.Container.attrib "" >
<!ENTITY % SVG.Style.attrib "" >
<!ENTITY % SVG.Viewport.attrib "" >
<!ENTITY % SVG.Text.attrib "" >
<!ENTITY % SVG.TextContent.attrib "" >
<!ENTITY % SVG.Font.attrib "" >
<!ENTITY % SVG.Paint.attrib "" >
<!ENTITY % SVG.Color.attrib "" >
<!ENTITY % SVG.Opacity.attrib "" >
<!ENTITY % SVG.Graphics.attrib "" >
<!ENTITY % SVG.Marker.attrib "" >
<!ENTITY % SVG.ColorProfile.attrib "" >
<!ENTITY % SVG.Gradient.attrib "" >
<!ENTITY % SVG.Clip.attrib "" >
<!ENTITY % SVG.Mask.attrib "" >
<!ENTITY % SVG.Filter.attrib "" >
<!ENTITY % SVG.FilterColor.attrib "" >
<!ENTITY % SVG.GraphicalEvents.attrib "" >
<!ENTITY % SVG.Cursor.attrib "" >
<!ENTITY % SVG.External.attrib "" >

<!-- SVG.Conditional.class ............................. -->

<!ENTITY % SVG.Conditional.extra.class "" >

<!ENTITY % SVG.Conditional.class
    "| %SVG.switch.qname; %SVG.Conditional.extra.class;"
>

<!-- SVG.Conditional.attrib ............................ -->

<!ENTITY % SVG.Conditional.extra.attrib "" >

<!ENTITY % SVG.Conditional.attrib
    "requiredFeatures %FeatureList.datatype; #IMPLIED
     requiredExtensions %ExtensionList.datatype; #IMPLIED
     systemLanguage %LanguageCodes.datatype; #IMPLIED
     %SVG.Conditional.extra.attrib;"
>

<!-- SVG.Presentation.attrib ........................... -->

<!ENTITY % SVG.Presentation.extra.attrib "" >

<!ENTITY % SVG.Presentation.attrib
    "%SVG.Container.attrib;
     %SVG.Viewport.attrib;
     %SVG.Text.attrib;
     %SVG.TextContent.attrib;
     %SVG.Font.attrib;
     %SVG.Paint.attrib;
     %SVG.Color.attrib;
     %SVG.Opacity.attrib;
     %SVG.Graphics.attrib;
     %SVG.Marker.attrib;
     %SVG.ColorProfile.attrib;
     %SVG.Gradient.attrib;
     %SVG.Clip.attrib;
     %SVG.Mask.attrib;
     %SVG.Filter.attrib;
     %SVG.FilterColor.attrib;
     %SVG.Cursor.attrib;
     flood-color %SVGColor.datatype; #IMPLIED
     flood-opacity %OpacityValue.datatype; #IMPLIED
     lighting-color %SVGColor.datatype; #IMPLIED
     %SVG.Presentation.extra.attrib;"
>

<!-- switch: Switch Element ............................ -->

<!ENTITY % SVG.switch.extra.content "" >

<!ENTITY % SVG.switch.element "INCLUDE" >
<![%SVG.switch.element;[
<!ENTITY % SVG.switch.content
    "(( %SVG.Description.class; )*, ( %SVG.svg.qname; | %SVG.g.qname;
      | %SVG.use.qname; | %SVG.text.qname; | %SVG.Animation.class;
        %SVG.Conditional.class; %SVG.Image.class; %SVG.Shape.class;
        %SVG.Hyperlink.class; %SVG.Extensibility.class;
        %SVG.switch.extra.content; )*)"
>
<!ELEMENT %SVG.switch.qname; %SVG.switch.content; >
<!-- end of SVG.switch.element -->]]>

<!ENTITY % SVG.switch.attlist "INCLUDE" >
<![%SVG.switch.attlist;[
<!ATTLIST %SVG.switch.qname;
    %SVG.Core.attrib;
    %SVG.Conditional.attrib;
    %SVG.Style.attrib;
    %SVG.Presentation.attrib;
    %SVG.GraphicalEvents.attrib;
    %SVG.External.attrib;
    transform %TransformList.datatype; #IMPLIED
>
<!-- end of SVG.switch.attlist -->]]>

<!-- end of svg-conditional.mod -->
