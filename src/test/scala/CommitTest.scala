import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import better.files._
import actions.{Add, Commit, Init}
import utils.{CommitManager, FileManager, StageManager}


class CommitTest extends FunSpec with BeforeAndAfter with Matchers {

  before{
    if(FileManager.isFileOrDirExists(".sgit")) FileManager.deleteFileOrDir(".sgit")
    if(FileManager.isFileOrDirExists("file.txt")) FileManager.deleteFileOrDir("file.txt")

    Init.init()
    val file : File = FileManager.createFile("file.txt")
    FileManager.writeLineFile(file, "Hello World!")
    Add.add(Seq(file))
  }
  after {
    if(FileManager.isFileOrDirExists(".sgit")) FileManager.deleteFileOrDir(".sgit")
    if(FileManager.isFileOrDirExists("file.txt")) FileManager.deleteFileOrDir("file.txt")
  }

  describe("With files in the stage") {

    describe("And no commits") {
      it("Should return true") {
        assert(Commit.commit("Commit").nonEmpty)
      }
      it("Should create a branch called master which references the commit") {
        val out = Commit.commit("Commit")
        assert(FileManager.isFileOrDirExists(".sgit/refs/heads/master"))
        FileManager.readFile(".sgit/refs/heads/master") should include(out.get)
      }
      it("Should create a commit file in the objects/commits directory") {
        val out = Commit.commit("Commit")
        ".sgit/objects/commits/".toFile.children.toSeq should have length 1
        assert(FileManager.isFileOrDirExists(".sgit/objects/commits/" + out.get))
      }
      it("Should be referenced by the last commit method") {
        val out = Commit.commit("Commit")
        CommitManager.lastCommit() should include (out.get)
      }
      it("Should have one child") {
        Commit.commit("Commit")
        val commit = CommitManager.getCommit(CommitManager.lastCommit()).get
        commit.files should have length 1
        commit.files.head.hash should include ("file.txt".toFile.sha1)
      }
    }

    describe("And a commit that already exists") {
      it("Should have 4 files in the directory (2 commits and 2 blobs)") {
        Commit.commit("Commit")
        val file = FileManager.getFile("file.txt").get
        FileManager.writeLineFile(file, "Toto")
        Add.add(Seq(file))
        Commit.commit("Commit")
        ".sgit/objects/blobs/".toFile.children.toSeq should have length 2
        ".sgit/objects/commits/".toFile.children.toSeq should have length 2
      }
      it("Should change the head") {
        val first = Commit.commit("Commit")
        val file = FileManager.getFile("file.txt").get
        FileManager.writeLineFile(file, "Toto")
        Add.add(Seq(file))
        Commit.commit("Commit")
        CommitManager.lastCommit() should not include first.get
      }
      it("Should reference the last commit as a parent") {
        val parent = Commit.commit("Commit")
        val file = FileManager.getFile("file.txt").get
        FileManager.writeLineFile(file, "Toto")
        Add.add(Seq(file))
        val curr = Commit.commit("Commit")
        val commit = CommitManager.getCommit(CommitManager.lastCommit()).get
        commit.parent should include (parent.get)
        commit.hash should include (curr.get)
      }

      describe("And a file with the same name"){
        it("Should have only one file referenced"){
          Commit.commit("Commit")
          val file = FileManager.getFile("file.txt").get
          FileManager.writeLineFile(file, "Toto")
          Add.add(Seq(file))
          Commit.commit("Commit")
          val commit = CommitManager.getCommit(CommitManager.lastCommit()).get
          commit.files should have length 1
          commit.files.head.path should include ("file.txt")
        }
      }

      describe("And another file added"){
        it("Should have 2 files referenced") {
          Commit.commit("Commit 1")
          val file2 = FileManager.createFile("file2.txt")
          FileManager.writeLineFile(file2, "Toto")
          Add.add(Seq(file2))
          Commit.commit("Commit 2")
          val commit = CommitManager.getCommit(CommitManager.lastCommit()).get
          commit.files should have length 1
          commit.files.head.path should include ("file2.txt")
          FileManager.deleteFileOrDir("file2.txt")
        }
      }
    }
  }

  describe("No files in the stage") {
    it("should print an error and exit.") {
      StageManager.deleteStage()
      assert(Commit.commit("Commit").isEmpty)
    }
  }
}