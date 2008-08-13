<?xml version="1.0"?>
<!DOCTYPE foo [

  <!ENTITY % Fo PUBLIC
      "http://www.w3.org/1999/XSL/Format"
      "/fo.dtd"
  >
  %Fo;

] >

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:exslt="http://exslt.org/common">
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


        <fo:static-content flow-name="xsl-region-after" >
          <fo:block
              font-size="20pt"    
          >
            <xsl:call-template name="display-page-number" >
              <xsl:with-param name="inside-content" >
                <fo:retrieve-marker
                    retrieve-class-name="inside-content"
                    retrieve-boundary="page"
                    retrieve-position="first-starting-within-page"
                />
              </xsl:with-param>
              <xsl:with-param name="page-number" >
                <fo:page-number/>
              </xsl:with-param>
            </xsl:call-template>
          </fo:block>
        </fo:static-content>

        <fo:flow flow-name="xsl-region-body">

          <fo:block background="#DDDDFF" >
            <!-- Use a marker to tell if current flow is real content. -->
            <fo:marker marker-class-name="inside-content" >true</fo:marker>

            <fo:block>
              Function "exsl:nodeSet" available=
              <xsl:value-of select="function-available('exslt:nodeSet')" />
            </fo:block>

            <fo:block>
              Function "exslt:node-set" available=
              <xsl:value-of select="function-available('exslt:node-set')" />
            </fo:block>

            <fo:block>
              Function "xsltc-extension:nodeset" available=
              <xsl:value-of select="function-available('xsltc-extension:nodeset')" />
            </fo:block>

            <fo:block>
              Function "xalan:nodeset" available=
              <xsl:value-of select="function-available('xalan:nodeset')" />
            </fo:block>

            <fo:block>
              Function "nodeset" available=
              <xsl:value-of select="function-available('nodeset')" />
            </fo:block>


            <xsl:apply-templates />

          </fo:block>

          <fo:block break-before="page" >
            This is the end of the document.
          </fo:block>
        </fo:flow>



      </fo:page-sequence>

    </fo:root>
  </xsl:template>


  <xsl:template name="display-page-number" >
    <xsl:param name="inside-content" />
    <xsl:param name="page-number" />

    <fo:block>
      copy-of $inside-content=<xsl:copy-of select="$inside-content" /> ;
    </fo:block>
    <fo:block>
      nodeset($inside-content)=<xsl:value-of select="xalan:nodeset($inside-content)" />;
    </fo:block>
    <fo:block>
      page-number=<xsl:copy-of select="$page-number" />
    </fo:block>

  </xsl:template>

  
  <xsl:template match="n:chapter" >
    <fo:block
        break-before="page"
    >

      <xsl:apply-templates />
    </fo:block>
  </xsl:template>


  <xsl:import href="pdf.xsl" />

</xsl:stylesheet>

