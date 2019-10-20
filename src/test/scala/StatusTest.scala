import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import better.files._
import actions.{Init, Add, Commit, Status}
import utils.{CommitManager, FileManager, StageManager}
import objects.Staged

class StatusTest extends FunSpec with BeforeAndAfter with Matchers {

  before {
    if (FileManager.isFileOrDirExists(".sgit")) FileManager.deleteFileOrDir(".sgit")
    if (FileManager.isFileOrDirExists("file.txt")) FileManager.deleteFileOrDir("file.txt")

    Init.init()
    val file: File = FileManager.createFile("file.txt")
    FileManager.writeLineFile(file, "Hello World!")
  }
  after {
    if (FileManager.isFileOrDirExists(".sgit")) FileManager.deleteFileOrDir(".sgit")
    if (FileManager.isFileOrDirExists("file.txt")) FileManager.deleteFileOrDir("file.txt")
  }

  describe("With no commits or stage") {
    it("Should list all files as untracked") {
      val allFiles = FileManager.listFilesInDirectory(FileManager.wd())
      val res = StageManager.getUntrackedFiles(allFiles)
      res.length should equal(allFiles.length)
    }
  }
  describe("With a file in the stage") {
    it("Should have one file in ready to be committed") {
      val file = FileManager.getFile("file.txt").get
      Add.add(Seq(file))
      val stageDiff: Seq[Staged] = StageManager.getStagedFiles()
      val res = Status.getChangesToBeCommitted(stageDiff, Seq())
      res.get should have length 1
    }
  }
  describe("Wih a file commited and modified") {
    it("Should list it as not staged for commit") {
      val file = FileManager.getFile("file.txt").get
      Add.add(Seq(file))
      Commit.commit("Commit")
      file.appendLine("Toto")
      val allFiles: Seq[File] = FileManager.listFilesInDirectory(FileManager.wd())
      val untrackedFiles: Seq[File] = StageManager.getUntrackedFiles(allFiles)
      val commitedFiles:  Seq[Staged] = CommitManager.getCommit(CommitManager.lastCommit()).get.files
      val stagedFiles: Seq[Staged] = StageManager.getStagedFiles()
      val res = Status.getChangesNotStaged(untrackedFiles, commitedFiles, stagedFiles)
      res._1 should have length 1
    }
  }
}