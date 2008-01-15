*** Toplevel elements
%sid some-optional-chapter-identifier

=== This is a section 
%sid some-optional-section-identifier

This is a paragraph. A paragraph may contain line feeds because sometimes it's useful have several sentences on different lines. 
So we ought to support it.
Anyways it doesn't hurt readability.

This is another paragraph. When rendered, the line feed between previous paragraph and this one disapppears.

%pid some-optional-paragraph-identifier
This is a paragraph with a unique paragraph identifier.

===

Section separator above indicates an anonymous section.



*** Classics

Emphasis does _exist_. Yep. _It may spread across several sentences and nesting is __supported__. Nesting can't go through not several paragraphs._

As always there is support (including non-breakable space) for colon : question mark ? Exclamation mark ! Semicolon; Ellipsis... 



*** Blocks

This is a paragraph inside an anonymous section.

This is a sample of "quoted" text.

"Nested ''quotes'' also happen (1 nesting level only)."

--- Speech introduction here.

--- The man :: Speech with locutor name.

((( This is some casual paragraph _not_ breaking speech sequence.

+++ As a speech may spread on several paragraphs, there is a continuation marker.

Interpolated clauses -- incise in French -- are supported. The double dash defines a block handling non-breakable spaces.

Interpolated clauses may end without closing dash -- so we need a closing marker to get non-breakable space right for the opening dash|-.


<<<
Blockquote represent written text.

Rules for paragraphs apply.

_Emphasis_ supported. 

"Quotes and ''nested'' quotes"
>>>





*** Other 

=== Comments

// Single-line

/* Or multiple-line 
comment with /*nesting*/ */


=== Tags

%tag synopsis
%tag main character

=== URI

http://blah.com/

"Description"http://blah.com

=== Numbering

With 1er or 22e, the number part triggers superscript for the ordinal ending.



*** Structure

=== General considerations

Structure files reorganize content files. Therefore they are two distinct kind of files.
Structure files reference parts of content files like this:
%directive [options] relativefilename <chapter> <section> [paragraph...]

Relative file name is relative to structure file.

Chapters, sections and paragraphs are referenced in one of following ways:
- Ordinal #1
- Identifier 'identifier'
- Title "title" (chapters and sections only).

Several chapters may be referenced in one directive.


=== Anchors and injection

An @anchor@ is a placeholder in content file for some other text as defined in a structure file. So the text containing the anchor not "aware" of what will be injected. What is injected is defined in a separate structure file. Resulting text contains origin as comment.
An anchor name must be unique throughout every known document. 

%inject @anchor@ 'some-identifier'
%inject @anchor@ relativefilename "Some chapter" "Some section"
%inject @anchor@ relativefilename "Some chapter" "Some section" #1 #2 #3 


=== Inclusions

Structure files become final content through inclusion of other files.

%include 'some-identifier'
%include relativefilename "Some chapter" "Some section"
%include relativefilename "Some chapter" "Some section" #1 'two-as-identifier', " 

Inclusions must retrieve _at least_ one paragraph.


