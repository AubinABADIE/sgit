package actions

import utils.{BranchManager, CommitManager, ConsoleOutput}

import scala.annotation.tailrec


case object Log {

  /**
   * Display the commits logs.
   */
  def logs(): Unit = {
    val lastCommit: String = CommitManager.lastCommit()
    if(lastCommit.isEmpty) ConsoleOutput.printError("your actual branch '" + BranchManager.getCurrentBranch() + "' doesn't have any commit yet")
    else {
      @tailrec
      def loopCommit(hash: String): Unit = {
        if(hash.nonEmpty) {
          val commit = CommitManager.getCommit(hash)
          ConsoleOutput.printYellow("commit " + commit.get.hash)
          ConsoleOutput.print("Date: " + commit.get.timestamp.toString)
          ConsoleOutput.print("\n\t" + commit.get.message + "\n")
          if(commit.get.parent.nonEmpty) loopCommit(commit.get.parent)
        }
      }
      loopCommit(lastCommit)
    }
  }
}
