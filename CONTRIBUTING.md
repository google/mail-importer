# Mail Importer for Gmail: Contributing

Want to contribute? Great! First, read this page (including the small print at
the end).

## Legal

### Before you contribute

Before we can use your code, you must sign the
[Google Individual Contributor License Agreement](https://cla.developers.google.com/about/google-individual)
(CLA), which you can do online. The CLA is necessary mainly because you own the
copyright to your changes, even after your contribution becomes part of our
codebase, so we need your permission to use and distribute your code. We also
need to be sure of various other things—for instance that you'll tell us if you
know that your code infringes on other people's patents. You don't have to sign
the CLA until after you've submitted your code for review and a member has
approved it, but you must do it before we can put your code into our codebase.
Before you start working on a larger contribution, you should get in touch with
us first through the issue tracker with your idea so that we can help out and
possibly guide you. Coordinating up front makes it much easier to avoid
frustration later on.

### The small print

Contributions made by corporations are covered by a different agreement than the
one above, the
[Software Grant and Corporate Contributor License Agreement](https://cla.developers.google.com/about/google-corporate).

## Code Reviews

All submissions, including submissions by project members, require review.

### How

We use Github pull requests. In your request, please give a short explanation of
what you are trying to do and why. In the git commit message for the patch, the
first line should be a summary of the whole patch. After that, explain the patch
in more detail.

### Style

For code style, we follow the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
We currently use a 100-column limit. We prefer to use `google-java-format` to
format our code.

We use Java 8 including all of the new functional fanciness. However, we try to
be reasonable and use it only when it actually makes things simpler and clearer.

### Design

There is rough documentation of the current design in [Design.md](Design.md).
