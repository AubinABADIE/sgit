package actions

import objects.{Commit, Staged}
import utils.{BranchManager, CommitManager, ConsoleOutput, StageManager}

import scala.annotation.tailrec

case object Commit {

  /**
   * Creates a new commit in the repository.
   * @return the commit SHA or none if error.
   */
  def commit(message: String): Option[String] = {
    val lastCommit: String = CommitManager.lastCommit()
    val stagedFiles: Seq[Staged] = StageManager.getStagedFiles()

    if (stagedFiles.isEmpty) {
      ConsoleOutput.printError("No files found. Please run 'sgit add <file> ...'")
      None
    }

    else if (lastCommit.isEmpty) {
      Some(applyCommit(stagedFiles, message, lastCommit))
    } else {
      val newFiles = CommitManager.getModifiedFiles(stagedFiles)
      val parent: Commit = CommitManager.getCommit(lastCommit).get
      if (newFiles.isEmpty) {
        ConsoleOutput.printError("No modified files found.")
        None
      }
      else Some(applyCommit(newFiles, message, parent.hash))
    }
  }

  def applyCommit(stagedFiles: Seq[Staged], message: String, parent: String): String = {
    val hash = CommitManager.createCommit(stagedFiles, message, parent)
    BranchManager.updateCurrentBranch(hash)
    ConsoleOutput.print("Successfully done new commit " + hash + " at " + BranchManager.getCurrentBranch())
    return hash
  }
}
