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

<!DOCTYPE stylesheet
[
  <!-- Tweaked entities: we want them to appear verbatim in resulting document. -->

  <!ENTITY mdash  "&amp;amp;mdash;" >
  <!ENTITY ndash  "&amp;amp;ndash;" >
  <!ENTITY hellip "&amp;amp;hellip;" >
  <!ENTITY raquo  "&amp;amp;raquo;" >
  <!ENTITY ldquo  "&amp;amp;ldquo;" >
  <!ENTITY rdquo  "&amp;amp;rdquo;" >
  <!ENTITY rsquo  "&amp;amp;rsquo;" >
  <!ENTITY nbsp   "&amp;amp;nbsp;" >
]
>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://novelang.org/book-xml/1.0"
>
  <xsl:import href="punctuation-US-EN.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  <xsl:param name="charset"/>

  <xsl:output method="xml" />

  <xsl:template match="/" >

    <html> 
      <head>
        <link rel="stylesheet" type="text/css" href="/display.css" />
        <xsl:element name="meta" >
          <xsl:attribute name="http-equiv" >content-type</xsl:attribute>
          <xsl:attribute name="content" >text/html;charset=<xsl:value-of select="$charset" /></xsl:attribute>
        </xsl:element>

        <meta name="Generator" content="Novelang" />
        <title><xsl:value-of select="$filename" /></title>
        <style type="text/css" />

      </head>
    <body>

    <xsl:apply-templates />

    </body>
    </html>
  </xsl:template>


  <xsl:template match="n:level/n:level-title" >
    <h1>&amp;lt;h1&amp;gt;<xsl:apply-templates />&amp;lt;/h1&amp;gt;</h1>
  </xsl:template>

  <xsl:template match="n:level/n:level" >
    <div class="section" >
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="n:level/n:level/n:level-title" >
    <h2>&amp;lt;p&amp;gt;&amp;lt;b&amp;gt;<xsl:apply-templates />&amp;lt;/b&amp;gt;&amp;lt;/p&amp;gt;</h2>
  </xsl:template>

  <xsl:template match="n:paragraphs-inside-angled-bracket-pairs" >
<tt>&amp;lt;blockquote&amp;gt;</tt><xsl:apply-templates/><tt>&amp;lt;/blockquote&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:lines-of-literal" >
<tt>&amp;lt;pre&amp;gt;</tt><pre><xsl:value-of select="n:raw-lines"/></pre><tt>&amp;lt;/pre&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-regular" >
<tt>&amp;lt;p&amp;gt;</tt><p><xsl:apply-templates/></p><tt>&amp;lt;/p&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:url" >
    <!--<a><xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute><tt>&amp;lt;a href="<xsl:value-of select="." />"&amp;gt;<xsl:value-of select="." />&amp;lt;/a&amp;gt;</tt></a>-->
    <a><xsl:attribute name="href"><xsl:value-of select="n:url-literal" /></xsl:attribute>
      <tt>&amp;lt;a href="<xsl:value-of select="n:url-literal" />"&amp;gt;
      <xsl:choose>
        <xsl:when test="n:block-inside-double-quotes" >
          <xsl:apply-templates select="n:block-inside-double-quotes/node()" />
        </xsl:when>
        <xsl:when test="n:block-inside-square-brackets" >
          <xsl:apply-templates select="n:block-inside-square-brackets/node()" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="n:url-literal" />
        </xsl:otherwise>
      </xsl:choose>
      &amp;lt;/a&amp;gt;</tt>
    </a>
  </xsl:template>

  <xsl:template match="n:paragraph-as-list-item" >
<tt>&amp;lt;p&amp;gt;</tt><p><tt>&mdash;&nbsp;</tt><xsl:apply-templates/></p><tt>&amp;lt;/p&amp;gt;</tt>
  </xsl:template>
  

  <xsl:template match="n:cell-rows-with-vertical-line" >
    &amp;lt;table&amp;gt;
      <xsl:apply-templates />
    &amp;lt;/table&amp;gt;
  </xsl:template>

  <xsl:template match="n:cell-row" >
    &amp;lt;tr&amp;gt;
      <xsl:apply-templates/>
    &amp;lt;/tr&amp;gt;
  </xsl:template>

  <xsl:template match="n:cell" >
    &amp;lt;td&amp;gt;
      <xsl:apply-templates/>
    &amp;lt;/td&amp;gt;
  </xsl:template>


  <xsl:template match="n:list-with-triple-hyphen" >
    &amp;lt;ul&amp;gt;
      <xsl:for-each select="n:paragraph-as-list-item" >
        &amp;lt;li&amp;gt;<xsl:apply-templates/>&amp;lt;/li&amp;gt;
      </xsl:for-each>
    &amp;lt;/ul&amp;gt;
  </xsl:template>

  <xsl:template match="n:embedded-list-with-hyphen" >
    &amp;lt;ul&amp;gt;
      <xsl:apply-templates/>
    &amp;lt;/ul&amp;gt;
  </xsl:template>

  <xsl:template match="n:embedded-list-item" >
    &amp;lt;li&amp;gt;
      <xsl:apply-templates/>
    &amp;lt;/li&amp;gt;
  </xsl:template>



  
  <xsl:template match="n:block-inside-double-quotes" ><tt>&ldquo;</tt><xsl:apply-templates/><tt>&rdquo;</tt></xsl:template>

  <xsl:template match="n:block-inside-solidus-pairs" ><tt>&amp;lt;em&amp;gt;</tt><xsl:apply-templates/><tt>&amp;lt;/em&amp;gt;</tt></xsl:template>

  <xsl:template match="n:word-after-circumflex-accent" >
    <tt>&amp;lt;sup&amp;gt;</tt><xsl:apply-templates/><tt>&amp;lt;/sup&amp;gt;</tt>
  </xsl:template>

  <xsl:template match="n:block-inside-hyphen-pairs" ><tt>&ndash;&nbsp;</tt><xsl:apply-templates/><tt>&nbsp;&ndash;</tt></xsl:template>

  <xsl:template match="n:block-inside-two-hyphens-then-hyphen-low-line" ><tt>&ndash;&nbsp;</tt><xsl:apply-templates/></xsl:template>

  <xsl:template match="n:block-of-literal-inside-grave-accent-pairs" ><tt>&amp;lt;code&amp;gt;</tt><code><xsl:apply-templates/></code><tt>&amp;lt;/code&amp;gt;</tt></xsl:template>

  <xsl:template match="n:apostrophe-wordmate" ><tt>&rsquo;</tt></xsl:template>

  <xsl:template match="n:location" />
  <xsl:template match="n:implicit-identifier" />
  <xsl:template match="n:explicit-identifier" />
  <xsl:template match="n:implicit-tag" />
  <xsl:template match="n:explicit-tag" />


</xsl:stylesheet>
