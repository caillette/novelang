<?xml version="1.0"?>
<!DOCTYPE foo [

  <!ENTITY % Fo PUBLIC
      "http://www.w3.org/1999/XSL/Format"
      "/fo.dtd"
  >
  %Fo;

] >

<!-- Used by NumberingTest, edit with care. -->    

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:exslt="http://exslt.org/common"
    xmlns:nlx="xalan://novelang.rendering.xslt.XsltFunctions"
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
          <xsl:apply-templates />

        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  <xsl:template match="n:paragraph-plain" >
    <fo:block
        text-align="justify"
       language="fr"
       hyphenate="true"
    >
      Lorem ipsum dolor sit amet, consectetuer adipiscingelit. Nullaaccumsandoloratrisus.
      Mauriseratorci, egestassed, suscipitut, ultriciesut, nulla. Donecatvelit.
      Proinconsequatvelitatdolor. Donecutnullainlacussodalesfringilla.
      Maurisnecfelisvelnisifeugiatpellentesque. Loremipsumdolorsitamet, consectetueradipiscingelit.
      Vivamuslobortisodiositametmimollistempor.
      Aliquamliberodui, consequatvel, fringillavel, hendreritquis, velit.
      Morbivarius, dolorquisinterdumaliquet, magnapurussodalesorci, aplaceratligulanibhadui.
      Maurisvitaeelit. Praesentvehiculapharetrametus. Aeneancongue, eroseuhendreritpretium,
      quamlacussuscipitest, vitaevulputateturpislectusquispede.
      Fuscesedduisedarcususcipitscelerisque. Crastempor, dolorvitaeullamcorperaliquet,
      nislenimadipiscingtortor, invulputatevelitmassasedipsum. Curabituregeteros.
      Pellentesquelobortis, liberovelconsequatfermentum, dolorvelitconsectetuermagna,
      nonvulputatepurusmassavitaelibero. Duisporta. Nameulacus. Suspendissedictum,
      augueegetcondimentumviverra, duivelitmollispurus, velvolutpatodiotellusutorci.
    </fo:block>

  </xsl:template>

</xsl:stylesheet>

