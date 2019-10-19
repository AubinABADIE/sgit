package utils

import better.files._

case object TagManager {

  /**
   * Creates a new tag.
   * @param tagName the tag name
   * @param commitName the commit to affect the tag to.
   * @return True if the commit has been created, false otherwise.
   */
  def createTag(tagName: String, commitName: String): Boolean = {
    if(".sgit/refs/tags".toFile.exists) {
      if((".sgit/refs/tags" + tagName).toFile.exists) false
      else {
        (".sgit/refs/tags/"+tagName).toFile.append(commitName)
        true
      }
    }else false
  }

  /**
   * Gets all the tags from the repository.
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
