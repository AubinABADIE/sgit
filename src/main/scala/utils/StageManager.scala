package utils

import better.files._
import objects.{Blob, Staged}

import scala.annotation.tailrec

object StageManager {

  /**
   * Get the staged files in the INDEX file.
   * @return a Seq of Staged object
   */
  def getStagedFiles(): Seq[Staged] = {
    val index : File = ".sgit/INDEX".toFile
    if(index.isEmpty) Seq()
    else {
      val content = FileManager.readFile(index)
        .replace("\r", "")
        .split("\n")
        .filterNot(string => string.isEmpty)
      if(content.isEmpty) return Seq()
      content.map(file => {
        val split = file.split(" ")
        Staged(split(0), split(1))
      })
    }
  }

  /**
   * Find the files which need to be update
   * @param newFiles the new files the ad command
   * @param oldFiles the existing staged files.
   * @return a Seq of Staged objects to be add
   */
  def updateIndex(newFiles: Seq[Staged], oldFiles: Seq[Staged]): Seq[Staged] = {
    if(oldFiles.isEmpty) return newFiles

    @tailrec
    def findExisting(files: Seq[Staged], existingFiles: Seq[Staged], out: Seq[Staged]): Seq[Staged] = {
      if(files.isEmpty) out ++ existingFiles
      else {
        val index = existingFiles.indexOf(files.head)

        if(index != -1) {
          val oldStage = existingFiles.take(index - 1) ++ existingFiles.drop(index + 1)
          findExisting(files.tail, oldStage, out :+ files.head)
        }
        else findExisting(files.tail, existingFiles, out :+ files.head)
      }
    }
    findExisting(newFiles, oldFiles, Seq())
  }

  /**
   * Remove old staged files and add the new ones in the INDEX file
   * @param filePaths the Seq of Staged objects
   * @return true if the files has been add, None otherwise.
   */
  def addStagedFiles(filePaths: Seq[Staged]): Option[Boolean] = {
    val index: File = FileManager.getFile(".sgit/INDEX").get
    if(!FileManager.isFileOrDirExists(index)) None
    else {
      deleteStage()
      filePaths.foreach(file => FileManager.writeLineFile(index, file.hash + " " + file.path))
      Some(true)
    }
  }

  /**
   * Empties the stage file.
   */
  def deleteStage(): Unit = {
    val index: File = FileManager.getFile(".sgit/INDEX").get
    index.overwrite("")
  }

  /**
   * Searches for the untracked files in the directory.
   * @param files the files to check
   * @return a list of untracked files.
   */
  def getUntrackedFiles(files: Seq[File]): Seq[File] = files.filterNot(file => FileManager.isFileOrDirExists(".sgit/objects/blobs/" + file.sha1))
}