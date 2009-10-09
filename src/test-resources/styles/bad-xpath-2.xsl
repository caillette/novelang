<?xml version="1.0"?>

<!-- Used in XslWriter test.
     Imports a broken stylesheet so we can see if XPath expressions are correctly checked
     in imports.
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:import href="bad-xpath-1.xsl" />
  <xsl:template match="/" />
</xsl:stylesheet>