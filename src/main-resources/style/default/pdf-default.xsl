<?xml version="1.0"?>
<!DOCTYPE foo [

  <!ENTITY % ISOnum PUBLIC
      "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML"
      "../ISOnum.pen"
  >
  %ISOnum;

  <!ENTITY % ISOpub PUBLIC
      "ISO 8879:1986//ENTITIES Publishing//EN//XML"
      "../ISOpub.pen"
  >

  %ISOpub;
  <!ENTITY % ISOlat1 PUBLIC
      "ISO 8879:1986//ENTITIES Added Latin 1//EN//XML"
      "../ISOlat1.pen"
  >
  %ISOlat1;

  <!ENTITY % Fo PUBLIC
      "http://www.w3.org/1999/XSL/Format"
      "../fo.dtd"
  >
  %Fo;


  <!ENTITY legal "Created with Novelang!">

] >

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
    xmlns:nlx="xalan://novelang.rendering.xslt"
>
  <xsl:import href="default/punctuation-US-EN.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>

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
            <xsl:apply-templates />
          </fo:block>
          <fo:block id="@last-page"/>
        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  <xsl:template match="/n:book/n:level-title[1]" >
    <fo:block
        padding-top="40pt"
        font-size="28pt"
        text-align="center"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="/n:book/n:level-title[position() > 1]" >
    <fo:block
        padding-top="16pt"
        font-size="18pt"
        text-align="center"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="/n:book/n:level-title[position() = last()]" >
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

  <xsl:template match="n:lines-of-literal" >
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
    <fo:block>
      <fo:basic-link
          color="blue"
          text-decoration="underline"
      >
        <xsl:attribute name="external-destination" ><xsl:value-of select="." /></xsl:attribute>
        <xsl:value-of select="." />
      </fo:basic-link>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:paragraph-as-list-item" >
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


  <xsl:template match="n:embedded-list-with-hyphen" >
    <fo:list-block
        text-align="justify"
        text-indent="1em"
        font-size="12pt"
        line-height="18pt"
    >
      <xsl:apply-templates/>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="n:embedded-list-with-hyphen/n:embedded-list-item" >
    <fo:list-item>
      <fo:list-item-label>
        <fo:block>&ndash;</fo:block>
      </fo:list-item-label>
      <fo:list-item-body>
        <fo:block>
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>

  <xsl:template match="n:block-inside-solidus-pairs" >
    <fo:inline font-style="italic" ><xsl:apply-templates/></fo:inline>
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


  <xsl:template match="n:cell-rows-with-vertical-line" >

    <fo:table>
      <xsl:for-each select="n:cell-row[1]/n:cell">
        <fo:table-column column-width="proportional-column-width(1)" />
      </xsl:for-each>

      <fo:table-body>
        <xsl:apply-templates />
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

  <xsl:template match="n:raster-image/n:resource-location" >
    <fo:block>
      <fo:external-graphic>
        <xsl:attribute name="src">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </fo:external-graphic>
    </fo:block>

  </xsl:template>  
  

</xsl:stylesheet>

