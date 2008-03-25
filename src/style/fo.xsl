<?xml version="1.0"?>
<!DOCTYPE foo
[
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
]
>
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
        <fo:simple-page-master
            page-width="210mm"
            page-height="297mm"
            margin-top="5mm"
            margin-left="45mm"
            margin-right="45mm"
            margin-bottom="7mm"
            master-name="PageMaster"
        >

          <fo:region-body
              margin-top="10mm"
              margin-bottom="15mm"
              column-count="1"
              column-gap="0mm"
          />

          <fo:region-before extent="10mm" />

          <fo:region-after extent="10mm"/>

        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence 
          initial-page-number="1" 
          master-reference="PageMaster"
      >
        <fo:static-content flow-name="xsl-region-before" >
          <fo:block text-align="right" >
            <fo:inline font-size="80%" >
              [<xsl:value-of select="$timestamp" />] &copy; Laurent Caillette 2002-2008
            </fo:inline>
          </fo:block>
        </fo:static-content>

        <fo:static-content flow-name="xsl-region-after" >
          <fo:block>
            <fo:leader 
                leader-pattern="rule" 
                rule-thickness="0.5pt" 
                leader-length="33%"
            />
          </fo:block>
          <fo:block text-align="left" >
            <fo:page-number/>
          </fo:block>
        </fo:static-content>

        <fo:flow
            flow-name="xsl-region-body"
            font-size="13pt"
            font-family="Georgia"
            line-height="18pt"
        >
          <xsl:apply-templates />
        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  <xsl:template match="n:chapter" >
    <fo:block
        break-before="page"
        padding-top="230pt" 

    >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="n:chapter/n:title" >
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

  <xsl:template match="n:section/n:title" >
    <fo:inline
        font-size="13pt"
        font-weight="bold"
        line-height="35pt"
        keep-with-next.within-page="always"
    >
      <xsl:apply-templates />
    </fo:inline>
  </xsl:template>

  <xsl:template match="n:blockquote" >
    <fo:block 
        text-align="justify"
        text-indent="0em"
        margin-left="30pt" 
        margin-right="10pt" 
        padding-before="6pt" 
        padding-after="8pt"
        font-size="12.5pt" 
        line-height="13pt" 
        keep-with-next.within-page="always"
    > 
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>


  <xsl:template match="n:paragraph-plain" >
    <fo:block
        text-indent="1em"
        text-align="justify"
    >
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:paragraph-speech" >
    <xsl:call-template name="speech" >
      <xsl:with-param name="speech-symbol" >&mdash;</xsl:with-param>
    </xsl:call-template>
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

  
  <xsl:template match="n:quote" >&ldquo;<xsl:apply-templates/>&rdquo;</xsl:template>

  <xsl:template match="n:emphasis" ><fo:inline font-style="italic" ><xsl:apply-templates/></fo:inline></xsl:template>

  <xsl:template match="n:parenthesis" >(<xsl:apply-templates/>)</xsl:template>

  <xsl:template match="n:square-brackets" >[<xsl:apply-templates/>]</xsl:template>

  <xsl:template match="n:interpolatedclause" >&ndash;&nbsp;<xsl:apply-templates/>&nbsp;&ndash;</xsl:template>

  <xsl:template match="n:interpolatedclause-silentend" >&ndash;&nbsp;<xsl:apply-templates/></xsl:template>

  <xsl:template match="n:ellipsis-opening" >&hellip;</xsl:template>

  <xsl:template match="n:apostrophe-wordmate" >'</xsl:template>

  <xsl:template match="n:sign-colon" >&nbsp;: </xsl:template>
  <xsl:template match="n:sign-comma" >, </xsl:template>
  <xsl:template match="n:sign-ellipsis" >&hellip;</xsl:template>
  <xsl:template match="n:sign-exclamationmark" >&nbsp;!</xsl:template>
  <xsl:template match="n:sign-fullstop" >.</xsl:template>
  <xsl:template match="n:sign-questionmark" >&nbsp;?</xsl:template>

</xsl:stylesheet>

