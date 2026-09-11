# Technical Report: OpenFlexo Markdown Adapter

**Version:** 1.0
**Date:** 2026-04-22

## Abstract

This document serves as a realistic test fixture for the OpenFlexo Markdown
technology adapter. It combines headings at multiple levels, regular
paragraphs, and code blocks in a structure representative of real-world
Markdown documents.

## 1. Architecture

The adapter is structured as a standard OpenFlexo technology adapter with
three modules:

- `markdown-ta` — core model and resource management
- `markdown-ta-ui` — Swing UI components and inspectors
- `markdown-ta-test-rc` — test resource center (this document)

### 1.1 Core Model

The core model exposes three element types that implement `FlexoDocElement`:

- `MarkdownHeading` — ATX headings (H1–H6)
- `MarkdownParagraph` — regular paragraphs
- `MarkdownCodeBlock` — fenced and indented code blocks

### 1.2 Parsing

Parsing is delegated to the flexmark-java library (version 0.62.2, Java 8
compatible). The `MarkdownFactory` holds a shared static `Parser` instance.

## 2. Resource Lifecycle

### 2.1 Loading

```
File → MarkdownDocumentResourceImpl.performLoad()
     → MarkdownFactory.makeNewMarkdownDocument(String)
     → PARSER.parse(rawMarkdown)
     → MarkdownDocument.updateFromMarkdownDocument(Document, MarkdownFactory)
```

### 2.2 Saving

```
MarkdownDocumentResourceImpl.performSave()
  → writeMarkdown(OutputStream)
    → renderElement() per FlexoDocElement
      → "# text"      for MarkdownHeading
      → "text"        for MarkdownParagraph
      → "```lang\n…"  for MarkdownCodeBlock
```

## 3. Known Limitations

The first version does not support:

- Inline formatting (bold, italic, links, images)
- Block quotes
- Ordered and unordered lists
- Tables
- HTML blocks

These can be added incrementally by extending `updateFromMarkdownDocument`
to handle the corresponding flexmark AST node types.

## 4. Example Code

Factory usage:

```java
MarkdownFactory factory = new MarkdownFactory(resource, editingContext);
MarkdownDocument doc = factory.makeNewMarkdownDocument(rawMarkdown);

for (FlexoDocElement<?, ?> element : doc.getElements()) {
    System.out.println(element);
}
```

Serialisation round-trip check:

```java
String original = readFile("TechnicalReport.md");
MarkdownDocument doc = factory.makeNewMarkdownDocument(original);
String serialised = serialise(doc);
assertEquals(original, serialised);
```

## 5. Conclusion

The Markdown technology adapter provides a lightweight, Java-8-compatible
bridge between CommonMark-formatted documents and the OpenFlexo FML virtual
model ecosystem.
