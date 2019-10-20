package actions

import objects.{Commit, Staged}
import utils.{BranchManager, CommitManager, ConsoleOutput, StageManager}

import scala.annotation.tailrec

case object Commit {

  /**
   * Create a new commit
   * @return the hash ID or None.
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

  /**
   * Create the Commit object and update the current branch
   * @param stagedFiles the files to be committed
   * @param message the name of the commit
   * @param parent the parent of the commit
   * @return the hash ID
   */
  def applyCommit(stagedFiles: Seq[Staged], message: String, parent: String): String = {
    val hash = CommitManager.createCommit(stagedFiles, message, parent)
    BranchManager.updateCurrentBranch(hash)
    ConsoleOutput.print("Successfully done new commit " + hash + " at " + BranchManager.getCurrentBranch())
    return hash
  }
}
