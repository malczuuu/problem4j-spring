# GitHub Actions and its Utilities

## File [`next_version.txt`](next_version.txt)

This file contains planned next version for `-SNAPSHOT` releases. It must match `vX.Y.Z` format and contain single line
with planned version only.

File integrity is validated with [`validate-version-next.yml`](../workflows/validate-version-next.yml) workflow.

Used by [`gradle-publish-snapshot.yml`](../workflows/gradle-publish-snapshot.yml) workflow, which is launched manually.
