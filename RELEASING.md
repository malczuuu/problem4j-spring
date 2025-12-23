# Releasing

A release is created by pushing an annotated git tag named `v1.2.3` with the message "Release 1.2.3". Preferably, use
the [`./tools/tagrelease`](./tools/tagrelease) script, which ensures the tag is correctly formatted and prevents
mistakes. Proper tag format is required to trigger build automation.

See `./tools/tagrelease --help` for reference.

## Branching and Release Workflow

This repository maintains two major versions, supporting Spring Boot 3 and 4. The goal is to maintain both versions at
least until Spring Boot 3 reaches its end of life or becomes irrelevant.

| branch           | info                                       | latest                                                                                                                            |
|------------------|--------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `main`           | version `2.x` supporting Spring Boot `4.x` | [![Sonatype](https://img.shields.io/maven-central/v/io.github.problem4j/problem4j-spring-bom?filter=2.*)][maven-central] |
| `release-v1.*.x` | version `1.x` supporting Spring Boot `3.x` | [![Sonatype](https://img.shields.io/maven-central/v/io.github.problem4j/problem4j-spring-bom?filter=1.*)][maven-central] |

Bugfixes for `1.x` should be merged into the lowest applicable `1.x` release branch. From there, they are cascaded
forward into newer version branches if applicable, so fixes propagate through the release line without being duplicated
unnecessarily.

Following diagram demonstrates the merge direction that comes from `release-v1.0.x` up to `main`.

```mermaid
graph LR
    A[release-v1.0.x<br/>original bugfix]
    A --> B[release-v1.1.x<br/>merge commit with release-v1.0.x]
    B --> C[main<br/>merge commit with release-v1.1.x]
```

**Note** that the `1.x` major version is supported, but older minor release lines may not be maintained long-term. Bug
fixes are applied only when necessary, and maintenance typically focuses on the more recent `1.*.x` branches unless an
issue is critical or a change can be backported with minimal effort.

## Maven Central

[![Publish Release Status](https://github.com/problem4j/problem4j-spring/actions/workflows/gradle-publish-release.yml/badge.svg)][gradle-publish-release]
[![Sonatype](https://img.shields.io/maven-central/v/io.github.problem4j/problem4j-spring-bom)][maven-central]

1. Keep Git tags with `vX.Y.Z-suffix` format. GitHub Actions job will only trigger on such tags and will remove `v`
   prefix.
2. The releasing procedure only uploads the artifacts to Sonatype repository. You need to manually log in to Sonatype to
   push the artifacts to Maven Central.

See [`gradle-publish-release.yml`][gradle-publish-release.yml] for publishing release versions instructions.

Set the following environment variables in your CI/CD (GitHub Actions, etc.):

```txt
# generated tokens on Sonatype account, do not use real username/password
PUBLISHING_USERNAME=<username>
PUBLISHING_PASSWORD=<password>

# generated PGP key for signing artifacts
SIGNING_KEY=<PGP key>
SIGNING_PASSWORD=<PGP password>
```

Artifacts are published to Maven Central via Sonatype, using following Gradle task.

```bash
./gradlew -Pversion=<version> -Psign publishAggregationToCentralPortal
```

This command uses `nmcp` Gradle plugin - [link](https://github.com/GradleUp/nmcp).

[gradle-publish-release]: https://github.com/problem4j/problem4j-spring/actions/workflows/gradle-publish-release.yml

[gradle-publish-release.yml]: .github/workflows/gradle-publish-release.yml

[maven-central]: https://central.sonatype.com/namespace/io.github.problem4j
