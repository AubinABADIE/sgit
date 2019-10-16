package utils

import better.files._
import objects.Staged

object StageManager {

  /**
   * Gets the staged files from the sgit folder.
   * @return a sequence of files
   */
  def getStagedFiles(): Option[Seq[Staged]] = {
    val index : File = ".sgit/INDEX".toFile
    if(FileManager.isFileOrDirExists(index) || FileManager.isEmpty(index)) None
    else {
      val content = index.contentAsString()
        .replace("\r", "")
        .split("\n")
        .filterNot(string => string.isEmpty)
      if(content.isEmpty) return None
      Some(content.toIndexedSeq.map((file: String) => {
        val split = file.split(" ")
        Staged(split(0), split(0))
      }))
    }
  }

  /**
   * Add files to the staged file: removes the old lines and adds others..
   * @param filePaths the sequence of file signatures (sha1)
   * @return an option, None if the folder doesn't exist, true otherwise.
   */
  def addStagedFiles(filePaths: Seq[Staged]): Option[Boolean] = {
    val index: File = ".sgit/INDEX".toFile
    if(!FileManager.isFileOrDirExists(index)) None
    else {
      index.createIfNotExists().overwrite("")
      val content: String = index.contentAsString()
      filePaths.foreach(file => {
        if(!content.contains(file.hash)) FileManager.writeLineFile(index, file.hash + " " + file.path)
      })
      Some(true)
    }
  }

  /**
   * Takes the existing lines from the staged file, removes the elements to removes, and writes back.
   * @param filePaths the file signatures to remove
   * @return None if error, Some(true) otherwise.
   */
  def removeStagedFiles(filePaths: Seq[String]): Option[Boolean] = {
    val index: File = ".sgit/INDEX".toFile
    if(!FileManager.isFileOrDirExists(index)) None
    else {
      val content: Seq[String] = index.contentAsString()
        .split("\n")
        .toIndexedSeq
      val newContent = content.filterNot(key => filePaths.contains(key.split(" ")(0)))
      index.overwrite("")
      newContent.foreach(file => FileManager.writeLineFile(index, file))
      Some(true)
    }
  }

  /**
   * Empties the stage file.
   */
  def deleteStage(): Unit = {
    val index: File = ".sgit/INDEX".toFile
    if(FileManager.isFileOrDirExists(index)) FileManager.deleteFileOrDir(index)
    else FileManager.createFile(index)
  }

  /**
   * Finds the duplicated files between the old stage and the new stage.
   * If the sha prints are different but the name is the same, it removes the old file from the final stage
   * @param newFiles The new files from the most recent add call.
   * @param oldFiles the existing files in the stage.
   * @return a tuple of sequence of files. _1 contains the new stage, _2 contains the files to delete.
   */
  def duplicatedStagedFiles(newFiles: Seq[Staged], oldFiles: Seq[Staged]): (Seq[Staged], Seq[Staged]) = {
    if(oldFiles == null) return (newFiles, Seq())

    val paths: Seq[String] = newFiles.map(_.path)
    val hashes: Seq[String] = newFiles.map(_.hash)
    //We keep the files that are duplicated but that don't have the same SHA than the added files.
    val filesToDelete: Seq[Staged] = oldFiles.filter(old => paths.contains(old.path) && !hashes.contains(old.hash))

    return (newFiles.concat(oldFiles.diff(filesToDelete)), filesToDelete)
  }

  /**
   * Removes the files that has not been modified from the previous commit.
   * @param newFiles the new files to add.
   * @return the new files minus the not modified ones.
   */
  def notModifiedFiles(newFiles: Seq[Staged]): Seq[Staged] = {
    val lastCommit = CommitManager.lastCommit()
    if(lastCommit.isEmpty) newFiles
    else {
      val committedFiles: Seq[Staged] = CommitManager.getCommit(lastCommit.get).get.files
      newFiles.diff(committedFiles)
    }
  }
}