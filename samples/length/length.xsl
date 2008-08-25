<?xml version="1.0"?>
<!DOCTYPE foo [

  <!ENTITY % Fo PUBLIC
      "http://www.w3.org/1999/XSL/Format"
      "/fo.dtd"
  >
  %Fo;

] >

<!-- Demo of the Length class providing methods for counting characters.
     The ultimate goal is to detect when a paragraph (or blockquote, or whatever at the same
     level) and its following siblings contain a number of character under a given threshold.
     This is useful for emulating orphan control as Apache FOP 0.95 doesn't seem to work well.
-->

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:exslt="http://exslt.org/common"
    xmlns:nlx="xalan://novelang.rendering.xslt"
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
          <fo:region-before extent="1cm" />
          <fo:region-after extent="1cm" />
        </fo:simple-page-master>

      </fo:layout-master-set>

      <fo:page-sequence
          initial-page-number="1"
          master-reference="A4"
      >

        <fo:static-content flow-name="xsl-region-after" >
          <fo:block/>
        </fo:static-content>

        <fo:flow
            flow-name="xsl-region-body"
        >
          <fo:block>
            Function "Length.countCharactersOfSelfAndFollowingSiblings" available=
            <xsl:value-of select="function-available('nlx:Length.countCharactersOfSelfAndFollowingSiblings')" />
          </fo:block>
          <fo:block>
            Function "Length.positionUnderCharacterRemainderThreshold" available=
            <xsl:value-of select="function-available('nlx:Length.positionUnderCharacterRemainderThreshold')" />
          </fo:block>

          <xsl:apply-templates />

        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  <xsl:template match="n:chapter" >

    <fo:block space-before="10pt" font-weight="bold" >
      "<xsl:value-of select="n:title" />"
    </fo:block>
    
    <fo:block>
      Total:
      <xsl:value-of
          select="nlx:Length.countCharactersOfSelfAndFollowingSiblings(./n:paragraph-plain | ./n:title)"
      />

      <xsl:variable
          name="breakafter"
          select="nlx:Length.positionUnderCharacterRemainderThreshold(*, 8)"
      />

      <fo:block font-size="8pt">
        $breakafter=<xsl:value-of select="$breakafter" />
      </fo:block>
      <fo:block font-size="8pt">
        <xsl:value-of select="nlx:Numbering.asString('breakAfter', $breakafter)"/>
      </fo:block>

      <xsl:choose>
        <xsl:when test="$breakafter = -1">
          <fo:block background-color="lightblue">
            <xsl:apply-templates />
          </fo:block>
        </xsl:when>
        <xsl:otherwise>
          <fo:block background-color="grey">
            <xsl:apply-templates select="*[position() &lt;= $breakafter]" />
          </fo:block>
          <fo:block background-color="yellow">
            <xsl:apply-templates select="./*[position() &gt; $breakafter]" />
          </fo:block>
        </xsl:otherwise>
      </xsl:choose>

      <!--
          <fo:block font-size="8pt" space-before="15pt" >
            positionUCRT(8)=
            <xsl:value-of select="$break-after" /> ;
          </fo:block>

          <fo:block space-before="4pt">
            <xsl:for-each select="./*">

              <fo:block font-size="7pt" >
                "<xsl:value-of select="." />" count=
                <xsl:value-of select="nlx:Length.countCharactersOfSelfAndFollowingSiblings(.)" /> ;
                position= <xsl:value-of select="position()" />
              </fo:block>

              <xsl:choose>
                <xsl:when test="8 > nlx:Length.countCharactersOfSelfAndFollowingSiblings(.)">
                  <fo:block background-color="yellow">
                    <xsl:apply-templates />
                  </fo:block>
                </xsl:when>
                <xsl:otherwise>
                  <fo:block background-color="grey">
                    <xsl:apply-templates />
                  </fo:block>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
          </fo:block>
      -->
    </fo:block>


  </xsl:template>

  <xsl:template match="n:paragraph-plain" >
    <fo:block>
      <xsl:apply-templates />
    </fo:block>

  </xsl:template>

</xsl:stylesheet>

