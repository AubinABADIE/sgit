package actions

import objects.{Commit, Staged}
import utils.{BranchManager, CommitManager, ConsoleOutput, StageManager}

case object Commit {

  /**
   * Creates a new commit in the repository.
   * @return the commit SHA or none if error.
   */
  def commit(message: String): Option[String] = {
    val lastCommit: String = CommitManager.lastCommit()
    val stagedFiles: Seq[Staged] = StageManager.getStagedFiles()

    def applyCommit(stagedFiles: Seq[Staged], message: String, parent: String): String = {
      val hash = CommitManager.createCommit(stagedFiles, message, parent)
      BranchManager.updateCurrentBranch(hash)
      //StageManager.deleteStage()
      ConsoleOutput.print("Successfully done new commit " + hash + " at " + BranchManager.getCurrentBranch())
      return hash
    }

    if(stagedFiles.isEmpty) {
      ConsoleOutput.printError("No files found. Please run 'sgit add <file> ...'")
      None
    }
    else if(lastCommit.isEmpty) {
      Some(applyCommit(stagedFiles, message, lastCommit))
    } else {
      val parent: Commit = CommitManager.getCommit(lastCommit).get
//      val newFiles: Seq[String] = stagedFiles.map(file => file.hash)
//      val newList: Seq[Staged] = parent.files.iterator.map(file => {
//        if(newFiles.contains(file.hash)) stagedFiles.filter(f => f.hash == file.hash).head
//        else file
//      }).toSeq
      Some(applyCommit(stagedFiles, message, parent.hash))
    }
  }
}
