package utils

import java.util.{Calendar, Date}

import better.files._
import objects.{Commit, Staged}

case object CommitManager {

  /**
   * Create a Commit object in the objects/commits directory.
   * @param files the files to commit
   * @param message the commit message
   * @param parent the parent of the commit
   */
  def createCommit(files: Seq[Staged], message: String, parent: String): String = {
    val commit: File = FileManager.createFile(".sgit/objects/commits/tmpObject")
    FileManager.writeLineFile(commit, Calendar.getInstance.getTime.toString)
    FileManager.writeLineFile(commit, message)
    FileManager.writeLineFile(commit, parent)
    files.map(file => commit.appendLine(file.hash + " " + file.path))
    commit.renameTo(commit.sha1).name
  }

  /**
   * Get the Commit object from its hash ID
   * @param hashId the commit id
   * @return the Commit object if it exists, None otherwise
   */
  def getCommit(hashId: String): Option[Commit] = {
    if(!FileManager.isFileOrDirExists(".sgit/objects/commits/" + hashId)) return None

    val data: Array[String] = FileManager.readFile(".sgit/objects/commits/" + hashId)
      .replace("\r", "")
      .split("\n")

    val files: List[Staged] = data.drop(3)
      .map(file => {
        val str = file.split(" ")
        Staged(str(0), str(1))
      }).toList

    Some(Commit(
      hashId,
      Date.from(FileManager.lastModifiedTime(".sgit/objects/commits/" + hashId)),
      data(1),
      data(2),
      files
    ))
  }

  /**
   * Get the last commit
   * @return the commit hash ID
   */
  def lastCommit(): String = {
    val ref : String = FileManager.readFile(".sgit/HEAD")

    if(FileManager.getFile(".sgit/" + ref).get.isEmpty) ""
    else FileManager.readFile(".sgit/" + ref)
  }

  /**
   * Get the files that has been modified since the previous commit
   * @param newFiles the new files to add
   * @return a Seq of modified Staged files
   */
  def getModifiedFiles(newFiles: Seq[Staged]): Seq[Staged] = {
    val lastCommit = CommitManager.lastCommit()
    if(lastCommit == "") newFiles
    else {
      val committedFiles: Seq[Staged] = CommitManager.getCommit(lastCommit).get.files
      newFiles.diff(committedFiles)
    }
  }
}
