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

  <!ENTITY % Fo PUBLIC
      "http://www.w3.org/1999/XSL/Format"
      "fo.dtd"
  >
  %Fo;


  <!ENTITY legal "Created with Novelang!">

] >

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:svg="http://www.w3.org/2000/svg"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
    xmlns:nlx="xalan://org.novelang.rendering.xslt"
>
  <xsl:import href="punctuation-US-EN.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="content-directory"/>

  <xsl:template match="/" >
    <fo:root>

      <fo:layout-master-set>

        <fo:simple-page-master
            master-name="A4"
            page-width="210mm"   page-height="297mm"
            margin-top="0mm"     margin-bottom="7mm"
            margin-right="25mm"  margin-left="25mm"
        >
          <fo:region-body margin-top="1.1cm" margin-bottom="1.1cm" />
          <fo:region-before extent="1cm" />
          <fo:region-after extent="1cm" />
        </fo:simple-page-master>

      </fo:layout-master-set>

      <fo:page-sequence
          initial-page-number="1"
          master-reference="A4"
      >
        <fo:static-content flow-name="xsl-region-after" >
          <fo:table >
            <fo:table-column column-width="proportional-column-width(1)" />
            <fo:table-column column-width="proportional-column-width(1)" />
            <fo:table-column column-width="proportional-column-width(1)" />

            <fo:table-body>
              <fo:table-row>
                <fo:table-cell display-align="after" >
                  <fo:block font-size="70%" text-align="left" />
                </fo:table-cell>
                <fo:table-cell>
                  <fo:block text-align="center" >
                    <fo:page-number/> / <fo:page-number-citation ref-id="@last-page"/>
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell display-align="after" >
                  <fo:block font-size="70%" text-align="right" >
                    <xsl:value-of
                        select="nlx:Numbering.formatDateTime( $timestamp, 'YYYY-MM-dd HH:mm:ss' )"
                    />
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>
          </fo:table>
        </fo:static-content>

        <fo:flow
            flow-name="xsl-region-body"
        >
          <fo:block font-family="Linux Libertine" >
            <xsl:call-template name="custom-document-start" />
            <xsl:apply-templates />
          </fo:block>
          <fo:block id="@last-page"/>
        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>
  
  <!--Override this template in stylesheet importing this one-->
  <xsl:template name="custom-document-start" />

  <xsl:template match="/n:opus/n:level-title[1]" >
    <fo:block
        padding-top="40pt"
        font-size="28pt"
        text-align="center"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="/n:opus/n:level-title[position() > 1]" >
    <fo:block
        padding-top="16pt"
        font-size="18pt"
        text-align="center"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="/n:opus/n:level-title[position() = last()]" >
    <fo:block
        padding-top="16pt"
        padding-bottom="30pt"
        font-size="18pt"
        text-align="center"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>


  <xsl:template match="n:level/n:level-title" >
    <fo:block
        font-size="17pt"
        font-weight="200"
        line-height="20pt"
        padding-bottom="8pt"
        keep-with-next.within-page="always"
    >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:level/n:level/n:level-title" >
    <fo:block
        font-size="11pt"
        font-weight="bold"
        line-height="35pt"
        keep-with-next.within-page="always"
        >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:level" >
    <fo:block
        padding-top="30pt"
        >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:level/n:level" >
    <fo:block
        padding-top="0pt"
        padding-bottom="10pt"
    >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:paragraphs-inside-angled-bracket-pairs" >
    <fo:block
        text-align="left"
        text-indent="0em"
        margin-left="30pt"
        margin-right="10pt"
        padding-before="6pt"
        padding-after="8pt"
        font-size="10pt"
        font-family="Linux Libertine"
        line-height="13pt"
        font-stretch="semi-condensed"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:lines-of-literal/n:raw-lines" >
    <fo:block
        text-align="left"
        text-indent="0em"
        margin-left="-16pt"
        margin-right="0pt"
        margin-top="4pt"
        margin-bottom="5pt"
        padding-before="6pt"
        padding-after="8pt"
        font-size="10pt"
        font-family="monospace"
        line-height="13pt"
        background-color="#EEEEEE"
        font-stretch="semi-condensed"
        white-space-treatment="preserve"
        white-space-collapse="false"
        linefeed-treatment="preserve"
        keep-together.within-page="always"
        border-left-style="solid"
        border-left-width="3pt"
        border-left-color="#CCCCCC"
        padding-left="10pt"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:block-of-literal-inside-grave-accent-pairs" >
    <fo:inline
        font-family="monospace"
        font-size="10pt"
        white-space-treatment="preserve"
        white-space-collapse="false"
        linefeed-treatment="preserve"
    >
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="n:block-of-literal-inside-grave-accents" >
    <fo:inline
        white-space-treatment="preserve"
        white-space-collapse="false"
        linefeed-treatment="preserve"
    >
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-regular" >
    <xsl:call-template name="paragraph-plain" />
  </xsl:template>

  <xsl:template name="paragraph-plain" >
    <fo:block
        text-indent="1em"
        text-align="justify"
        font-size="12pt"
        line-height="18pt"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="//n:paragraphs-inside-angled-bracket-pairs//n:paragraph-regular" >
    <fo:block text-indent="0em" >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:url" >
    <!--<fo:block>-->
      <fo:basic-link
          color="blue"
          text-decoration="underline"
      >
        <xsl:attribute name="external-destination" ><xsl:value-of select="n:url-literal" /></xsl:attribute>
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
      </fo:basic-link>
    <!--</fo:block>-->
  </xsl:template>


  <xsl:template match="n:list-with-triple-hyphen/n:paragraph-as-list-item" >
    <fo:block
        text-align="justify"
        text-indent="1em"
        font-size="12pt"
        line-height="18pt"
    >
      <fo:inline
          text-align="justify"
          text-indent="1em"
      >
        &mdash;&nbsp;
        <xsl:apply-templates/>
      </fo:inline>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:list-with-double-hyphen-and-number-sign" >
    <fo:list-block
        provisional-distance-between-starts="6pt"
        provisional-label-separation="10pt">
      <xsl:apply-templates/>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="n:list-with-double-hyphen-and-number-sign/n:paragraph-as-list-item" >
    <fo:list-item space-before="5pt">
      <fo:list-item-label end-indent="label-end()" >
        <fo:block text-indent="1pt" >
          <xsl:number count="n:paragraph-as-list-item" format="1."/>
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body>
        <fo:block text-indent="20pt"> <!--Better way?-->
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>




  <xsl:template match="n:block-inside-solidus-pairs" >
    <fo:inline font-style="italic" ><xsl:apply-templates/></fo:inline>
  </xsl:template>

  <xsl:template match="n:block-inside-asterisk-pairs" >
    <fo:inline font-weight="bold" ><xsl:apply-templates/></fo:inline>
  </xsl:template>

  <xsl:template match="n:word-after-circumflex-accent" >
    <fo:inline
        font-style="italic"
        vertical-align="super"
        font-size="8pt"
    >
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="n:block-after-tilde/n:subblock[position() > 1]" >&#x200b;<xsl:apply-templates/></xsl:template>

  <xsl:template match="n:cell-rows-with-vertical-line" >

    <fo:table>
      <xsl:for-each select="n:cell-row[1]/n:cell">
        <fo:table-column column-width="proportional-column-width(1)" />
      </xsl:for-each>

      <fo:table-body>

        <xsl:choose>
          <xsl:when test="count( * ) > 1" >
            <fo:table-row>
              <xsl:apply-templates select="n:cell-row[ position() = 1 ]/*" mode="header" />
            </fo:table-row>
            <xsl:apply-templates select="n:cell-row[ position() > 1 ]" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates/>
          </xsl:otherwise>
        </xsl:choose>
 

      </fo:table-body>

    </fo:table>
  </xsl:template>

  <xsl:template match="n:cell-row" >
    <fo:table-row>
      <xsl:apply-templates/>
    </fo:table-row>
  </xsl:template>

  <xsl:template match="n:cell" >
    <fo:table-cell padding="2pt" border="1pt solid black">
      <fo:block>
        <xsl:apply-templates />
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <xsl:template match="n:cell" mode="header" >
    <fo:table-cell
        padding="2pt"
        border="1pt solid black"
        text-align="center"
        background-color="#f4f4f4"
    >
      <fo:block font-weight="bold">
        <xsl:apply-templates />
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <xsl:template match="n:raster-image/n:resource-location" >
    <fo:block>
      <fo:external-graphic>
        <xsl:attribute name="src">.<xsl:value-of select="."/></xsl:attribute>
        <xsl:attribute name="content-width"><xsl:value-of select="../n:image-width"/></xsl:attribute>
        <xsl:attribute name="content-height"><xsl:value-of select="../n:image-height"/></xsl:attribute>
      </fo:external-graphic>
    </fo:block>
  </xsl:template>  
  
  <xsl:template match="n:vector-image/n:resource-location" >
    <fo:block>
      <fo:instream-foreign-object>
        <svg:svg>
          <xsl:attribute name="width" ><xsl:value-of select="../n:image-width"/></xsl:attribute>
          <xsl:attribute name="height" ><xsl:value-of select="../n:image-height"/></xsl:attribute>
          <svg:image>
            <xsl:attribute name="width" ><xsl:value-of select="../n:image-width"/></xsl:attribute>
            <xsl:attribute name="height" ><xsl:value-of select="../n:image-height"/></xsl:attribute>
            <xsl:attribute name="xlink:href" ><xsl:value-of select="$content-directory"/><xsl:value-of select="."/></xsl:attribute>
          </svg:image>
        </svg:svg>
      </fo:instream-foreign-object>
    </fo:block>
  </xsl:template>
  
  

  <xsl:template match="n:embedded-list-with-hyphen" >
    <fo:list-block 
        provisional-distance-between-starts="3pt"
        provisional-label-separation="1pt">
      <xsl:apply-templates/>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="n:embedded-list-with-hyphen//n:embedded-list-with-hyphen">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="n:embedded-list-with-hyphen/n:embedded-list-item">
    <fo:list-item space-before="0pt">
      <fo:list-item-label>
        <fo:block text-indent="1pt" >
          <!--<xsl:number count="n:embedded-list-item" format="1."/>-->
          -
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body>
        <fo:block>
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>

  <xsl:template 
      match="n:embedded-list-with-hyphen//n:embedded-list-with-hyphen/n:embedded-list-item" 
      priority="1"
  >
    <fo:list-item text-indent="20pt" >
      <fo:list-item-label>
        <fo:block text-indent="10pt" >
          <!--<xsl:number count="n:embedded-list-item" format="a."/>-->
          -
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body>
        <fo:block>
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>
  
  <xsl:template 
      match="n:embedded-list-with-hyphen//n:embedded-list-with-hyphen//n:embedded-list-with-hyphen/n:embedded-list-item" 
      priority="2"
  >
    <fo:list-item text-indent="30pt" >
      <fo:list-item-label>
        <fo:block text-indent="20pt" >
          <!--<xsl:number count="n:embedded-list-item" format="i."/>-->
          -
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body>
        <fo:block>
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>





  <xsl:template match="n:embedded-list-with-number-sign" >
    <fo:list-block
        provisional-distance-between-starts="3pt"
        provisional-label-separation="1pt">
      <xsl:apply-templates/>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="n:embedded-list-with-number-sign//n:embedded-list-with-number-sign">
    <xsl:apply-templates/>
  </xsl:template>



  <xsl:template match="n:embedded-list-with-number-sign/n:embedded-list-item">
    <fo:list-item space-before="0pt">
      <fo:list-item-label end-indent="label-end()" >
        <fo:block text-indent="1pt" >
          <xsl:number count="n:embedded-list-item" format="1."/>
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body>
        <fo:block>
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>

  <xsl:template
      match="n:embedded-list-with-number-sign//n:embedded-list-with-number-sign/n:embedded-list-item"
      priority="1"
  >
    <fo:list-item text-indent="20pt" >
      <fo:list-item-label end-indent="label-end()" >
        <fo:block text-indent="10pt" >
          <xsl:number count="n:embedded-list-item" format="a."/>
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body>
        <fo:block>
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>

  <xsl:template
      match="n:embedded-list-with-number-sign//n:embedded-list-with-number-sign//n:embedded-list-with-number-sign/n:embedded-list-item" 
      priority="2"
  >
    <fo:list-item text-indent="30pt" >
      <fo:list-item-label end-indent="label-end()" >
        <fo:block text-indent="20pt" >
          <xsl:number count="n:embedded-list-item" format="i."/>
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body>
        <fo:block>
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>



</xsl:stylesheet>

