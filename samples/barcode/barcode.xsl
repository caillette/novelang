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
          <fo:block>
            The barcode:
            <fo:instream-foreign-object>
              <barcode:barcode message="123456789">
                <barcode:code128>
                  <barcode:height>20mm</barcode:height>
                </barcode:code128>
              </barcode:barcode>
            </fo:instream-foreign-object>
          </fo:block>
        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>


</xsl:stylesheet>

