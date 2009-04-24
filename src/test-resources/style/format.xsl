<?xml version="1.0"?>
<!DOCTYPE foo [

  <!ENTITY % Fo PUBLIC
      "http://www.w3.org/1999/XSL/Format"
      "/fo.dtd"
  >
  %Fo;

] >

<!-- Used by XsltNumberingTest, edit with care. -->

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:exslt="http://exslt.org/common"
    xmlns:nlx="xalan://novelang.rendering.xslt.Numbering"
 >
  <xsl:import href="default/pdf-default.xsl" />

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
          <fo:region-after extent="3cm" />
        </fo:simple-page-master>

      </fo:layout-master-set>

      <fo:page-sequence
          initial-page-number="1"
          master-reference="A4"
      >


        <!-- This prints page numbers only when they are content-related.
             It works because the "xsl-region-body" has set a marker containing
             current page number.
        -->
        <fo:static-content flow-name="xsl-region-after" >
          <fo:block
              font-size="20pt"    
          >
            <!-- <fo:page-number/> not allowed inside <fo:retrieve-marker> so use a variable. -->
            <xsl:variable name="page-number" ><fo:page-number/></xsl:variable>

            <fo:retrieve-marker
                retrieve-class-name="inside-content"
                retrieve-boundary="page"
                retrieve-position="first-starting-within-page"
            >
              <xsl:value-of select="$page-number" />
            </fo:retrieve-marker>

          </fo:block>
        </fo:static-content>

        <fo:flow flow-name="xsl-region-body">

          <fo:block>
            <!-- Use a marker to inject page number as we are in a block of real content. -->
            <fo:marker marker-class-name="inside-content" ><fo:page-number/></fo:marker>

            <!-- Now play with functions. -->

            <fo:block >
              First we check if our chapter-numbering function is present:
            </fo:block>

            <fo:block>
              Function "numberAsText" available=
              <xsl:value-of select="function-available('nlx:numberAsText')" />
            </fo:block>

            <fo:block>
              Function "formatDateTime" available=
              <xsl:value-of select="function-available('nlx:formatDateTime')" />
            </fo:block>

            <fo:block padding-before="10pt" >
              Now some dateTime formatting.
            </fo:block>
            <fo:block>
              $timestamp=<xsl:value-of select="nlx:formatDateTime( $timestamp, 'yyyy-MM-dd kk:mm') " />
            </fo:block>
            <fo:block>
              $timestamp as hex=<xsl:value-of select="nlx:formatDateTime( $timestamp, 'HEX')" />
            </fo:block>



            <fo:block padding-before="10pt" />


            <xsl:apply-templates />

          </fo:block>

          <fo:block break-before="page" >
            This is the end of the document. This page should have no number.
          </fo:block>
        </fo:flow>



      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  
  
  <xsl:template match="n:level" >
    <fo:block
        break-before="page"
    >
      <!-- Chapter index converted to text! -->
      <xsl:value-of select="nlx:numberAsText(position(),'EN','capital')" />:
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>



</xsl:stylesheet>

