package utils

import java.util.{Calendar, Date}

import better.files._
import objects.{Commit, Staged}

case object CommitManager {

  /**
   * Creates a commit file on the objects directory.
   * @param files the files to commit.
   * @param message the commit description
   * @param parent the parents of the commit.
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
   * Finds the commit infos by its ID.
   * Commit structure:
   * desc: "desc"
   * parent: "SHA prints separated by spaces"
   * The following lines are the files.
   * @param hashId the commit id
   * @return a, option with a commit object containing the infos.
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
   * Finds the last commit.
   * @return the commit hash ID.
   */
  def lastCommit(): String = {
    val ref : String = FileManager.readFile(".sgit/HEAD")

    if(FileManager.getFile(".sgit/" + ref).get.isEmpty) ""
    else FileManager.readFile(".sgit/" + ref)
  }
}
