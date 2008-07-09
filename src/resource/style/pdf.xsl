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


  <!ENTITY legal "(c) your name here!">

] >

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>

  <xsl:template match="/" >
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" >

      <fo:layout-master-set>

        <!-- master set for chapter pages, first page is the title page -->
        <fo:page-sequence-master master-name="chapter-master">

          <fo:repeatable-page-master-alternatives>

            <fo:conditional-page-master-reference
                blank-or-not-blank="blank"
                master-reference="blank-page"
            />

            <fo:conditional-page-master-reference
                page-position="first"
                odd-or-even="odd"
                master-reference="title-first"
            />

            <fo:conditional-page-master-reference
                page-position="rest"
                odd-or-even="odd"
                master-reference="chapter-rest-odd"
            />
            <fo:conditional-page-master-reference
                page-position="rest"
                odd-or-even="even"
                master-reference="chapter-rest-even"
            />
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>


        <!-- simple page masters -->

        <fo:simple-page-master
            master-name="title-first"
            page-width="210mm"   page-height="297mm"
            margin-top="0mm"     margin-bottom="7mm"
            margin-right="45mm"  margin-left="45mm"
        >
          <fo:region-body/>
        </fo:simple-page-master>

        <fo:simple-page-master
            master-name="blank-page"
            page-width="210mm"   page-height="297mm"
            margin-top="0mm"     margin-bottom="7mm"
            margin-right="45mm"  margin-left="45mm"
        >
          <fo:region-body/>
        </fo:simple-page-master>

        <fo:simple-page-master
            master-name="chapter-rest-odd"
            margin-top="0mm"     margin-bottom="7mm"
            margin-left="60mm"   margin-right="30mm"
        >
          <fo:region-body margin-top="20mm" margin-bottom="15mm" />
          <fo:region-before extent="2mm" />
          <fo:region-after
              region-name="region-after-odd"
              extent="10mm"
          />
        </fo:simple-page-master>

        <fo:simple-page-master
            master-name="chapter-rest-even"
            margin-top="0mm"     margin-bottom="7mm"
            margin-left="30mm"   margin-right="60mm"
        >
          <fo:region-body margin-top="20mm" margin-bottom="15mm" />
          <fo:region-before extent="2mm" />
          <fo:region-after
              region-name="region-after-even"
              extent="10mm"
          />
        </fo:simple-page-master>

      </fo:layout-master-set>



      <fo:page-sequence
          initial-page-number="1"
          master-reference="chapter-master"
      >

        <fo:static-content flow-name="xsl-region-before" >
          <fo:block text-align="right" >
            <fo:inline font-size="80%" >
              &legal;
            </fo:inline>
          </fo:block>
        </fo:static-content>

        <fo:static-content flow-name="region-after-odd" >
          <fo:block text-align="right"><fo:page-number/></fo:block>
        </fo:static-content>

        <fo:static-content flow-name="region-after-even" >
          <fo:block text-align="left"><fo:page-number/></fo:block>
        </fo:static-content>



        <fo:flow
            flow-name="xsl-region-body"
        >
          <xsl:call-template name="title-page" />
          <xsl:apply-templates />
        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>


  <xsl:template name="title-page" >
    
  </xsl:template>


  <xsl:template match="n:chapter" >
    <xsl:choose>
      <xsl:when test="n:style[text()='standalone']" >
        <xsl:call-template name="standalone" />
      </xsl:when>
      <xsl:when test="n:style[text()='all-emphasized']" >
        <xsl:call-template name="all-emphasized" />
      </xsl:when>
      <xsl:otherwise>
        <!--<xsl:call-template name="standard" />-->
        <xsl:call-template name="standalone" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="n:chapter/n:title | n:chapter/n:identifier" >
    <fo:block
        font-size="15pt"
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
    <fo:inline
        font-size="13pt"
        font-weight="bold"
        line-height="35pt"
        keep-with-next.within-page="always"
    >
      <xsl:apply-templates />
    </fo:inline>
  </xsl:template>

  <xsl:template name="standard" >
    <fo:block padding-top="40pt" >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template name="standalone" >
    <fo:block
        break-after="odd-page"
        padding-top="230pt"
        font-size="12pt"
        font-family="serif"
        line-height="18pt"
    >
      <xsl:apply-templates select="*[name() != 'n:title' and name() != 'n:identifier']" />
    </fo:block>
  </xsl:template>

  <xsl:template name="all-emphasized" >
    <fo:block
        break-before="page"
        padding-top="230pt"
        text-align="left"
        font-style="italic"
    >
      <xsl:apply-templates/>
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
      <fo:inline>
        <xsl:if test="n:locutor" >
          <xsl:value-of select="n:locutor" />&nbsp;:
        </xsl:if>
      </fo:inline>
      <fo:inline
          text-align="justify"
          text-indent="1em"
      >
        <xsl:value-of select="$speech-symbol" />&nbsp;
        <xsl:apply-templates/>
      </fo:inline>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:locutor" />


  <xsl:template match="n:emphasis" >
    <fo:inline font-style="italic" ><xsl:apply-templates/></fo:inline>
  </xsl:template>

  <xsl:import href="general-punctuation.xsl" />
<!--

  <xsl:template match="n:quote" >&ldquo;<xsl:apply-templates/>&rdquo;</xsl:template>

  <xsl:template match="n:parenthesis" >(<xsl:apply-templates/>)</xsl:template>

  <xsl:template match="n:square-brackets" >[<xsl:apply-templates/>]</xsl:template>

  <xsl:template match="n:interpolatedclause" >&ndash;&nbsp;<xsl:apply-templates/>&nbsp;&ndash;</xsl:template>

  <xsl:template match="n:interpolatedclause-silentend" >&ndash;&nbsp;<xsl:apply-templates/></xsl:template>

  <xsl:template match="n:ellipsis-opening" >&hellip;</xsl:template>

  <xsl:template match="n:apostrophe-wordmate" >&rsquo;</xsl:template>

  <xsl:template match="n:sign-colon" >&nbsp;:</xsl:template>
  <xsl:template match="n:sign-semicolon" >&nbsp;;</xsl:template>
  <xsl:template match="n:sign-comma" >,</xsl:template>
  <xsl:template match="n:sign-ellipsis" >&hellip;</xsl:template>
  <xsl:template match="n:sign-exclamationmark" >&nbsp;!</xsl:template>
  <xsl:template match="n:sign-fullstop" >.</xsl:template>
  <xsl:template match="n:sign-questionmark" >&nbsp;?</xsl:template>
-->
</xsl:stylesheet>

