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
<!-- SVG 1.1 Basic Attribute Collection Module ............................. -->
<!-- SVG 1.1 Basic Attribute Collection

     This module defines the set of common attributes that can be present
     on many SVG elements.
-->

<!-- module: svg-conditional.mod ....................... -->

<!ENTITY % ExtensionList.datatype "CDATA" >
<!ENTITY % FeatureList.datatype "CDATA" >

<!ENTITY % SVG.Conditional.extra.attrib "" >
<!ENTITY % SVG.Conditional.attrib
    "requiredFeatures %FeatureList.datatype; #IMPLIED
     requiredExtensions %ExtensionList.datatype; #IMPLIED
     systemLanguage %LanguageCodes.datatype; #IMPLIED
     %SVG.Conditional.extra.attrib;"
>

<!-- module: svg-style.mod ............................. -->

<!ENTITY % ClassList.datatype "CDATA" >
<!ENTITY % StyleSheet.datatype "CDATA" >

<!ENTITY % SVG.Style.extra.attrib "" >
<!ENTITY % SVG.Style.attrib
    "style %StyleSheet.datatype; #IMPLIED
     class %ClassList.datatype; #IMPLIED
     %SVG.Style.extra.attrib;"
>

<!-- module: svg-text.mod .............................. -->

<!ENTITY % BaselineShiftValue.datatype "CDATA" >
<!ENTITY % FontFamilyValue.datatype "CDATA" >
<!ENTITY % FontSizeValue.datatype "CDATA" >
<!ENTITY % FontSizeAdjustValue.datatype "CDATA" >
<!ENTITY % GlyphOrientationHorizontalValue.datatype "CDATA" >
<!ENTITY % GlyphOrientationVerticalValue.datatype "CDATA" >
<!ENTITY % KerningValue.datatype "CDATA" >
<!ENTITY % SpacingValue.datatype "CDATA" >
<!ENTITY % TextDecorationValue.datatype "CDATA" >

<!ENTITY % SVG.Text.extra.attrib "" >
<!ENTITY % SVG.Text.attrib
    "writing-mode ( lr-tb | rl-tb | tb-rl | lr | rl | tb | inherit ) #IMPLIED
     %SVG.Text.extra.attrib;"
>

<!ENTITY % SVG.TextContent.extra.attrib "" >
<!ENTITY % SVG.TextContent.attrib
    "alignment-baseline ( auto | baseline | before-edge | text-before-edge |
                          middle | central | after-edge | text-after-edge |
                          ideographic | alphabetic | hanging | mathematical |
                          inherit ) #IMPLIED
     baseline-shift %BaselineShiftValue.datatype; #IMPLIED
     direction ( ltr | rtl | inherit ) #IMPLIED
     dominant-baseline ( auto | use-script | no-change | reset-size |
                         ideographic | alphabetic | hanging | mathematical |
                         central | middle | text-after-edge | text-before-edge |
                         inherit ) #IMPLIED
     glyph-orientation-horizontal %GlyphOrientationHorizontalValue.datatype;
                                  #IMPLIED
     glyph-orientation-vertical %GlyphOrientationVerticalValue.datatype;
                                #IMPLIED
     kerning %KerningValue.datatype; #IMPLIED
     letter-spacing %SpacingValue.datatype; #IMPLIED
     text-anchor ( start | middle | end | inherit ) #IMPLIED
     text-decoration %TextDecorationValue.datatype; #IMPLIED
     unicode-bidi ( normal | embed | bidi-override | inherit ) #IMPLIED
     word-spacing %SpacingValue.datatype; #IMPLIED
     %SVG.TextContent.extra.attrib;"
>

<!ENTITY % SVG.Font.extra.attrib "" >
<!ENTITY % SVG.Font.attrib
    "font-family %FontFamilyValue.datatype; #IMPLIED
     font-size %FontSizeValue.datatype; #IMPLIED
     font-size-adjust %FontSizeAdjustValue.datatype; #IMPLIED
     font-stretch ( normal | wider | narrower | ultra-condensed |
                    extra-condensed | condensed | semi-condensed |
                    semi-expanded | expanded | extra-expanded |
                    ultra-expanded | inherit ) #IMPLIED
     font-style ( normal | italic | oblique | inherit ) #IMPLIED
     font-variant ( normal | small-caps | inherit ) #IMPLIED
     font-weight ( normal | bold | bolder | lighter | 100 | 200 | 300 | 400 |
                   500 | 600 | 700 | 800 | 900 | inherit ) #IMPLIED
     %SVG.Font.extra.attrib;"
>

<!-- module: svg-profile.mod ........................... -->

<!ENTITY % SVG.ColorProfile.extra.attrib "" >
<!ENTITY % SVG.ColorProfile.attrib
    "color-profile CDATA #IMPLIED
     %SVG.ColorProfile.extra.attrib;"
>

<!-- module: svg-gradient.mod .......................... -->

<!ENTITY % NumberOrPercentage.datatype "CDATA" >

<!ENTITY % SVG.Gradient.extra.attrib "" >
<!ENTITY % SVG.Gradient.attrib
    "stop-color %SVGColor.datatype; #IMPLIED
     stop-opacity %OpacityValue.datatype; #IMPLIED
     %SVG.Gradient.extra.attrib;"
>

<!-- module: svg-basic-clip.mod ........................ -->

<!ENTITY % ClipPathValue.datatype "CDATA" >

<!ENTITY % SVG.Clip.extra.attrib "" >
<!ENTITY % SVG.Clip.attrib
    "clip-path %ClipPathValue.datatype; #IMPLIED
     clip-rule %ClipFillRule.datatype; #IMPLIED
     %SVG.Clip.extra.attrib;"
>

<!-- module: svg-mask.mod .............................. -->

<!ENTITY % MaskValue.datatype "CDATA" >

<!ENTITY % SVG.Mask.extra.attrib "" >
<!ENTITY % SVG.Mask.attrib
    "mask %MaskValue.datatype; #IMPLIED
     %SVG.Mask.extra.attrib;"
>

<!-- module: svg-basic-filter.mod ...................... -->

<!ENTITY % FilterValue.datatype "CDATA" >
<!ENTITY % NumberOptionalNumber.datatype "CDATA" >

<!ENTITY % SVG.Filter.extra.attrib "" >
<!ENTITY % SVG.Filter.attrib
    "filter %FilterValue.datatype; #IMPLIED
     %SVG.Filter.extra.attrib;"
>

<!ENTITY % SVG.FilterColor.extra.attrib "" >
<!ENTITY % SVG.FilterColor.attrib
    "color-interpolation-filters ( auto | sRGB | linearRGB | inherit )
                                   #IMPLIED
     %SVG.FilterColor.extra.attrib;"
>

<!-- end of svg11-basic-attribs.mod -->
