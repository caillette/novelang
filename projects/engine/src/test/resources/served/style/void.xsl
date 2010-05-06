<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>

  <xsl:template match="/" >

    <html>
      <head>
        <!-- Title used in tests, don't change -->
        <title>this is the void stylesheet</title>
      </head>
    <body>
      This page was generated using a stylesheet which renders no content.
      The purpose is to verify the use of requested stylesheet.
    </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
