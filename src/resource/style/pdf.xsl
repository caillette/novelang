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

        <fo:static-content flow-name="xsl-region-before" >
          <fo:block text-align="right" >
            <fo:inline font-size="80%" >
              &legal;
            </fo:inline>
          </fo:block>
        </fo:static-content>

        <fo:flow
            flow-name="xsl-region-body"
        >
          <xsl:apply-templates />
        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  <xsl:template match="/n:book/n:title[1]" >
    <fo:block
        padding-top="60pt"
        font-size="24pt"
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


  <xsl:template match="n:chapter" >
    <fo:block
        padding-top="30pt"
        font-size="12pt"
        font-family="serif"
        line-height="18pt"
    >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:chapter/n:title | n:chapter/n:identifier" >
    <fo:block
        font-size="18pt"
        font-weight="bold"
        line-height="20pt"
        keep-with-next.within-page="always"
    >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:section" >
    <fo:block padding-top="20pt" >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:section/n:title | n:section/n:identifier" >
    <fo:block
        font-size="13pt"
        font-weight="bold"
        line-height="35pt"
        keep-with-next.within-page="always"
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

  <xsl:template match="n:litteral" >
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

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-plain" >
    <xsl:call-template name="paragraph-plain" />
  </xsl:template>

  <xsl:template name="paragraph-plain" >
    <fo:block
        text-indent="1em"
        text-align="justify"
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
      <fo:inline
          font-style="bold"
      >
        <xsl:apply-templates/>
      </fo:inline>
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

