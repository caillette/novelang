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
          <fo:region-body margin-top="0.5cm" margin-bottom="1.1cm" />
        </fo:simple-page-master>

      </fo:layout-master-set>

      <fo:page-sequence
          initial-page-number="1"
          master-reference="A4"
      >

        <fo:flow
            flow-name="xsl-region-body"
        >
          <xsl:apply-templates />
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
        padding-top="0pt"
        text-align="center"
        padding-bottom="5pt"
        keep-together="always"
        >
      <fo:block
          border-style="dotted"
          padding-top="5pt"
          padding-bottom="6pt"
          font-weight="bold"
          space-after="10pt"
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
          <xsl:value-of select="$embed-file" />
        </fo:block>
      </fo:block>


      <xsl:call-template name="demo-sentences">
        <xsl:with-param name="font-family">
          <xsl:value-of select="$font-family"/>
        </xsl:with-param>
        <xsl:with-param name="font-style">
          <xsl:value-of select="$font-style"/>
        </xsl:with-param>
        <xsl:with-param name="font-weight">
          <xsl:value-of select="$font-weight"/>
        </xsl:with-param>
        <xsl:with-param name="font-size" >12</xsl:with-param>
        <xsl:with-param name="sentence" >The quick brown fox jumps over the lazy dog</xsl:with-param>
      </xsl:call-template>

      <fo:block
          space-before="10pt"
          space-after="10pt"
          margin-left="20pt"
          margin-right="20pt"
          text-align="center"
      >
        <xsl:call-template name="characters">
          <xsl:with-param name="font-family">
            <xsl:value-of select="$font-family"/>
          </xsl:with-param>
          <xsl:with-param name="font-style">
            <xsl:value-of select="$font-style"/>
          </xsl:with-param>
          <xsl:with-param name="font-weight">
            <xsl:value-of select="$font-weight"/>
          </xsl:with-param>
        </xsl:call-template>
      </fo:block>


    </fo:block>
    
  </xsl:template>

  <xsl:template name="characters" >

    <xsl:param name="font-family" />
    <xsl:param name="font-style" />
    <xsl:param name="font-weight" />
    <xsl:param name="font-size" >20</xsl:param>
    <xsl:param name="column-count" >16</xsl:param>

    <xsl:variable
        name="characters"
        select="//n:chapter[n:title='Characters']/n:paragraph-plain"
    />

    <fo:table
        border-collapse="collapse"    
    >

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
                <fo:table-cell
                    border="0.3pt solid grey"
                    padding="2pt"
                    overflow="hidden"
                    text-align="center"
                >
                  <!-- This value of text-indent fixes bad horizontal alignment. Why? -->
                  <fo:block text-indent="-18pt" >
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


  <xsl:template name="demo-sentences" >

    <xsl:param name="font-family" />
    <xsl:param name="font-style" />
    <xsl:param name="font-weight" />
    <xsl:param name="sentence" />
    <xsl:param name="font-size" >20</xsl:param>


    <fo:block
        text-align="left"    
    >
      <xsl:attribute name="font-family" >
        <xsl:value-of select="$font-family" />
      </xsl:attribute>
      <xsl:attribute name="font-size" >
        <xsl:value-of select="$font-size" />
      </xsl:attribute>
      <xsl:attribute name="font-weight" >
        <xsl:value-of select="$font-weight" />
      </xsl:attribute>
      <xsl:attribute name="font-style" >
        <xsl:value-of select="$font-style" />
      </xsl:attribute>
      <xsl:value-of select="$sentence" />
    </fo:block>


  </xsl:template>



</xsl:stylesheet>

