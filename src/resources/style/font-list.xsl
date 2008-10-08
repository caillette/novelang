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
    xmlns:nf="http://novelang.org/font-list-xml/1.0"
>

  <xsl:template match="/" >
    <fo:root>

      <fo:layout-master-set>

        <fo:simple-page-master
            master-name="A4"
            page-width="210mm"   page-height="297mm"
            margin-top="10mm"     margin-bottom="10mm"
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


  <xsl:template match="nf:family" >
    
    <xsl:variable name="font-family" select="nf:name" />
    <xsl:variable name="embed-file" select="nf:embed-file" />
    <xsl:variable name="font-style" select="nf:style" />
    <xsl:variable name="font-weight" select="nf:weight" />

    <fo:block
        padding-top="0pt"
        text-align="center"
        padding-bottom="5pt"
        keep-together="always"
    >
      <fo:block
          border-style="dotted"
          padding-top="12pt"
          padding-bottom="8pt"
          font-weight="bold"
          space-after="10pt"
      >
        <fo:block
            font-size="18pt"
        >
          <xsl:call-template name="font-synthetic-name">
            <xsl:with-param name="font-family">
              <xsl:value-of select="$font-family" />
            </xsl:with-param>
            <xsl:with-param name="font-style">
              <xsl:value-of select="$font-style" />
            </xsl:with-param>
            <xsl:with-param name="font-weight">
              <xsl:value-of select="$font-weight" />
            </xsl:with-param>
          </xsl:call-template>
        </fo:block>


        <fo:block
            space-before="4pt"
            font-family="monospace"
            font-size="9pt"
        >
          <xsl:value-of select="$embed-file" />
        </fo:block>
      </fo:block>


      <fo:block
          space-before="15pt"
      >
        <xsl:call-template name="demo-sentences">
          <xsl:with-param name="font-family">
            <xsl:value-of select="$font-family" />
          </xsl:with-param>
          <xsl:with-param name="font-style">
            <xsl:value-of select="$font-style" />
          </xsl:with-param>
          <xsl:with-param name="font-weight">
            <xsl:value-of select="$font-weight" />
          </xsl:with-param>
          <xsl:with-param name="font-size">12</xsl:with-param>
        </xsl:call-template>
      </fo:block>


      <fo:block
          space-before="20pt"
          space-after="40pt"
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
        select="//nf:characters/nf:character"
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
                    border="0.5pt solid grey"
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
    <xsl:param name="font-size" >12</xsl:param>

    <xsl:variable name="sentences" select="//nf:sentences/nf:sentence" />

    <xsl:for-each select="$sentences" >
      <fo:block
          text-align="left"
      >
        <xsl:attribute name="font-family">
          <xsl:value-of select="$font-family" />
        </xsl:attribute>
        <xsl:attribute name="font-size">
          <xsl:value-of select="$font-size" />
        </xsl:attribute>
        <xsl:attribute name="font-weight">
          <xsl:value-of select="$font-weight" />
        </xsl:attribute>
        <xsl:attribute name="font-style">
          <xsl:value-of select="$font-style" />
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </fo:block>
    </xsl:for-each>


  </xsl:template>



</xsl:stylesheet>

