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
        </fo:simple-page-master>

      </fo:layout-master-set>

      <fo:page-sequence
          initial-page-number="1"
          master-reference="A4"
      >

        <fo:flow
            flow-name="xsl-region-body"
        >
          <fo:block>
            <xsl:apply-templates />
          </fo:block>
        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  <xsl:template match="n:chapter[n:title='Fonts']/n:section" >

    
    <xsl:variable name="font-family" select="n:title/n:quote" />
    <xsl:variable name="embed-file" select="n:title/n:hard-inline-literal" />
    <xsl:variable name="font-style" select="n:title/n:square-brackets[1]" />
    <xsl:variable name="font-weight" select="n:title/n:square-brackets[2]" />
    <xsl:variable name="priority" select="n:title/n:square-brackets[3]" />

    <fo:block
        padding-top="20pt"
        text-align="center"
        padding-bottom="5pt"
        font-weight="bold"
        keep-together="always"
    >
      <fo:block
          border-style="dotted"
          padding-top="5pt"
          padding-bottom="2pt"
      >
        <xsl:call-template name="font-synthetic-name" >
          <xsl:with-param name="font-family" ><xsl:value-of select="$font-family" /></xsl:with-param>
          <xsl:with-param name="font-style" ><xsl:value-of select="$font-style" /></xsl:with-param>
          <xsl:with-param name="font-weight" ><xsl:value-of select="$font-weight" /></xsl:with-param>
        </xsl:call-template>

        <fo:block
            font-family="monospace"
            font-size="60%"
        >
          <fo:block>
            <xsl:value-of select="$embed-file" />
          </fo:block>
<!--
          <fo:block>
            style=<xsl:value-of select="$font-style" />,
            weight=<xsl:value-of select="$font-weight" />,
            priority=<xsl:value-of select="$priority" />
          </fo:block>
-->
        </fo:block>
      </fo:block>
      <xsl:call-template name="characters" >
        <xsl:with-param name="font-family" ><xsl:value-of select="$font-family" /></xsl:with-param>
        <xsl:with-param name="font-style" ><xsl:value-of select="$font-style" /></xsl:with-param>
        <xsl:with-param name="font-weight" ><xsl:value-of select="$font-weight" /></xsl:with-param>
      </xsl:call-template>
    </fo:block>
    
  </xsl:template>

  <xsl:template name="characters" >

    <xsl:param name="font-family" />
    <xsl:param name="font-style" />
    <xsl:param name="font-weight" />
    <xsl:param name="font-size" >20</xsl:param>
    <xsl:param name="column-count" >30</xsl:param>

    <xsl:variable
        name="characters"
        select="//n:chapter[n:title='Characters']/n:paragraph-plain"
    />

    <fo:table >

      <xsl:for-each select="$characters[ position() &lt;= $column-count ]" >
        <fo:table-column/>
      </xsl:for-each>

      <fo:table-body>
          <xsl:for-each select="$characters[ ( position() mod $column-count = 1 )]">
            <xsl:variable name="row-index" select="position()" />
            <fo:table-row>
              <xsl:variable name="low-index" select="( $row-index - 1 ) * $column-count + 1" />
              <xsl:variable name="hi-index" select="$row-index * $column-count + 1" />

              <xsl:for-each
                  select="$characters[ ( position() &gt;= $low-index ) and ( position() &lt; $hi-index ) ]"
              >
                <fo:table-cell>
                  <fo:block>
                    <xsl:attribute name="font-family" >
                      <xsl:value-of select="$font-family" />
                    </xsl:attribute>
                    <xsl:attribute name="font-weight" >
                      <xsl:value-of select="$font-weight" />
                    </xsl:attribute>
                    <xsl:attribute name="font-style" >
                      <xsl:value-of select="$font-style" />
                    </xsl:attribute>
                    <xsl:value-of select="." />
                  </fo:block>
                </fo:table-cell>
                
              </xsl:for-each>
            </fo:table-row>
          </xsl:for-each>
      </fo:table-body>

    </fo:table>
  </xsl:template>

  <xsl:template match="n:paragraph-plain" mode="paragraph" >
    <fo:block>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:chapter/n:title" />

  <xsl:template match="n:chapter[n:title='Characters']" />

  <xsl:template name="font-synthetic-name" >
    <xsl:param name="font-family" />
    <xsl:param name="font-style" />
    <xsl:param name="font-weight" />
    <xsl:value-of select="$font-family" />

    <xsl:text> </xsl:text>
    <xsl:if test="$font-style='italic'" >italic</xsl:if>
    <xsl:call-template name="weight-name" >
      <xsl:with-param name="font-weight" >
        <xsl:value-of select="$font-weight" />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="weight-name" >
    <xsl:param name="font-weight" />
    <xsl:choose>
      <xsl:when test="$font-weight=200" >
        light
      </xsl:when>
      <xsl:when test="$font-weight=400" />
      <xsl:when test="$font-weight=700" >
        bold
      </xsl:when>
      <xsl:when test="$font-weight=800" >
        extra-bold
      </xsl:when>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>

