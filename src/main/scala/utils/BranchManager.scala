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
   * Gets the current branch name.
   * @return the branch name.
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

//  /**
//   * Returns all the branches.
//   * @return An optional map of branches, name -> commit
//   */
//  def getAllBranches: Option[Map[String, String]] = {
//    if(".sgit/refs/heads".toFile.isEmpty) None
//    else {
//      val branches = ".sgit/refs/heads".toFile.children.toIndexedSeq.sorted(File.Order.byModificationTime)
//      Some(branches.map(branch => (branch.name, branch.contentAsString)).toMap)
//    }
//  }

  /**
   * Updates the current branch, and returns the name of the current branch.
   * @param commit the new sha to refer to.
   * @return an option, none if error, or the branch name.
   */
  def updateCurrentBranch(commit: String) = FileManager.getFile(".sgit/refs/heads/" + getCurrentBranch()).get.overwrite(commit)

  /**
   * Updates the current branch to the new head.
   * @param newBranch the new branch to refer.
   */
  def updateHead(newBranch: String): Unit = FileManager.getFile(".sgit/HEAD").get.overwrite("refs/heads/" + newBranch)

}
