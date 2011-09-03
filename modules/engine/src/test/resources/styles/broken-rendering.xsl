<?xml version="1.0"?>
<!--
  ~ Copyright (C) 2011 Laurent Caillette
  ~
  ~ This program is free software; you can redistribute it and/or
  ~ modify it under the terms of the GNU Lesser General Public
  ~ License as published by the Free Software Foundation, either
  ~ version 3 of the License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->


<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:nlx="xalan://org.novelang.rendering.xslt.Numbering"
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

  <xsl:template match="n:paragraph-regular" >
    <fo:block>
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


    <fo:block>
      <xsl:value-of select="nlx:formatDateTime( null, null ) " />
    </fo:block>

  </xsl:template>


</xsl:stylesheet>

