# Publishing

Keep Git tags with `vX.Y.Z-suffix` format. GitHub Actions job will only trigger on such tags and will remove `v` prefix.

- See [`gradle-publish-release.yml`](.github/workflows/gradle-publish-release.yml) for publishing release versions
  instructions.
- See [`gradle-publish-snapshot.yml`](.github/workflows/gradle-publish-snapshot.yml) for publishing snapshot version
  instructions.

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

**Note** that this only uploads the artifacts to Sonatype repository. You need to manually log in to Sonatype to push
the artifacts to Maven Central.
