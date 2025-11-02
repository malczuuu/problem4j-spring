import java.io.File
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

private const val UNSPECIFIED = "unspecified"

/**
 * Returns a snapshot version string based on the abbreviated Git commit hash of HEAD. On error,
 * returns "unspecified".
 *
 * @param projectRootDir the root directory of the project (containing .git)
 */
fun getSnapshotVersion(projectRootDir: File): String {
  return try {
    val builder =
        FileRepositoryBuilder()
            .setGitDir(File(projectRootDir, ".git"))
            .readEnvironment()
            .findGitDir()

    builder.build().use { repository ->
      val headId = repository.resolve("HEAD") ?: return UNSPECIFIED

      RevWalk(repository).use { revWalk ->
        val headCommit: RevCommit = revWalk.parseCommit(headId)
        headCommit.id.name.substring(0, 7)
      }
    }
  } catch (e: Exception) {
    System.err.println("Error determining version: $e")
    UNSPECIFIED
  }
}
