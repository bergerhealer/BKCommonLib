# About This Documentation

Defines how architecture documentation is written and maintained. This file is
project-agnostic and may be shared verbatim between repositories. Do not add
project-specific details here.

## Purpose

The documentation lets humans and AI assistants understand a project without first reading
its source. A reader should be able to:

- Find which source files to open to make a change or diagnose a bug.
- Understand how classes, modules and features relate.
- Start using a class or method correctly from the docs alone.

It is an architectural overview, not a replacement for source code or generated API docs.

## Layout

- `AGENTS.md` (repository root) points agents to `docs/` and is kept plain: only a pointer to the documentation, no
  content of its own. `CLAUDE.md` references `AGENTS.md`.
- `docs/overview.md` is the starting point. Guidance on when to read a specific page (for example how to recognize code
  that needs a dedicated syntax or usage page) belongs in the overview, not in `AGENTS.md`.
- `docs/README.md` is the index. Every page is listed there with a one-line description
  of what it covers and when to read it.
- `docs/about-documentation.md` is this file.
- `docs/SUMMARY.md` is the minimal context an agent loads to decide whether this project is worth considering for the
  problem it is solving. Because it is consumed always and costs tokens every time, keep it extremely short: only what
  the project can be used for, when it applies, the main package path(s) if it is a software library, and how to recognize its code (names, annotations, file types) in other
  code. It is handed to the agent without its file location, so it must state where the docs live (e.g. `docs/`) and
  name pages by path from the repository root in plain code formatting, never as relative links. No explanations, examples or API details; point to the overview, the index or specific pages instead. Anything
  beyond that is found through further tool calls to specific source files or docs. Update it when the project's scope
  changes.
- Required pages:
  - **Overview**: the big picture. Purpose of the system, module map, external dependencies,
    key data and control flows. This is the top of a top-down hierarchy
    (system, modules, components, classes); other pages zoom into one level of it.
  - **Glossary**: domain and project terms, defined once.
- Implementation details that only matter to maintainers of the project itself (not to users of it as a
  dependency) go in separate pages under `docs/internals/`, listed under their own
  heading in the index and stating the audience at the top.
- Larger examples that would clutter a page go under `docs/examples/`, one page per topic, linked from the page they
  accompany (see What to Omit).
- Other pages cover one module, subsystem or major feature each. Group them in subfolders
  when a topic spans several pages.

## Page Types

Each page is mainly one of these (after the Diátaxis framework); don't mix them within a page
beyond short pointers:

- **Explanation**: why and how a subsystem is designed, and how its parts relate.
- **Reference**: key classes and methods, their usage, and conventions.
- **How-to**: short recipes such as "to add a new X: subclass Y, register in Z".

## Page Template

Use a consistent structure so pages are predictable and easy to scan:

1. Summary: scope of the page in a few sentences.
2. Key files: paths with a one-line role each.
3. Relationships: hierarchies, ownership, dependencies, flows.
4. Usage: how to use the important classes and methods.
5. Conventions and how-tos.
6. Rationale and gotchas.

Omit sections that do not apply.

## What to Include

- **Source references**: relative paths plus class/method names, never line numbers.
- **Relationships**: class hierarchies, ownership, module dependencies, data/control flow.
- **Usage guidance**: for important classes and methods, state purpose, when to use them,
  arguments and return values, preconditions, threading or lifecycle constraints, and common
  pitfalls.
- **Conventions**: naming, registration and extension patterns.
- **Task entry points**: "to change X, look at A and B"; for debugging, where a symptom
  most likely originates.
- **Rationale**: why a design was chosen, its trade-offs and constraints. Not a change history.
- **Gotchas**: non-obvious behaviour, ordering requirements, generated or external code
  that must not be edited by hand.
- **Diagrams** where they clarify hierarchies or flows. Use text-based formats (e.g. Mermaid)
  that live in version control and diff well. Diagrams supplement prose; they never replace it.

## What to Omit

- Code listings. Inline code is limited to short snippets (a few lines), e.g. one usage
  example for a major entry point. Names of classes, methods and files in prose are fine.
  Exception: pages that define a language, file format or configuration syntax should give a short example
  for each documented construct (artificial examples are fine, real ones are better), since the syntax cannot
  be learned from prose alone. Larger or complete examples go in a separate page under `docs/examples/`, linked
  from the page they accompany and listed in the index.
- Restating what code obviously does, or copying source comments wholesale.
- Exhaustive member lists. Document the important API; point to the source file for the rest.
- Volatile details: line numbers, version numbers, counts.
- Change history, TODO lists, opinions, planning notes.
- Secrets, credentials and machine-specific paths.

## Writing Style

- Concise and factual: short paragraphs, bullet lists, tables. Active voice.
- Use one term per concept, as defined in the glossary.
- Pages are self-contained: they must make sense when loaded alone, so an AI reader
  can use a single page. Use stable, descriptive headings and consistent places for file paths.
- Link external projects, libraries and tools to their repository or homepage wherever they are mentioned.
- Link with descriptive link text and relative paths. Cross-reference instead of duplicating;
  each fact lives in one place.
- Keep a consistent heading hierarchy.

## Generating the Documentation

1. Survey the project: build files, module layout, entry points, main packages.
2. Decide the page structure, starting with the overview and glossary; list pages in `docs/README.md`.
3. Write each page from the relevant source, following the rules above. Verify that every
   referenced path and name exists.
4. Check each page against the purpose: could someone make a change or fix a bug knowing
   only this page and the files it points to?
5. If a documented dependency has its own docs, link to them instead of re-explaining.

## Maintaining the Documentation

- Update affected pages in the same change that alters architecture, public usage or conventions.
- Fix references when files or classes are renamed or moved; remove docs for removed features.
- Keep `docs/README.md` in sync with the existing pages.
- Keep `docs/SUMMARY.md` accurate and minimal: when adding to it, check that the detail could not live in a page instead.
- Where possible, add an automated check (e.g. in CI) that links and referenced paths still exist.

## Updating These Guidelines

When a user states that something should or should not be documented, or how, record it here
so the rule persists.

- If it is unclear whether the instruction is a general rule or an exception for one case,
  ask the user before recording it.
- General rules go in the relevant section above. One-off exceptions apply only to the page
  concerned and are not added here.
- Project-specific rules do not belong in this file; put them elsewhere (e.g. the project's
  `AGENTS.md`).
