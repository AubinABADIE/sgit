package actions

import utils.{BranchManager, CommitManager, ConsoleOutput}

case object Branch {

  /**
   * Handles the command branch. If a parameter name is given, creates a new branch. If not, prints all the branches.
   * @param branchName the optional branch name.
   * @return Boolean if everything went up correctly or not.
   */
  def branch(branchName: Option[String], verbose: Boolean): Boolean = {
    if(branchName.isEmpty) {
      val branches = BranchManager.getAllBranches()
      val currentBranch: String = BranchManager.getCurrentBranch()
      branches.foreach(branch => {
        if(verbose) {
          val commit = CommitManager.getCommit(branch._2)
          if(branch._1 == currentBranch) ConsoleOutput.printGreen("* " + branch._1 + " -> " + branch._2  + ": " + commit.get.message)
          else ConsoleOutput.print(branch._1 + " -> " + branch._2  + ": " + commit.get.message)
        }
        else if(branch._1 == currentBranch) ConsoleOutput.printGreen("* " + branch._1)
        else ConsoleOutput.print(branch._1)
      })
      return true
    } else {
      val commit = CommitManager.lastCommit()
      if(commit.isEmpty) {
        ConsoleOutput.printError("fatal: your actual branch '" + BranchManager.getCurrentBranch() + "' doesn't have any commit yet")
        return false
      } else {
        if(BranchManager.getAllBranches().exists(_._1 == branchName.get)) {
          ConsoleOutput.printError("A branch named '" + branchName.get + "' already exists")
          return false
        } else {
          BranchManager.createBranch(branchName.get, commit)
          ConsoleOutput.printGreen("Branch " + branchName.get + " successfully created")
          return true
        }
      }
    }
  }

}
