package actions

import better.files._
import utils.{CommitManager, ConsoleOutput}

import scala.annotation.tailrec


case object Log {

  /**
   * Shows the logs of the commits.
   */
  def logs(): Unit = {
    val lastCommit: String = CommitManager.lastCommit()
    if(lastCommit.isEmpty) ConsoleOutput.printError("No commits yet, nothing to show.")
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
