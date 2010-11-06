<?xml version="1.0"?>
<!--
  ~ Copyright (C) 2010 Laurent Caillette
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
    This stylesheet contains standard transformations, without HTML page layout and scripts
    and styling.
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
>
  <xsl:import href="punctuation-US-EN.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>

  <xsl:output method="xml" />



  <xsl:template match="/" >

    <html> 
    <body>
      <xsl:apply-templates />
    </body>
    </html>
  </xsl:template>

  <xsl:template match="n:level" >
    <div class="level" >
      <xsl:call-template name="descriptor-vanilla"/>
      <xsl:apply-templates/>
    </div>

  </xsl:template>

  <xsl:template match="//n:level/n:level-title" >
    <h1><xsl:apply-templates /></h1>
  </xsl:template>

  <xsl:template match="//n:level/n:level/n:level-title" >
    <h2><xsl:apply-templates /></h2>
  </xsl:template>

  <xsl:template match="//n:level/n:level/n:level/n:level-title" >
    <h3><xsl:apply-templates /></h3>
  </xsl:template>

  <xsl:template match="n:paragraphs-inside-angled-bracket-pairs" >
    <blockquote>
      <xsl:call-template name="descriptor-for-blockquote" />
      <xsl:apply-templates />
    </blockquote>
  </xsl:template>

  <xsl:template match="n:lines-of-literal" >
    <div class="pre" >
      <xsl:call-template name="descriptor-for-pre" /><pre><xsl:apply-templates select="n:raw-lines"/></pre>
    </div>
  </xsl:template>

  <xsl:template match="n:paragraphs-inside-angled-bracket-pairs/n:lines-of-literal" >
    <div class="pre" ><xsl:call-template name="descriptor-for-pre-inside-blockquote" /><pre><xsl:apply-templates/></pre></div>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-regular" >
    <div class="p" >
      <xsl:call-template name="descriptor-vanilla" />
      <p>
        <xsl:apply-templates/>
      </p>
    </div>

  </xsl:template>

  <xsl:template match="n:url" >
    <a><xsl:attribute name="href"><xsl:value-of select="n:url-literal" /></xsl:attribute>
      <xsl:choose>
        <xsl:when test="n:block-inside-double-quotes" >
          <xsl:apply-templates select="n:block-inside-double-quotes/node()" />
        </xsl:when>
        <xsl:when test="n:block-inside-square-brackets" >
          <xsl:apply-templates select="n:block-inside-square-brackets/node()" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="n:url-literal" />
        </xsl:otherwise>
      </xsl:choose>
    </a>
  </xsl:template>


  <xsl:template match="n:list-with-double-hyphen-and-number-sign" >
    <xsl:for-each select="n:paragraph-as-list-item" >
      <xsl:call-template name="descriptor-vanilla" />
      <div class="p" >
        <p>
          <xsl:value-of select="position()" />.&nbsp;
          <xsl:apply-templates />
        </p>
      </div>
    </xsl:for-each>

  </xsl:template>
  
  <xsl:template match="n:list-with-triple-hyphen/n:paragraph-as-list-item" >
    <xsl:call-template name="descriptor-vanilla" />
    <div class="p" >
      <p>
        &mdash;&nbsp;
        <xsl:apply-templates/>
      </p>
    </div>
  </xsl:template>

  <xsl:template match="n:block-inside-solidus-pairs" >
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="n:block-of-literal-inside-grave-accents" >
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="n:block-of-literal-inside-grave-accent-pairs" >
    <code><xsl:apply-templates/></code>
  </xsl:template>

  <xsl:template match="n:word-after-circumflex-accent" >
    <sup><xsl:apply-templates/></sup>
  </xsl:template>

  <xsl:template match="n:block-after-tilde/n:subblock[position() > 1]" >&#x200b;<xsl:apply-templates/></xsl:template>



  <xsl:template match="n:cell-rows-with-vertical-line" >
    <xsl:call-template name="descriptor-vanilla" />
    <table>
      <xsl:choose>
        <xsl:when test="count( * ) > 2" >
          <tr>
            <xsl:apply-templates select="n:cell-row[ position() = 1 ]/*" mode="header" />
          </tr>
          <xsl:apply-templates select="n:cell-row[ position() > 1 ]" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>

    </table>
  </xsl:template>

  <xsl:template match="n:cell-row" >
    <tr>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>

  <xsl:template match="n:cell" >
    <td>
      <xsl:apply-templates/>
    </td>
  </xsl:template>
  
  <xsl:template match="n:cell" mode="header" >
    <th>
      <xsl:apply-templates/>
    </th>
  </xsl:template>

  <xsl:template match="n:raster-image/n:resource-location" >
    <img>
      <xsl:attribute name="src" ><xsl:value-of select="." /></xsl:attribute>
      <xsl:attribute name="title" ><xsl:value-of select="." /> [<xsl:value-of select="../n:image-height"/>x<xsl:value-of select="../n:image-height"/>]</xsl:attribute>
    </img>
  </xsl:template>  

  <xsl:template match="n:vector-image/n:resource-location" >
    <object type="text/xml" > <!--type="image/svg+xml"-->
      <xsl:attribute name="data" ><xsl:value-of select="." /></xsl:attribute>
      <xsl:attribute name="width"><xsl:value-of select="../n:image-width"/></xsl:attribute>
      <xsl:attribute name="height"><xsl:value-of select="../n:image-height"/></xsl:attribute>
    </object>
  </xsl:template>
  
  <xsl:template match="n:embedded-list-with-hyphen" >
    <ul>
      <xsl:apply-templates/>
    </ul>
  </xsl:template> 

  <xsl:template match="n:embedded-list-with-number-sign" >
    <ol>
      <xsl:apply-templates/>
    </ol>
  </xsl:template>

  <xsl:template match="n:embedded-list-item" >
    <li>
      <xsl:apply-templates/>
    </li>
  </xsl:template> 


  <xsl:template name="descriptor-for-blockquote" >
    <xsl:call-template name="descriptor-body" >
      <xsl:with-param name="icon" >/icons/OpeningQuote.png</xsl:with-param>
      <xsl:with-param name="css-class" >opening-quote</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="descriptor-for-pre" >
    <xsl:call-template name="descriptor-body" >
      <xsl:with-param name="icon" >/icons/Gears.png</xsl:with-param>
      <xsl:with-param name="css-class" >opening-pre</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="descriptor-for-pre-inside-blockquote" >
    <xsl:call-template name="descriptor-body" >
      <xsl:with-param name="icon" />
      <xsl:with-param name="css-class" >opening-pre</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="descriptor-vanilla" >
    <xsl:call-template name="descriptor-body" >
      <xsl:with-param name="icon" />
      <xsl:with-param name="css-class" />
    </xsl:call-template>
  </xsl:template>


  <xsl:template name="descriptor-body" >
    <xsl:param name="icon" />
    <xsl:param name="css-class" />

    <div class="descriptor" >
      <xsl:if test="n:implicit-tag or n:explicit-tag or n:implicit-identifier or n:explicit-identifier or n:colliding-explicit-identifier or n:location">
        <img class="descriptor-disclosure" src="/icons/Descriptor.png" />
        <div class="collapsable-descriptor" style="display : none ;">

          <xsl:if test="n:location">
            <span class="location">
              <xsl:value-of select="n:location"/>
            </span>
          </xsl:if>
          
          <xsl:if test="n:explicit-identifier">
            <br/>
            <p class="explicit-identifier">
              <xsl:value-of select="n:explicit-identifier"/>
            </p>
          </xsl:if>
          
          <xsl:if test="n:colliding-explicit-identifier">
            <br/>
            <p class="colliding-explicit-identifier" >
              <xsl:value-of select="n:colliding-explicit-identifier"/>
            </p>
          </xsl:if>

          <xsl:if test="n:implicit-identifier">
            <br/>
            <p class="implicit-identifier">
              <xsl:value-of select="n:implicit-identifier"/>
            </p>
          </xsl:if>
          
          <xsl:if test="n:implicit-tag">
            <ul class="tags">
              <xsl:for-each select="n:implicit-tag">
                <li >
                  <xsl:attribute name="class" >implicit-tag Tag-<xsl:value-of select="." /></xsl:attribute>
                  <xsl:value-of select="."/>
                </li>
              </xsl:for-each>
            </ul>
          </xsl:if>
          
        </div>
        
        <xsl:if test="n:promoted-tag">
          <ul class="tags">
            <xsl:for-each select="n:promoted-tag">
              <li >
                <xsl:attribute name="class" >promoted-tag Tag-<xsl:value-of select="." /></xsl:attribute>
                <xsl:value-of select="."/>
              </li>
            </xsl:for-each>
          </ul>
        </xsl:if>

        <xsl:if test="n:explicit-tag">
          <ul class="tags">
            <xsl:for-each select="n:explicit-tag">
              <li >
                <xsl:attribute name="class" >explicit-tag Tag-<xsl:value-of select="." /></xsl:attribute>
                <xsl:value-of select="."/>
              </li>
            </xsl:for-each>
          </ul>
        </xsl:if>
      </xsl:if>


      <xsl:if test="$icon" >
        <xsl:element name="img" >
          <xsl:attribute name="src" ><xsl:value-of select="$icon" /></xsl:attribute>
          <xsl:attribute name="class" ><xsl:value-of select="$css-class" /></xsl:attribute>
          <xsl:attribute name="alt" />
        </xsl:element>
      </xsl:if>

    </div>
  </xsl:template>


</xsl:stylesheet>
