package actions

import utils.{BranchManager, CommitManager, ConsoleOutput, TagManager}

case object Tag {

  /**
   * Creates a tag in the repository.
   * The tag references the most recent commit.
   * If no parameter, prints all the tags.
   * @param tagName the tag name if given.
   * @return true if the operation was successful, false otherwise.
   */
  def tag(tagName: Option[String]): Boolean =  {
    if(tagName.isEmpty) {
      val tags = TagManager.getAllTags
      if(tags.isEmpty) false
      else {
        tags.get.foreach(tag => ConsoleOutput.printYellow(tag._1))
        true
      }
    }
    else {
      val commit = CommitManager.lastCommit()
      if(commit.isEmpty) {
        ConsoleOutput.printError("fatal: your actual branch '" + BranchManager.getCurrentBranch() + "' doesn't have any commit yet")
        false
      }
      else {
        TagManager.createTag(tagName.get, commit)
      }
    }
  }
}
