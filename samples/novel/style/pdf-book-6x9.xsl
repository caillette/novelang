<?xml version="1.0"?>
<!DOCTYPE foo [

  <!ENTITY % ISOnum PUBLIC
      "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML"
      "ISOnum.pen"
  >
  %ISOnum;
  <!ENTITY % ISOpub PUBLIC
      "ISO 8879:1986//ENTITIES Publishing//EN//XML"
      "ISOpub.pen"
  >

  %ISOpub;
  <!ENTITY % ISOlat1 PUBLIC
      "ISO 8879:1986//ENTITIES Added Latin 1//EN//XML"
      "ISOlat1.pen"
  >
  %ISOlat1;

  <!ENTITY % Fo PUBLIC
      "http://www.w3.org/1999/XSL/Format"
      "fo.dtd"
  >
  %Fo;


  <!ENTITY % Book6x9 PUBLIC
      "http://novelang.sourceforge.net/pdf-book-6x9"
      "pdf-book-6x9.ent"
  >
  %Book6x9; 


] >

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:n="http://novelang.org/book-xml/1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:xsltc-extension="http://xml.apache.org/xalan/xsltc"
    xmlns:nlx="xalan://novelang.rendering.xslt"    
>

  <xsl:import href="punctuation-FR.xsl" />

  <xsl:param name="timestamp"/>
  <xsl:param name="filename"/>
  
  <xsl:variable
      name="region-body-height-pt"
      select="number( ( ( &region-body-line-count; ) * &line-height-pt; ) )"
  />
  
  <xsl:variable
      name="region-body-margin-bottom-pt"
      select="number( &page-height-pt; - $region-body-height-pt - &margin-top-pt; - &region-body-margin-top-pt; - &margin-bottom-pt; )"
  />

  <xsl:variable
      name="chapter-header-height-pt"
      select="number( ( ( &chapter-skipped-lines-at-start; ) * &line-height-pt; ) )"
  />

  <xsl:variable
      name="chapter-title-space-before-pt"
      select="number( $chapter-header-height-pt - &chapter-title-line-height-pt; - &chapter-title-padding-bottom-pt; )"
  />


  <xsl:template match="/" >
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" >

      <fo:layout-master-set>

        <fo:page-sequence-master master-name="title-master">        
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference
                blank-or-not-blank="not-blank"                
                master-reference="title-first"
            />          
            <fo:conditional-page-master-reference
                blank-or-not-blank="blank"
                master-reference="blank-page"
            />
          </fo:repeatable-page-master-alternatives>                 
        </fo:page-sequence-master>


        <fo:page-sequence-master master-name="unnumbered-master">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference
                odd-or-even="odd"
                blank-or-not-blank="not-blank"                
                master-reference="unnumbered-odd"
            />
            <fo:conditional-page-master-reference
                odd-or-even="even"
                blank-or-not-blank="not-blank"                
                master-reference="unnumbered-even"
            />
            <fo:conditional-page-master-reference
                blank-or-not-blank="blank"
                master-reference="blank-page"
            />
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>

        <fo:page-sequence-master master-name="chapter-master">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference
                odd-or-even="odd"
                blank-or-not-blank="not-blank"                
                master-reference="chapter-odd"
            />
            <fo:conditional-page-master-reference
                odd-or-even="even"
                blank-or-not-blank="not-blank"
                master-reference="chapter-even"
            />
            <fo:conditional-page-master-reference
                blank-or-not-blank="blank"
                master-reference="blank-page"
            />
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>


        <!-- simple page masters -->

        <fo:simple-page-master
            master-name="title-first"
            page-width="&page-width-pt;pt"       page-height="&page-height-pt;pt"
            margin-top="&margin-top-pt;pt"       margin-bottom="&margin-bottom-pt;pt"
            margin-right="&margin-outside-pt;pt" margin-left="&margin-outside-pt;pt"
        >
          <fo:region-body/>
        </fo:simple-page-master>

        <fo:simple-page-master
            master-name="blank-page"
            page-width="&page-width-pt;pt"       page-height="&page-height-pt;pt"
            margin-top="&margin-top-pt;pt"       margin-bottom="&margin-bottom-pt;pt"
            margin-right="&margin-outside-pt;pt" margin-left="&margin-outside-pt;pt"
        >
          <fo:region-body/>
        </fo:simple-page-master>

        <fo:simple-page-master
            master-name="chapter-odd"
            page-width="&page-width-pt;pt"       page-height="&page-height-pt;pt"
            margin-top="&margin-top-pt;pt"       margin-bottom="&margin-bottom-pt;pt"
            margin-left="&margin-inside-pt;pt"   margin-right="&margin-outside-pt;pt"
        >
          <fo:region-body 
              margin-top="&region-body-margin-top-pt;pt"
              margin-left="&region-body-margin-left-pt;pt"
              margin-right="&region-body-margin-right-pt;pt"
          >
            <xsl:attribute name="margin-bottom" >
              <xsl:value-of select="$region-body-margin-bottom-pt" />pt
            </xsl:attribute>
          </fo:region-body>
          <fo:region-before extent="&region-before-extent-pt;pt" />
          <fo:region-after
              region-name="region-after"
              extent="&region-after-extent-pt;pt"
          />
        </fo:simple-page-master>

        <fo:simple-page-master
            master-name="chapter-even"
            page-width="&page-width-pt;pt"       page-height="&page-height-pt;pt"
            margin-top="&margin-top-pt;pt"       margin-bottom="&margin-bottom-pt;pt"
            margin-right="&margin-inside-pt;pt"  margin-left="&margin-outside-pt;pt"
        >
          <fo:region-body
              margin-top="&region-body-margin-top-pt;pt"
              margin-left="&region-body-margin-left-pt;pt"
              margin-right="&region-body-margin-right-pt;pt"
          >
            <xsl:attribute name="margin-bottom" >
              <xsl:value-of select="$region-body-margin-bottom-pt" />pt
            </xsl:attribute>
          </fo:region-body>
          <fo:region-before extent="&region-before-extent-pt;pt" />
          <fo:region-after
              region-name="region-after"
              extent="&region-after-extent-pt;pt"
          />
        </fo:simple-page-master>

        <fo:simple-page-master
            master-name="unnumbered-odd"
            page-width="&page-width-pt;pt"       page-height="&page-height-pt;pt"
            margin-top="&margin-top-pt;pt"       margin-bottom="&margin-bottom-pt;pt"
            margin-left="&margin-inside-pt;pt"   margin-right="&margin-outside-pt;pt"
        >
          <fo:region-body
              margin-top="&region-body-margin-top-pt;pt"
              margin-left="&region-body-margin-left-pt;pt"
              margin-right="&region-body-margin-right-pt;pt"
          >
            <xsl:attribute name="margin-bottom" >
              <xsl:value-of select="$region-body-margin-bottom-pt" />pt
            </xsl:attribute>
          </fo:region-body>
          <fo:region-before extent="&region-before-extent-pt;pt" />
          />
        </fo:simple-page-master>

        <fo:simple-page-master
            master-name="unnumbered-even"
            page-width="&page-width-pt;pt"       page-height="&page-height-pt;pt"
            margin-top="&margin-top-pt;pt"       margin-bottom="&margin-bottom-pt;pt"
            margin-right="&margin-inside-pt;pt" margin-left="&margin-outside-pt;pt"
        >
          <fo:region-body
              margin-top="&region-body-margin-top-pt;pt"
              margin-left="&region-body-margin-left-pt;pt"
              margin-right="&region-body-margin-right-pt;pt"
          >
            <xsl:attribute name="margin-bottom" >
              <xsl:value-of select="$region-body-margin-bottom-pt" />pt
            </xsl:attribute>
          </fo:region-body>
          <fo:region-before extent="&region-before-extent-pt;pt" />
        </fo:simple-page-master>

      </fo:layout-master-set>


      <!-- White page. -->
      
      <fo:page-sequence 
          master-reference="unnumbered-master" 
          initial-page-number="auto-odd"
      >
        <fo:flow flow-name="xsl-region-body" >
          <xsl:if test="&interior-debug; >= 1" >
            <xsl:call-template name="interior-debug" />
          </xsl:if>
          <fo:block/>
        </fo:flow>
      </fo:page-sequence>
    
      <!-- Fake title page. -->
      
      <fo:page-sequence 
          master-reference="unnumbered-master" 
          initial-page-number="auto-odd"
      >
        <fo:flow flow-name="xsl-region-body" >
          <xsl:call-template name="fake-title-page" />         
        </fo:flow>
      </fo:page-sequence>
    
      <!-- Legal stuff. -->
      
      <fo:page-sequence 
          master-reference="unnumbered-master" 
          initial-page-number="auto-even"
      >
        <fo:flow flow-name="xsl-region-body" >
          <xsl:call-template name="legal-prologue" />         
        </fo:flow>
      </fo:page-sequence>
    
      <!-- Full title page. -->

      <fo:page-sequence 
          initial-page-number="auto-odd"
          master-reference="title-master"
      >
        <fo:flow
            flow-name="xsl-region-body"
        >
          <xsl:call-template name="full-title-page" />         
        </fo:flow>
      
      </fo:page-sequence>


      <!-- Chapters -->

      <fo:page-sequence
          initial-page-number="auto-odd"
          master-reference="chapter-master"
      >

        <fo:static-content flow-name="xsl-region-before" >
          <fo:block 
              font-family="&font-family;"
              font-size="&font-size-pt;pt"
              line-height="&line-height-pt;pt"
          />
        </fo:static-content>

        <fo:static-content flow-name="region-after" >
          <fo:block 
              text-align="center" 
              font-family="&font-family;"
              font-size="&font-size-pt;pt"
          >
            <fo:page-number/>
          </fo:block>
        </fo:static-content>

        <fo:flow
            flow-name="xsl-region-body"
        >
          <!-- Default format for all chapters text. -->
          <fo:block 
              font-family="&font-family;"
              font-size="&font-size-pt;pt"
              text-align="justify"
              line-height="&line-height-pt;pt"
              line-height-shift-adjustment="disregard-shifts"
              language="&language;"
          > 
            <xsl:apply-templates select="//n:chapter[not(n:style!='')]" /> 
          </fo:block>

        </fo:flow>

      </fo:page-sequence>


      <!-- White page, avoid chapter even page facing next sequence. -->
      
      <fo:page-sequence 
          master-reference="unnumbered-master" 
          initial-page-number="auto-odd"
      >
        <fo:flow flow-name="xsl-region-body" >
          <fo:block/>
        </fo:flow>
      </fo:page-sequence>
    

      
      <!-- Table of contents -->
      
      <fo:page-sequence 
          initial-page-number="auto-odd"
          master-reference="unnumbered-master"
      >
        <fo:flow
            flow-name="xsl-region-body"
        >
          <fo:block 
              font-family="&font-family;"
              font-size="&font-size-pt;pt"
              text-align="justify"
              line-height="&line-height-pt;pt"
              line-height-shift-adjustment="disregard-shifts"
              language="&language;"
          > 
            <xsl:call-template name="table-of-contents" />
          </fo:block>
        </fo:flow>
      
      </fo:page-sequence>

      <!-- Acknowledgements -->
      
      <fo:page-sequence 
          initial-page-number="auto-even"
          master-reference="unnumbered-master"
      >
        <fo:flow
            flow-name="xsl-region-body"
        >
          <fo:block 
              font-family="&font-family;"
              font-size="&font-size-pt;pt"
              text-align="justify"
              line-height="&line-height-pt;pt"
              line-height-shift-adjustment="disregard-shifts"
              language="&language;"
          > 
            <xsl:call-template name="acknowledgements" />
          </fo:block>
        </fo:flow>
      
      </fo:page-sequence>


      <!-- Bibliography -->
      
      <fo:page-sequence 
          initial-page-number="auto-odd"
          master-reference="unnumbered-master"
      >
        <fo:flow
            flow-name="xsl-region-body"
        >
          <fo:block 
              font-family="&font-family;"
              font-size="&font-size-pt;pt"
              text-align="justify"
              line-height="&line-height-pt;pt"
              line-height-shift-adjustment="disregard-shifts"
              language="&language;"
          > 
            <xsl:call-template name="bibliography" />
          </fo:block>
        </fo:flow>
      
      </fo:page-sequence>
      
      <!-- Legal epilogue. -->
      
      <fo:page-sequence 
          master-reference="unnumbered-master" 
          initial-page-number="auto-odd"
      >
        <fo:flow flow-name="xsl-region-body" >
          <xsl:call-template name="&legal-epilogue-stylename;" />         
        </fo:flow>
      </fo:page-sequence>
    
      
      
      <!-- White page. -->
      
      <fo:page-sequence 
          master-reference="unnumbered-master" 
          initial-page-number="auto-even"
      >
        <fo:flow flow-name="xsl-region-body" >
          <fo:block/>
        </fo:flow>
      </fo:page-sequence>
    
      

    </fo:root>
  </xsl:template>


  <xsl:template name="fake-title-page" >
    <fo:block
        text-align="&fake-title-text-align;"
        padding-top="&fake-title-padding-top-pt;pt"
        font-family="&fake-title-font-family;"
        font-size="&fake-title-font-size-pt;pt"
        font-weight="&fake-title-font-weight;"
        font-style="&fake-title-font-style;"        
    >
      &fake-title-text;
    </fo:block>
  </xsl:template>
  
  <xsl:template name="legal-prologue" >
    <fo:block
        text-align="&legal-prologue-text-align;"
        font-family="&legal-font-family;"
        line-height="&legal-prologue-line-height;"
        font-size="&legal-prologue-font-size-pt;pt"
    >
      <fo:block
          padding-top="&legal-prologue-author-www-padding-top-pt;pt"
          margin-left="&legal-prologue-margin-left-pt;pt"
      >
        &author-www-url;
      </fo:block>
      <fo:block
          padding-top="&legal-prologue-legal-space-before-pt;pt"
          margin-left="&legal-prologue-margin-left-pt;pt"
      >
        <fo:block>
          &legal-isbn-label; &legal-isbn-value;
        </fo:block>

      </fo:block>

      <fo:table>
        <fo:table-column column-width="70%"/>
        <fo:table-column column-width="30%"/>

        <fo:table-body alignment-baseline="baseline">
          <fo:table-row>
            <fo:table-cell>
              <fo:block margin-left="&legal-prologue-margin-left-pt;pt" >
                <fo:inline
                    >
                  &legal-copyright-label; &legal-copyright-text;
                </fo:inline>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell>
              <fo:block text-align="right" >
                <fo:inline
                    font-size="&timestamp-font-size-pt;"
                    font-weight="&timestamp-font-weight;"
                    font-style="&timestamp-font-style;"
                    >
                  <xsl:value-of select="nlx:Numbering.formatDateTime( $timestamp, 'BASE36')"/>
                </fo:inline>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>

      </fo:table>
    </fo:block>


    
  </xsl:template>
  

  <xsl:template name="full-title-page" >
    <fo:block
        text-align="&title-page-align;"
        margin-right="&title-page-margin-right-pt;pt"
    >
      <fo:block 
          padding-top="&author-padding-top-pt;pt"
          font-size="&author-font-size-pt;pt"
          font-family="&author-font-family;"
          font-style="&author-font-style;"
          font-weight="&author-font-weight;"
      >
        &title-authorname;
      </fo:block>

      <fo:block 
          padding-top="&title1-padding-top-pt;pt"
          font-size="&title1-font-size-pt;pt"
          font-family="&title1-font-family;"
          font-style="&title1-font-style;"
          font-weight="&title1-font-weight;"
      >
        &title-1;
      </fo:block>

      <fo:block 
          padding-top="&title2-padding-top-pt;pt"
          font-size="&title2-font-size-pt;pt"
          font-family="&title1-font-family;"
          font-style="&title1-font-style;"
          font-weight="&title1-font-weight;"
      >
        &title-2;
      </fo:block>

    </fo:block>
    
  </xsl:template>

  <xsl:template match="n:chapter" >
    <!-- padding-top required here instead of space-before. -->
    <fo:block
        page-break-before="right"
        id="{generate-id()}"
        hyphenate="true"
        hyphenation-push-character-count="&hyphenation-push-character-count;"
        hyphenation-remain-character-count="&hyphenation-remain-character-count;"
        widows="&chapter-widows;" 
        orphans="&chapter-orphans;"
    >
      <xsl:attribute name="padding-top" >
        <xsl:value-of select="$chapter-title-space-before-pt" />pt
      </xsl:attribute>
      <fo:block
        line-height="&chapter-title-line-height-pt;pt"
        font-family="&chapter-title-font-family;"
        font-size="&chapter-title-font-size-pt;pt"
        font-style="&chapter-title-font-style;"
        font-weight="&chapter-title-font-weight;"
        text-align="&chapter-title-text-align;"
      >
        <xsl:attribute name="space-after.minimum" >
          <xsl:value-of select="&chapter-title-padding-bottom-pt;" />pt
        </xsl:attribute>
        <xsl:attribute name="space-after.maximum" >
          <xsl:value-of select="&chapter-title-padding-bottom-pt;" />pt
        </xsl:attribute>
        <xsl:attribute name="space-after.optimum" >
          <xsl:value-of select="&chapter-title-padding-bottom-pt;" />pt
        </xsl:attribute>
        <xsl:value-of
            select="nlx:Numbering.numberAsText(position(),'FR','capital')" 
        />
      </fo:block>
                  
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:title" />

  <xsl:template match="n:identifier" />

  <xsl:template match="n:section" >
    <fo:block 
        space-before.minimum="&section-space-before-minimum-pt;pt"
        space-before.maximum="&section-space-before-maximum-pt;pt"
        space-before.optimum="&section-space-before-optimum-pt;pt"
        space-after.minimum="&section-space-after-minimum-pt;pt"
        space-after.maximum="&section-space-after-maximum-pt;pt"
        space-after.optimum="&section-space-after-optimum-pt;pt"
    >      
      <xsl:if test="contains( n:title, 'KEEPTOGETHER' )" >
        <xsl:attribute name="keep-together.within-page">always</xsl:attribute>
      </xsl:if>

      <xsl:apply-templates/>
      
      <xsl:if test="not( contains( following-sibling::*[1]/n:title, 'NODELIMITER') ) and ( position() &lt; last() )" >
        <fo:block
          text-align="center"
          white-space-treatment="preserve"
          white-space-collapse="false"
          linefeed-treatment="preserve"
          space-before.minimum="&section-space-between-minimum-pt;pt"
          space-before.maximum="&section-space-between-maximum-pt;pt"
          space-before.optimum="&section-space-between-optimum-pt;pt"
        >
*
        </fo:block>
      </xsl:if>
    </fo:block>
    
  </xsl:template>

  <xsl:template match="n:blockquote" >
    <fo:block
        text-indent="&blockquote-text-indent;"
        text-align="&blockquote-text-align;"
        margin-left="&blockquote-margin-left-pt;pt"
        margin-right="&blockquote-margin-right-pt;pt"
        space-before.minimum="&blockquote-space-before-minimum-pt;pt"
        space-before.maximum="&blockquote-space-before-maximum-pt;pt"
        space-before.optimum="&blockquote-space-before-optimum-pt;pt"
        space-after.minimum="&blockquote-space-after-minimum-pt;pt"
        space-after.maximum="&blockquote-space-after-maximum-pt;pt"
        space-after.optimum="&blockquote-space-after-optimum-pt;pt"
        font-style="&blockquote-font-style;"
        keep-together.within-page="always"
        hyphenate="false"
    > 
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="n:style" />

  <xsl:template match="n:paragraph-plain" >
    <xsl:call-template name="paragraph-plain" />
  </xsl:template>

  <xsl:template name="paragraph-plain" >
    <fo:block
        text-indent="&text-indent;"
        font-family="&font-family;"
        line-height="&line-height-pt;pt"
        space-before.minimum="&paragraph-space-before-minimum-pt;pt"
        space-before.maximum="&paragraph-space-before-maximum-pt;pt"
        space-before.optimum="&paragraph-space-before-optimum-pt;pt"
        space-after.minimum="&paragraph-space-after-minimum-pt;pt"
        space-after.maximum="&paragraph-space-after-maximum-pt;pt"
        space-after.optimum="&paragraph-space-after-optimum-pt;pt"
    >
      <xsl:if test="position() = last() and child::n:emphasis" >
        <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
      </xsl:if>
      
      <xsl:if test="contains( ../n:title, 'STICKPARAGRAPHS' )" >
        <xsl:attribute name="space-before.minimum" >0pt</xsl:attribute>
        <xsl:attribute name="space-before.maximum" >0pt</xsl:attribute>
        <xsl:attribute name="space-before.optimum" >0pt</xsl:attribute>
        
      </xsl:if>
      
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>
  
  <xsl:template match="//n:paragraph-plain//n:superscript" >
    <fo:inline
        vertical-align="super"
        font-size="&superscript-font-size-pt;pt"
    >
      <xsl:apply-templates/>
    </fo:inline>  
  </xsl:template>

  <xsl:template match="//n:blockquote//n:paragraph-plain" >
    <fo:block 
        text-indent="&blockquote-text-indent;"     
        font-family="&blockquote-font-family;"
        line-height="&blockquote-line-height-pt;pt"
        font-size="&blockquote-font-size-pt;pt"
    > 
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="//n:blockquote//n:superscript" >
    <fo:inline
        font-style="italic"
        vertical-align="super"
        font-size="&blockquote-superscript-font-size-pt;pt"
    >
      <xsl:apply-templates/>
    </fo:inline>  
  </xsl:template>
  
  <xsl:template match="n:paragraph-speech" >
    <xsl:call-template name="speech" >
      <xsl:with-param name="speech-symbol" >&mdash;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="n:paragraph-speech-escaped" >
    <xsl:call-template name="paragraph-plain" />
  </xsl:template>

  <xsl:template match="n:paragraph-speech-continued" >
    <xsl:call-template name="speech" >
      <xsl:with-param name="speech-symbol" >&raquo;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="speech" >
    <xsl:param name = "speech-symbol" />

    <fo:list-block
        space-before.minimum="&paragraph-space-before-minimum-pt;pt"
        space-before.maximum="&paragraph-space-before-maximum-pt;pt"
        space-before.optimum="&paragraph-space-before-optimum-pt;pt"
        space-after.minimum="&paragraph-space-after-minimum-pt;pt"
        space-after.maximum="&paragraph-space-after-maximum-pt;pt"
        space-after.optimum="&paragraph-space-after-optimum-pt;pt"
        widows="&paragraph-widows;"
        orphans="&paragraph-orphans;"
    >
      <fo:list-item>
        <fo:list-item-label>
          <fo:block
              text-indent="&text-indent;"
          >          
            <xsl:value-of select="$speech-symbol" />
          </fo:block>
        </fo:list-item-label>
        <fo:list-item-body>
          <fo:block
            text-indent="&list-item-body-text-indent;"
          >
            <xsl:apply-templates/>
            
            <!-- Enf of speech-sequence if any -->
            <!-- Strang 'if' cascade because 'and' don't seem to work. -->
            <xsl:if test="name(..) = 'n:speech-sequence'" > 
              <xsl:if test="position() = last()" > 
                <xsl:if test="name(.) = 'n:paragraph-speech-continued'" >&nbsp;&raquo;
                </xsl:if>
              </xsl:if>
            </xsl:if>

          </fo:block>
        </fo:list-item-body>
      </fo:list-item>
    </fo:list-block>
  </xsl:template>
  
  
  
  
  <xsl:template name="table-of-contents" > 

    <fo:block
      text-align="center"
      space-after="&toc-title-space-after-pt;pt"
    >
      &toc-title; 
    </fo:block>
    
    <fo:block
        text-align="justify" 
        text-align-last="justify"
        line-height="&toc-entry-line-height;"
    >
      <xsl:for-each select="//n:chapter[not(n:style!='')]" >
        <fo:block>
          &toc-chapter-word;
          <xsl:value-of select="nlx:Numbering.numberAsText(position(),'FR','lower')" />

          <fo:leader 
              leader-pattern="dots"
              leader-alignment="reference-area"            
              leader-pattern-width="5pt"
          />
          <fo:page-number-citation ref-id="{generate-id(.)}" />        
        </fo:block>
      </xsl:for-each>      
      
    </fo:block>

  </xsl:template>


  <xsl:template name="acknowledgements" > 
  
    <fo:block text-align="&acknowledgements-all-text-align;">
      <fo:block      
        padding-top="&acknowledgements-title-padding-top-pt;pt"
        space-after="&acknowledgements-title-space-after-pt;pt"
        font-weight="&acknowledgements-title-font-weight;"
        font-style="&acknowledgements-title-font-style;"
      >
        &acknowledgements-title;       
      </fo:block>    
      <fo:block 
          font-weight="&acknowledgements-text-font-weight;" 
          font-style="&acknowledgements-text-font-style;" 
      >
        &acknowledgements-text;
      </fo:block>
    </fo:block>
    
  </xsl:template>


  <xsl:template name="bibliography" > 

    <fo:block
      text-align="center"
      padding-top="&bibliography-title-padding-top-pt;pt"
      space-after="&bibliography-title-space-after-pt;pt"
    >
      &bibliography-title; 
    </fo:block>


    <fo:block text-align="left" >
      <xsl:for-each select="//n:chapter[n:style='bibliography']//n:section" >
        <fo:block font-size="&bibliography-entries-font-size-pt;pt" >
          <fo:inline 
              font-weight="&bibliography-author-font-weight;" 
              keep-together.within-line="always"
          >
            <xsl:value-of select="n:title" />
          </fo:inline>
          <xsl:if test="n:paragraph-plain" >,&nbsp;</xsl:if> 
          <fo:inline font-style="&bibliography-entry-font-style;" >
            <xsl:for-each select="n:paragraph-plain" >
              <fo:inline keep-together.within-line="always">
                <xsl:apply-templates/>
              </fo:inline>
              <xsl:if test="position() &lt; last()" >, </xsl:if>
            </xsl:for-each>
          </fo:inline>
          <xsl:if test="n:paragraph-plain" >.</xsl:if> 
        </fo:block>
      </xsl:for-each>
    </fo:block>
    
  </xsl:template>

  <xsl:template name="&legal-epilogue-stylename;" >
    <fo:block
        padding-top="&legal-epilogue-padding-top-pt;pt"
        text-align="&legal-epilogue-text-align;"
        font-family="&legal-font-family;"
        font-size="&legal-epilogue-font-size-pt;pt" 
        line-height="&legal-epilogue-line-height;"
    >
      <xsl:for-each select="//n:chapter[n:style='&legal-epilogue-stylename;']//n:paragraph-plain" >
        <fo:block>
          <fo:inline keep-together.within-line="always">
            <xsl:apply-templates/>
          </fo:inline>
        </fo:block>
      </xsl:for-each>
    </fo:block>
  </xsl:template>


  <xsl:template match="n:hard-inline-literal" />

  <xsl:template match="n:quote" >&laquo;&nbsp;<xsl:apply-templates/>&nbsp;&raquo;</xsl:template>

  <xsl:template match="n:paragraph-speech//n:quote" >&ldquo;<xsl:apply-templates/>&rdquo;</xsl:template>

  <xsl:template match="n:paragraph-speech-continued//n:quote" >&ldquo;<xsl:apply-templates/>&rdquo;</xsl:template>


  <xsl:template match="n:emphasis" >
    <fo:inline 
        font-style="italic" 
        hyphenate="false"
    ><xsl:apply-templates/></fo:inline>
  </xsl:template>

  <xsl:template match="n:parenthesis" >(<xsl:apply-templates/>)</xsl:template>

  <xsl:template match="n:square-brackets" >[<xsl:apply-templates/>]</xsl:template>

  <xsl:template match="n:interpolatedclause" >&mdash;&nbsp;<xsl:apply-templates/>&nbsp;&mdash;</xsl:template>

  <xsl:template match="n:interpolatedclause-silentend" >&mdash;&nbsp;<xsl:apply-templates/></xsl:template>


  <xsl:template name="interior-debug" >
    <fo:block>
      <fo:block>
        Debug
      </fo:block>

      <fo:block>
        page-height-pt=
        <xsl:value-of select="&page-height-pt;" />
      </fo:block>
      <fo:block>
        margin-top-pt=
        <xsl:value-of select="&margin-top-pt;" />
      </fo:block>
      <fo:block>
        region-body-margin-top-pt=
        <xsl:value-of select="&region-body-margin-top-pt;" />
      </fo:block>
      <fo:block>
        region-body-height-pt=
        <xsl:value-of select="$region-body-height-pt" />
      </fo:block>
      <fo:block>
        region-body-margin-bottom-pt=
        <xsl:value-of select="$region-body-margin-bottom-pt" />
      </fo:block>
      <fo:block>
        margin-bottom-pt=
        <xsl:value-of select="&margin-bottom-pt;" />
      </fo:block>
      <fo:block>
        chapter-header-height-pt=
        <xsl:value-of select="$chapter-header-height-pt" />
      </fo:block>
      <fo:block>
        chapter-title-space-before-pt=
        <xsl:value-of select="$chapter-title-space-before-pt" />
      </fo:block>

    </fo:block>

  </xsl:template>

</xsl:stylesheet>

