<?xml version="1.0"?>
<!DOCTYPE foo [

  <!ENTITY LandL "L &amp; L" >
  <!ENTITY Love1 "SAIMERENTBEAUCOUP" >
  <!ENTITY Love2 "ETNEURENTPASDENFANTS" >
  <!ENTITY GuineaPig "Mais un cochon d'Inde de luxe !" >

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
    xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
    xmlns:barcode="http://barcode4j.krysalis.org/ns"
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
          <fo:block-container
              absolute-position="fixed"
              top="50mm"
              left="61mm"
              fox:transform="rotate(10)"
          >
            <fo:block>
              <fo:instream-foreign-object>
                <barcode:barcode message="&LandL;">
                  <barcode:datamatrix>
                    <barcode:module-width>9.5mm</barcode:module-width>
                    <barcode:shape>force-square</barcode:shape>
                  </barcode:datamatrix>
                </barcode:barcode>
              </fo:instream-foreign-object>
            </fo:block>
          </fo:block-container>



          <fo:block space-before="210mm" text-align="center">
            <fo:block>
              <fo:instream-foreign-object>
                <barcode:barcode message="&Love1;">
                  <barcode:royal-mail-cbc>
                    <barcode:module-width>0.7mm</barcode:module-width>
                    <barcode:track-height>1.25mm</barcode:track-height>
                    <barcode:ascender-height>1.25mm</barcode:ascender-height>
                    <barcode:interchar-gap-width>1mw</barcode:interchar-gap-width>
                  </barcode:royal-mail-cbc>
                </barcode:barcode>
              </fo:instream-foreign-object>
            </fo:block>
            <fo:block>
              <fo:instream-foreign-object>
                <barcode:barcode message="&Love2;">
                  <barcode:royal-mail-cbc>
                    <barcode:module-width>0.5mm</barcode:module-width>
                    <barcode:track-height>1.25mm</barcode:track-height>
                    <barcode:ascender-height>1.25mm</barcode:ascender-height>
                    <barcode:interchar-gap-width>1mw</barcode:interchar-gap-width>
                  </barcode:royal-mail-cbc>
                </barcode:barcode>
              </fo:instream-foreign-object>
            </fo:block>

          </fo:block>

          <fo:block space-before="10mm" text-align="center" >
            <fo:instream-foreign-object>
              <barcode:barcode message="&GuineaPig;" >
                <barcode:pdf417>
                  <barcode:module-width>0.352777mm</barcode:module-width>
                  <!-- 1 pixel at 72dpi -->
                  <barcode:row-height>3mw</barcode:row-height>
                  <barcode:columns>2</barcode:columns>
                  <barcode:min-columns>2</barcode:min-columns>
                  <barcode:max-columns>2</barcode:max-columns>
                  <barcode:min-rows>3</barcode:min-rows>
                  <barcode:max-rows>90</barcode:max-rows>
                  <barcode:ec-level>0</barcode:ec-level>
                  <barcode:quiet-zone enabled="false" >0mw</barcode:quiet-zone>
                  <barcode:width-to-height-ratio>3.5</barcode:width-to-height-ratio>
                </barcode:pdf417>
              </barcode:barcode>
            </fo:instream-foreign-object> <fo:inline font-size="47pt">&#160;!</fo:inline>
          </fo:block>

        </fo:flow>

      </fo:page-sequence>

      <fo:page-sequence master-reference="A4" initial-page-number="2" >
        <fo:flow flow-name="xsl-region-body" >
          <fo:block text-align="center" font-size="20pt" >
            <fo:block padding-before="40mm" font-style="italic" >
              Traduction !
            </fo:block>
            <fo:block space-before="6mm" font-size="30pt" font-weight="bold" >
              &LandL;
            </fo:block>
            <fo:block space-before="6mm" font-weight="bold" >
              &Love1;
            </fo:block>
            <fo:block space-before="6mm" font-weight="bold" >
              &Love2;
            </fo:block>
            <fo:block space-before="4mm" font-weight="bold" >
              &GuineaPig;
            </fo:block>
          </fo:block>

        </fo:flow>
      </fo:page-sequence>
    </fo:root>

  </xsl:template>


</xsl:stylesheet>

