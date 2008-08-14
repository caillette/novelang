<?xml version="1.0"?>
<!DOCTYPE foo [

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
    xmlns:n="http://novelang.org/book-xml/1.0"
>
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
          <fo:table  >
            <fo:table-column column-width="40%" />
            <fo:table-column column-width="20%" />
            <fo:table-column column-width="40%" />

            <fo:table-body>
              <fo:table-row>
                <fo:table-cell display-align="after" >
                  <fo:block font-size="70%" text-align="left" >
                    <xsl:value-of select="/n:book/n:title" />
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell>
                  <fo:block text-align="center" >
                    <fo:page-number/> / <fo:page-number-citation ref-id="@last-page"/>
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell display-align="after" >
                  <fo:block font-size="70%" text-align="right" >
                    <xsl:value-of select="$timestamp" />
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>
          </fo:table>
        </fo:static-content>

        <fo:flow
            flow-name="xsl-region-body"
        >
          <xsl:apply-templates />
          <fo:block id="@last-page"/>
        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  <xsl:template match="/n:book/n:title[1]" >
    <fo:block
        padding-top="40pt"
        font-size="28pt"
        text-align="center"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="/n:book/n:title[position() > 1]" >
    <fo:block
        padding-top="16pt"
        font-size="18pt"
        text-align="center"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="/n:book/n:title[position() = last()]" >
    <fo:block
        padding-top="16pt"
        padding-bottom="30pt"
        font-size="18pt"
        text-align="center"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>


  <xsl:template match="n:chapter/n:title | n:chapter/n:identifier" >
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

  <xsl:template match="n:section/n:title | n:section/n:identifier" >
    <fo:block
        font-size="11pt"
        font-weight="bold"
        line-height="35pt"
        keep-with-next.within-page="always"
        >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:chapter" >
    <fo:block
        padding-top="30pt"
        >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:section" >
    <fo:block
        padding-top="0pt"
        padding-bottom="10pt"
    >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:blockquote" >
    <fo:block
        text-align="left"
        text-indent="0em"
        margin-left="30pt"
        margin-right="10pt"
        padding-before="6pt"
        padding-after="8pt"
        font-size="10pt"
        font-family="sans-serif"
        line-height="13pt"
        font-stretch="semi-condensed"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:literal" >
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

  <xsl:template match="n:hard-inline-literal" >
    <fo:inline
        font-family="monospace"
    >
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-plain" >
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

  <xsl:template match="//n:blockquote//n:paragraph-plain" >
    <fo:block text-indent="0em" >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:url" >
    <fo:block>
      <fo:basic-link
          color="blue"
          text-decoration="underline"
          font-size="85%"
      >
        <xsl:attribute name="external-destination" ><xsl:value-of select="." /></xsl:attribute>
        <xsl:value-of select="." />
      </fo:basic-link>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:paragraph-speech" >
    <xsl:call-template name="speech" >
      <xsl:with-param name="speech-symbol" >&mdash;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="n:paragraph-speech-escaped" >
    <xsl:call-template name="paragraph-plain" />
  </xsl:template>

  <xsl:template match="n:paragraph-speech-continued" >
    <xsl:call-template name="speech" >
      <xsl:with-param name="speech-symbol" >&raquo;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="speech" >
    <xsl:param name = "speech-symbol" />
    <fo:block
        text-align="justify"
        text-indent="1em"
        font-size="12pt"
        font-family="serif"
        line-height="18pt"
    >
      <fo:inline
          text-align="justify"
          text-indent="1em"
      >
        <xsl:value-of select="$speech-symbol" />&nbsp;
        <xsl:apply-templates/>
      </fo:inline>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:emphasis" >
    <fo:inline font-style="italic" ><xsl:apply-templates/></fo:inline>
  </xsl:template>

  <xsl:import href="punctuation-US-EN.xsl" />

</xsl:stylesheet>

