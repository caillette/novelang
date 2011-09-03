<?xml version="1.0"?>
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

<!DOCTYPE stylesheet [

    <!ENTITY % ISOnum PUBLIC
        "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML"
        "ISOnum.pen"
    >
    %ISOnum;

    <!ENTITY % ISOpub PUBLIC
        "ISO 8879:1986//ENTITIES Publishing//EN//XML"
        "ISOpub.pen"
    >
    %ISOpub;

    <!ENTITY % ISOlat1 PUBLIC
        "ISO 8879:1986//ENTITIES Added Latin 1//EN//XML"
        "ISOlat1.pen"
    >
    %ISOlat1;

]>


<!--
    Contains HTML layout and scripts and styling.
    The other stuff (text rendering) goes to simple-html.xsl because the Novelang Ant task
    doesn't find the novelang.rendering.xslt.WebColor class.
    So Novelang task uses simple-html.xsl and the rest uses this one, which reuses simple-html.xsl.
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
    xmlns:nlx="xalan://org.novelang.rendering.xslt"
    exclude-result-prefixes="nlx"
>
  <xsl:import href="simple-html.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>

  <xsl:output method="xml" />

  <xsl:variable name="webColor" select="nlx:WebColor.new()" />


  <xsl:template match="/" >

    <html> 
      <head>
        <link rel="stylesheet" type="text/css" href="/reset.css" />
        <link rel="stylesheet" type="text/css" href="/display.css" />
        <link rel="stylesheet" type="text/css" href="/custom.css" />
        <link rel="stylesheet" type="text/css" href="/jquery-theme/jquery-ui-1_7_2_custom.css" />
        <style type="text/css" /> <!-- Placeholder for dynamically-created tag classes. -->

        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$charset" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" >
          <xsl:attribute name="novelang-word-count" ><xsl:value-of select="//n:meta/n:word-count" /></xsl:attribute>          
        </meta>

        <title><xsl:value-of select="$filename"/></title>

        <script type="text/javascript" src="/javascript/jquery-1_3_2_min.js" />
        <script type="text/javascript" src="/javascript/jquery-rule-1_0_1_min.js" />
        <script type="text/javascript" src="/javascript/jquery-ui-1_7_2_custom_min.js" />
        <script type="text/javascript" src="/javascript/tags.js" />
        <script type="text/javascript" src="/javascript/tabs.js" />
        <script type="text/javascript"> //<![CDATA[
          $( document ).ready( function() {
            initializeTagSystem( "/javascript/colors.htm" ) ;
            initializeTabs() ;
            spiceUpSpacesInPre() ;
          } ) ;
        //]]></script>


        <style type="text/css" >
          <xsl:for-each select="//n:meta/n:tags/n:explicit-tag" >
            .Tag-<xsl:value-of select="." /> {
              color: <xsl:value-of select="nlx:foreground( $webColor )" /> ;
              border-color: <xsl:value-of select="nlx:foreground( $webColor )" /> ;
              background-color: <xsl:value-of select="nlx:background( $webColor )" /> ;
            }
            <xsl:value-of select="nlx:next( $webColor )" />
          </xsl:for-each>
        </style>

      </head>

    <body>



      <div id="rendered-document">
        <xsl:apply-templates />
      </div>

      <div id="right-sidebar">


        <div id="navigation" class="fixed-position" >

          <ul>
            <li><a href="#tag-list-content">Tags</a></li>
            <!--<li><a href="#identifier-list">Ids</a></li>-->
            <!--<li><a href="#debug-messages">Debug</a></li>-->
          </ul>

          <form name="tag-list" id="tag-list" >
            <!-- Dynamically filled. -->
            <div id="tag-list-content" />
            <br/>
          </form>

          <div id="identifier-list" />

          <p id="debug-messages" />

        </div>



        <div id="pin-navigation" >
          <ul>
            <li onclick="resetNavigation()" >reset</li>
            <li onclick="togglePinNavigation()">unpin</li>
          </ul>
        </div>
      </div>


      <xsl:if test="//n:meta/n:tags" >
        <dl id="tag-definitions" style="display : none ;">
          <xsl:for-each select="//n:meta/n:tags/*">
            <dt><xsl:value-of select="." /></dt>
          </xsl:for-each>
        </dl>
      </xsl:if>


    </body>
    </html>
  </xsl:template>


</xsl:stylesheet>
