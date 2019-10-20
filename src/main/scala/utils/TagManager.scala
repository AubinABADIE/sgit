package utils

import java.util.Calendar

import better.files._

case object TagManager {

  /**
   * Create a new tag
   * @param tagName the tag name
   * @param commitName the concerned commit hash ID
   * @return True if the commit has been created, false otherwise
   */
  def createTag(tagName: String, commitName: String): Boolean = {
    if(FileManager.isFileOrDirExists(".sgit/refs/tags/" + tagName)) {
      ConsoleOutput.printError("fatal: tag '" + tagName + "' already exists")
      false
    } else {
      FileManager.writeLineFile(".sgit/refs/tags/" + tagName, "commit " + commitName + "(HEAD -> " + BranchManager.getCurrentBranch() + ", tag: " + tagName + ")")
      FileManager.writeLineFile(".sgit/refs/tags/" + tagName, "Date: " + Calendar.getInstance.getTime.toString)
      ConsoleOutput.printError("Successfully created tag " + tagName)
      true
    }
  }

  /**
   * Get all the tags in the refs/tags repository
   * @return an optional map of tags, name -> commit
   */
  def getAllTags: Option[Map[String, String]] = {
    if(".sgit/refs/tags".toFile.isEmpty) None
    else {
      val tags = ".sgit/refs/tags".toFile.children.toIndexedSeq.sorted(File.Order.byModificationTime)
      Some(tags.map(tag => (tag.name, tag.contentAsString)).toMap)
    }
  }
}
