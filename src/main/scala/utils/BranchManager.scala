package utils

import better.files._

case object BranchManager {

  /**
   * Creates a new branch. Does not override an existing branch.
   * @param branchName the new branch name
   * @param commitName the commit to reference.
   * @return true if the branch has been created, false otherwise.
   */
  def createBranch(branchName: String, commitName: String) = {
    val path = ".sgit/refs/heads/" + branchName
    if(!FileManager.isFileOrDirExists(path)) {
      val file = FileManager.createFile(path)
      FileManager.writeFile(file, commitName)
    }
  }

  /**
   * Get the current branch name
   * @return the branch name
   */
  def getCurrentBranch(): String = {
    if(!".sgit/HEAD".toFile.exists) ""
    else {
      ".sgit/HEAD".toFile
        .contentAsString
        .split("/")
        .last
    }
  }

  /**
   * Return all the branches.
   * @return An optional map of branches, name -> commit
   */
  def getAllBranches(): Map[String, String] = {
    val branches = FileManager.getFile(".sgit/refs/heads").get
      .children
      .toSeq
      .sorted(File.Order.byModificationTime)
    branches.map(branch => (branch.name, branch.contentAsString)).toMap
  }

  /**
  * Update the current branch
  * @param commit the hash ID of the last commit
  */
  def updateCurrentBranch(commit: String): Unit = FileManager.getFile(".sgit/refs/heads/" + getCurrentBranch()).get.overwrite(commit)

  /**
   * Change the current branch to an other one
   * @param newBranch the new branch
   */
  def updateHead(newBranch: String): Unit = FileManager.getFile(".sgit/HEAD").get.overwrite("refs/heads/" + newBranch)
}
