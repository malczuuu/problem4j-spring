# GitHub Actions and its Utilities

## File `utils/version-next.txt`

This file contains planned next version for `-SNAPSHOT` releases. It must match `vX.Y.Z` format and contain single line
with planned version only.

File integrity is validated with [`validate-version-next.yml`](workflows/validate-version-next.yml) workflow.
