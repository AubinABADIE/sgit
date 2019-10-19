package utils

import better.files._
import objects.{Blob, Staged}

import scala.annotation.tailrec

object StageManager {

  /**
   * Gets the staged files from the sgit folder.
   * @return a sequence of files
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
   * Finds the duplicated files between the old stage and the new stage.
   * If the sha prints are different but the name is the same, it removes the old file from the final stage
   * @param newFiles The new files from the most recent add call.
   * @param oldFiles the existing files in the stage.
   * @return a tuple of sequence of files. _1 contains the new stage, _2 contains the files to delete.
   */
  def updateIndex(newFiles: Seq[Staged], oldFiles: Seq[Staged]): Seq[Staged] = {
    if(oldFiles.isEmpty) return newFiles

    @tailrec
    def findExisting(files: Seq[Staged], existingFiles: Seq[Staged], out: Seq[Staged]): Seq[Staged] = {
      //println(files + "\n" + existingFiles + "\n" + out + "\n")
      if(files.isEmpty) out ++ existingFiles
      else {
        val index = existingFiles.indexOf(files.head)

        if(index != -1) {
          val oldStage = existingFiles.take(index - 1) ++ existingFiles.drop(index + 1)
          println(oldStage)
          findExisting(files.tail, oldStage, out :+ files.head)
        }
        else findExisting(files.tail, existingFiles, out :+ files.head)
      }
    }
    findExisting(newFiles, oldFiles, Seq())
  }

  /**
   * Add files to the staged file: removes the old lines and adds others..
   * @param filePaths the sequence of file signatures (sha1)
   * @return an option, None if the folder doesn't exist, true otherwise.
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

  ///////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Takes the existing lines from the staged file, removes the elements to removes, and writes back.
   * @param filePaths the file signatures to remove
   * @return None if error, Some(true) otherwise.
   */
  def removeStagedFiles(filePaths: Seq[String]): Option[Boolean] = {
    val index: File = ".sgit/INDEX".toFile
    if(!FileManager.isFileOrDirExists(index)) None
    else {
      val content: Seq[String] = FileManager.readFile(index)
        .split("\n")
        .toIndexedSeq
      val newContent = content.filterNot(key => filePaths.contains(key.split(" ")(0)))
      index.overwrite("")
      newContent.foreach(file => FileManager.writeLineFile(index, file))
      Some(true)
    }
  }



}