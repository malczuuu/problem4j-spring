/*
 * Copyright (c) 2025 Damian Malczewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package internal

import java.io.File
import org.gradle.api.Project

/**
 * Returns a snapshot version string based on the abbreviated Git commit hash of `HEAD`. On error,
 * returns `"unspecified"`.
 *
 * It does not use JGit library but instead reads plain `HEAD` and other files within `.git/`
 * directory, to not call any external process. Falls back to `Project.DEFAULT_VERSION`.
 */
fun Project.findSnapshotVersion(): String {
  return try {
    val gitDir = File(this.rootDir, ".git")
    if (!gitDir.exists()) {
      logger.info(".git directory not found, using {} version", Project.DEFAULT_VERSION)
      return Project.DEFAULT_VERSION
    }

    val headFile = File(gitDir, "HEAD")
    if (!headFile.exists()) {
      return Project.DEFAULT_VERSION
    }

    val headContent = headFile.readText().trim()

    val commitHash =
        when {
          headContent.startsWith("ref: ") -> {
            val refPath = headContent.substring(5)
            val refFile = File(gitDir, refPath)
            if (refFile.exists()) {
              refFile.readText().trim()
            } else {
              readPackedRef(gitDir, refPath)
            }
          }

          headContent.matches(Regex("[0-9a-f]{40}")) -> headContent
          else -> null
        }

    commitHash?.take(7) ?: Project.DEFAULT_VERSION
  } catch (e: Exception) {
    logger.error("Error determining version: {}", e.message)
    Project.DEFAULT_VERSION
  }
}

/**
 * Reads the value of a Git reference from the `packed-refs` file.
 *
 * Git stores references (branches, tags, etc.) in `.git/packed-refs` for efficiency. This function
 * looks for a reference matching the given [refPath] and returns its SHA-1 hash.
 *
 * @param gitDir the `.git` directory of the repository
 * @param refPath the relative path of the Git reference (e.g., `refs/heads/main`)
 * @return the SHA-1 hash of the reference if found, or `null` if the reference does not exist
 */
private fun readPackedRef(gitDir: File, refPath: String): String? {
  val packedRefsFile = File(gitDir, "packed-refs")
  if (!packedRefsFile.exists()) {
    return null
  }

  return packedRefsFile.useLines { lines ->
    lines
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") && !it.startsWith("^") }
        .firstNotNullOfOrNull { line ->
          val parts = line.split(" ", limit = 2)
          if (parts.size == 2 && parts[1] == refPath) parts[0] else null
        }
  }
}
