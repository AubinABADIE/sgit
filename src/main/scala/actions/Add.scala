package actions

import better.files._
import utils.{ConsoleOutput, FileManager, ObjectManager, StageManager}
import objects.{Blob, Staged}

import scala.annotation.tailrec

object Add {

  /**
   * Adds certain files to the stage.
   * This method is not RT nor pure
   * @param paths the files given by the user.
   */
  def add(paths: Seq[File]): Unit = {
    @tailrec
    def getFiles(filePaths: Seq[File], out: Seq[File]): Seq[File] = {
      if(filePaths.isEmpty) return out
      val files = FileManager.listFiles(filePaths.head)
      getFiles(filePaths.tail, out ++ files)
    }
    val files = getFiles(paths, Seq())

    if(files.isEmpty)
      ConsoleOutput.printError("Repository had not been initialized yet. Please run 'sgit init'.")
    else {
      val newFiles: Seq[Blob] = ObjectManager.createObjects(files).get
      val stagedFiles = StageManager.getStagedFiles().orNull
      val modifications: (Seq[Staged], Seq[Staged]) = StageManager.duplicatedStagedFiles(ObjectManager.blobsToStaged(newFiles), stagedFiles)

      if(modifications._2.nonEmpty) {
        @tailrec
        def deleteObjects(files: Seq[Staged]): Unit = {
          if(files.isEmpty) return
          ObjectManager.deleteObject(files.head.hash)
          deleteObjects(files.tail)
        }
        deleteObjects(modifications._2)
      }
      val staged: Option[Boolean] = StageManager.addStagedFiles(/*StageManager.notModifiedFiles(*/modifications._1)/*)*/ //Has side effects

      if(staged.isEmpty)
        ConsoleOutput.print("Can't add the files to stage.")
      else {
        ConsoleOutput.print("Successfully added files to stage!")
      }
    }
  }
}