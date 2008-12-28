<?xml version="1.0"?>

<!-- Used in XslWriterTest.
     Contains a broken XPath: element name in Novelang namespace has no match in NodeKind.
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:template match="n:has-no-corresponding-nodekind" />

</xsl:stylesheet>