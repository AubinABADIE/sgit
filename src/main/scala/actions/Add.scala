package actions

import better.files._
import utils.{ConsoleOutput, FileManager, ObjectManager, StageManager}
import objects.Staged

import scala.annotation.tailrec

object Add {

  /**
   * Adds certain files to the stage.
   * @param paths the files given by the user.
   */
  def add(paths: Seq[File]): Unit = {
    if(!FileManager.isFileOrDirExists(".sgit")) ConsoleOutput.printError("Repository had not been initialized yet. Please run 'sgit init'.")
    if(paths.isEmpty) ConsoleOutput.printError("There are no files to add in stage or the files doesn't exist.")

    @tailrec
    def getFiles(filesPath: Seq[File], out: Seq[File]): Seq[File] = {
      if(filesPath.isEmpty) out
      else {
        val head = filesPath.head
        if(head.isDirectory) getFiles(filesPath.tail, out ++ FileManager.listFilesInDirectory(head))
        else getFiles(filesPath.tail, out :+ head)
      }
    }
    val files = getFiles(paths, Seq())

    val newFiles: Seq[Staged] = ObjectManager.blobsToStaged(ObjectManager.createObjects(files))
    val oldFiles: Seq[Staged] = StageManager.getStagedFiles()

    val staged = if(oldFiles.isEmpty) {
      StageManager.addStagedFiles(newFiles)
    }  else {
      val newStagedFiles: Seq[Staged] = StageManager.updateIndex(newFiles, oldFiles)
      StageManager.addStagedFiles(newStagedFiles)
    }

    if(staged.isEmpty)
      ConsoleOutput.print("No modifications found.")
    else {
      ConsoleOutput.print("Successfully added files to stage!")
    }
  }
}