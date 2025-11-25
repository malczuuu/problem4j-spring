# Contributing to Problem4J

Problem4J is released under [The MIT License][mit]. By contributing to this project, you
agree that your contributions will be licensed under it.

## How to Contribute

### Opening Issues or Discussions

1. Open an [issue][issues] to report a bug, with a clear description and, if possible, a minimal reproducible example.
2. Open an issue or start a [discussion][discussions] to ask your questions or to suggest your features.
3. You may also participate in already existing issues, discussions or open reviews. Every insight is appreciated.

### Submitting a Pull Request

Consider asking first if suggested feature is planned or not to avoid making unnecessary work, as this library aims to
be minimalistic, yet extensible.

1. Fork the repository, create a feature branch, and submit a pull request.
2. Please check out or rebase your branch against the current `main` branch.
3. Please squash and cleanup your commits as much as possible to avoid unnecessary noise.
4. If there is an issue you're trying to resolve, please reference it in your PR description.
5. Please include tests if able and apply code formatting with `./gradlew spotlessApply` task.

Your contribution may be modified prior to merging, or backported into a different branch than original PR. You will
however keep being the author for your Git commits. You may also be asked to introduce additional changes to the
contribution.

### Developer Certificate of Origin (DCO)

By submitting a Pull Request or commit to this project, you are certifying that you have the right to contribute the
code under the project's license ([MIT][mit]). This is done using the [Developer Certificate of Origin][dco].

The sign-off ensures that:

1. You wrote the code or have permission to submit it.
2. You agree to license your contribution under [MIT][mit].
3. Project maintainers can safely merge your work without needing to verify IP ownership.

If a PR contains unsigned commits, you'll be asked to amend them before merging.

#### How to sign off

Add following line at the end of your commit message:

```txt
Signed-off-by: Your Name <your.email@example.com>
```

If you have your Git `user.name` and `user.email` configured, you can add this automatically with `-s` option.

```bash
git commit -s -m "message"
```

If you prefer not to expose your personal email, you **may use your GitHub nickname and/or GitHub-provided noreply
address**, for example:

```txt
Signed-off-by: nickname <12345678+nickname@users.noreply.github.com>
```

Repository maintainers and codeowners are not required to sign off their commits. For all external contributors, **every
commit in a PR must include a DCO sign-off**.

## Code of Conduct

Please be respectful and constructive.

[dco]: https://developercertificate.org/

[discussions]: https://github.com/malczuuu/problem4j-spring/discussions

[issues]: https://github.com/malczuuu/problem4j-spring/issues

[mit]: https://opensource.org/license/MIT
