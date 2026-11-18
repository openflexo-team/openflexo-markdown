# openflexo-markdown — Backlog

Features and evolutions to develop. Each entry says what problem it solves and when it is done. Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `MARKDOWN-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML runtime errors

### MARKDOWN-F-1 — The markdown adapter raises typed FML errors instead of returning null after a warning or a printed stack  ·  `DONE`

**GitHub:** [openflexo-team/openflexo-markdown#1](https://github.com/openflexo-team/openflexo-markdown/issues/1)

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`), done with the other remaining adapters in one batch (asked by the user, 2026-11-16), with the rules settled on the first adapters (xlsx, xml, jdbc, rest, formod, docx): the edition actions caught the failures of their evaluations (four catches printing the stack, or a log), and went on with null after a warning.

**Rules applied (decided with the user on the first adapters).** Evaluations through `FMLEvaluation`; every `execute` / `performExecute` declares `throws FMLExecutionException`; a null receiver, model, document, name or required input is an `FMLNullReferenceError`; a select from a null source is an `FMLNullReferenceError` naming the select; a foreseen failure of the technology an `FMLTechnologyError` with a message of its own; a `ModelDefinitionException` an `FMLInternalError`; a cancelled loading an `FMLCancellation`; a typed failure of `AbstractCreateResource` rethrown unchanged; a log just before a throw removed (the failure is reported once, by the editor); the `System.out` traces of the actions removed. Scope: the paths that FML execution goes through; loading and conversion code left.

**Done (2026-11-16).**
- `AddMarkdownHeading`, `AddMarkdownParagraph`, `AddMarkdownCodeBlock`: a null document is an `FMLNullReferenceError` (null after a warning).
- `CreateMarkdownDocumentResource`: a resource that cannot be created is an `FMLTechnologyError` (null after a warning), its loading failures typed.
- The selects of headings and paragraphs: a null document is an `FMLNullReferenceError` (an empty result before).

**After review (batch review).** `CreateMarkdownDocumentResource`: a null name is an `FMLNullReferenceError`, a resource center that is
not on the file system an `FMLTechnologyError` (a `ClassCastException`).

**Verified (2026-11-16).** markdown-ta-test 29: `TestMarkdownEditionActions` red as before phase 3 (same message, an `UnresolvedTechnologySpecificAction`), the others green; ECJ compiles markdown-ta. No new test.
