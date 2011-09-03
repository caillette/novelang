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
<!-- SVG 1.1 XLink Attribute Module ........................................ -->
<!-- XLink Attribute

       type, href, role, arcrole, title, show, actuate

     This module defines the XLink, XLinkRequired, XLinkEmbed, and
     XLinkReplace attribute set.
-->

<!ENTITY % SVG.XLink.extra.attrib "" >

<!ENTITY % SVG.XLink.attrib
    "%XLINK.xmlns.attrib;
     %XLINK.pfx;type ( simple ) #FIXED 'simple'
     %XLINK.pfx;href %URI.datatype; #IMPLIED
     %XLINK.pfx;role %URI.datatype; #IMPLIED
     %XLINK.pfx;arcrole %URI.datatype; #IMPLIED
     %XLINK.pfx;title CDATA #IMPLIED
     %XLINK.pfx;show ( other ) 'other'
     %XLINK.pfx;actuate ( onLoad ) #FIXED 'onLoad'
     %SVG.XLink.extra.attrib;"
>

<!ENTITY % SVG.XLinkRequired.extra.attrib "" >

<!ENTITY % SVG.XLinkRequired.attrib
    "%XLINK.xmlns.attrib;
     %XLINK.pfx;type ( simple ) #FIXED 'simple'
     %XLINK.pfx;href %URI.datatype; #REQUIRED
     %XLINK.pfx;role %URI.datatype; #IMPLIED
     %XLINK.pfx;arcrole %URI.datatype; #IMPLIED
     %XLINK.pfx;title CDATA #IMPLIED
     %XLINK.pfx;show ( other ) 'other'
     %XLINK.pfx;actuate ( onLoad ) #FIXED 'onLoad'
     %SVG.XLinkRequired.extra.attrib;"
>

<!ENTITY % SVG.XLinkEmbed.extra.attrib "" >

<!ENTITY % SVG.XLinkEmbed.attrib
    "%XLINK.xmlns.attrib;
     %XLINK.pfx;type ( simple ) #FIXED 'simple'
     %XLINK.pfx;href %URI.datatype; #REQUIRED
     %XLINK.pfx;role %URI.datatype; #IMPLIED
     %XLINK.pfx;arcrole %URI.datatype; #IMPLIED
     %XLINK.pfx;title CDATA #IMPLIED
     %XLINK.pfx;show ( embed ) 'embed'
     %XLINK.pfx;actuate ( onLoad ) #FIXED 'onLoad'
     %SVG.XLinkEmbed.extra.attrib;"
>

<!ENTITY % SVG.XLinkReplace.extra.attrib "" >

<!ENTITY % SVG.XLinkReplace.attrib
    "%XLINK.xmlns.attrib;
     %XLINK.pfx;type ( simple ) #FIXED 'simple'
     %XLINK.pfx;href %URI.datatype; #REQUIRED
     %XLINK.pfx;role %URI.datatype; #IMPLIED
     %XLINK.pfx;arcrole %URI.datatype; #IMPLIED
     %XLINK.pfx;title CDATA #IMPLIED
     %XLINK.pfx;show ( new | replace ) 'replace'
     %XLINK.pfx;actuate ( onRequest ) #FIXED 'onRequest'
     %SVG.XLinkReplace.extra.attrib;"
>

<!-- end of svg-xlink-attrib.mod -->
