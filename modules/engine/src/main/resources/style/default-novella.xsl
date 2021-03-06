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
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <!-- Don't import default punctuation because it would add special space characters
       that are hard to distinguish from inside a text editor.
  -->
  
  <xsl:output method="text" omit-xml-declaration="yes" indent="no" />

  <xsl:param name="timestamp"/>
  <xsl:param name="wordcount"/>
  <xsl:param name="filename"/>




  <xsl:template match="/" >
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template name="metadata-header" >
  %% Generated on: <xsl:value-of select="$timestamp" />
  %%         from: <xsl:value-of select="$filename" />
  %%   word count: <xsl:value-of select="//n:meta/n:word-count" />
  </xsl:template>    


  <xsl:template match="n:level" >
<xsl:text> 

</xsl:text>
== <xsl:choose>
      <xsl:when test="n:level-title" > <xsl:apply-templates select="n:level-title" mode="header" /></xsl:when>
    </xsl:choose>
<xsl:apply-templates />
  </xsl:template>



  <xsl:template match="n:level/n:level" >
<xsl:text> 

</xsl:text>
=== <xsl:choose>
    <xsl:when test="n:level-title" > <xsl:apply-templates select="n:level-title" mode="header" /></xsl:when>
    </xsl:choose>
<xsl:apply-templates />
    </xsl:template>

  <xsl:template match="*" mode="header" >
    <xsl:apply-templates/>
  </xsl:template>


  <xsl:template match="n:paragraphs-inside-angled-bracket-pairs" >

&lt;&lt;<xsl:apply-templates/>

>>

  </xsl:template>

  <xsl:template match="n:lines-of-literal/n:raw-lines" >

&lt;&lt;&lt;
<xsl:apply-templates/>
>>>
  
  </xsl:template>

  <xsl:template match="n:block-of-literal-inside-grave-accents" >`<xsl:apply-templates/>`</xsl:template>

  <xsl:template match="n:paragraph-regular" >
<xsl:text> 

</xsl:text>
<xsl:apply-templates/>
  </xsl:template>




  <xsl:template match="n:url" >
<xsl:text>
</xsl:text>
<xsl:if test="n:block-inside-double-quotes" >  "<xsl:value-of select="n:block-inside-double-quotes" />"<xsl:text>  
</xsl:text>
</xsl:if><xsl:value-of select="n:url-literal" /><xsl:text>
</xsl:text>
  </xsl:template>


  <xsl:template match="n:list-with-triple-hyphen/n:paragraph-as-list-item" >

--- <xsl:apply-templates/></xsl:template>

  <xsl:template match="n:list-with-double-hyphen-and-number-sign/n:paragraph-as-list-item" >

--# <xsl:apply-templates/></xsl:template>


  <xsl:template match="n:paragraph-regular/n:embedded-list-with-hyphen" ><xsl:apply-templates/><xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="n:paragraph-regular/n:embedded-list-with-number-sign" ><xsl:apply-templates/><xsl:text>
</xsl:text>
</xsl:template>

  <xsl:template match="n:embedded-list-with-hyphen/n:embedded-list-item" >
- <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="n:embedded-list-with-hyphen/n:embedded-list-with-hyphen/n:embedded-list-item" >
  - <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="n:embedded-list-with-hyphen/n:embedded-list-with-hyphen/n:embedded-list-with-hyphen/n:embedded-list-item" >
    - <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="n:embedded-list-with-number-sign/n:embedded-list-item" >
# <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="n:embedded-list-with-number-sign/n:embedded-list-with-number-sign/n:embedded-list-item" >
  # <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="n:embedded-list-with-number-sign/n:embedded-list-with-number-sign/n:embedded-list-with-number-sign/n:embedded-list-item" >
    # <xsl:apply-templates/>
  </xsl:template>



  <xsl:template match="n:implicit-identifier" />
  <xsl:template match="n:explicit-identifier" />
  <xsl:template match="n:implicit-tag" />
  <xsl:template match="n:explicit-tag" />
  <xsl:template match="//n:meta/n:word-count" />
  <xsl:template match="n:level-title" />
  <xsl:template match="n:style" />
  
  <xsl:template match="n:block-inside-double-quotes" >"<xsl:apply-templates/>"</xsl:template>

  <xsl:template match="n:block-inside-solidus-pairs" >//<xsl:apply-templates/>//</xsl:template>

  <xsl:template match="n:block-inside-asterisk-pairs" >**<xsl:apply-templates/>**</xsl:template>

  <xsl:template match="n:block-inside-parenthesis" >(<xsl:apply-templates/>)</xsl:template>

  <xsl:template match="n:block-inside-square-brackets" >[<xsl:apply-templates/>]</xsl:template>

  <xsl:template match="n:block-inside-hyphen-pairs" >-- <xsl:apply-templates/> --</xsl:template>

  <xsl:template match="n:block-inside-two-hyphens-then-hyphen-low-line" >-- <xsl:apply-templates/> -_</xsl:template>
  
  <xsl:template match="n:block-of-literal-inside-grave-accents" >`<xsl:apply-templates/>`</xsl:template>
  
  <xsl:template match="n:block-of-literal-inside-grave-accent-pairs" >``<xsl:apply-templates/>``</xsl:template>
  
  <xsl:template match="n:block-after-tilde/n:subblock" >~<xsl:apply-templates mode="nospace" /></xsl:template>
  

  <xsl:template match="n:apostrophe-wordmate" >'</xsl:template>

  <xsl:template match="n:sign-colon" > :</xsl:template>
  <xsl:template match="n:sign-semicolon" > ;</xsl:template>
  <xsl:template match="n:sign-comma" >,</xsl:template>
  <xsl:template match="n:sign-ellipsis" >...</xsl:template>
  <xsl:template match="n:sign-exclamationmark" > !</xsl:template>
  <xsl:template match="n:sign-fullstop" >.</xsl:template>
  <xsl:template match="n:sign-questionmark" > ?</xsl:template>

  <xsl:template match="n:sign-colon" mode="nospace" >:</xsl:template>
  <xsl:template match="n:sign-semicolon" mode="nospace" >;</xsl:template>
  <xsl:template match="n:sign-exclamationmark" mode="nospace" >!</xsl:template>
  <xsl:template match="n:sign-questionmark" mode="nospace" >?</xsl:template>
  <xsl:template match="n:block-of-literal-inside-grave-accents" mode="nospace" >`<xsl:apply-templates/>`</xsl:template>
  <xsl:template match="n:block-of-literal-inside-grave-accent-pairs" mode="nospace" >``<xsl:apply-templates/>``</xsl:template>
  <xsl:template match="n:block-inside-parenthesis" mode="nospace" >(<xsl:apply-templates/>)</xsl:template>
  <xsl:template match="n:location" />


  <xsl:template match="text()" >
    <xsl:value-of select="." />
  </xsl:template>


</xsl:stylesheet>

