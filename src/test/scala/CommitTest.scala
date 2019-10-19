import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import better.files._
import actions.{Add, Commit, Init}
import utils.{CommitManager, FileManager}


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

//  describe("With files in the stage") {
//
//    describe("And no commits") {
//      it("Should return true") {
//        assert(Commit.commit("Commit").nonEmpty)
//      }
//      it("Should create a branch called master which references the commit") {
//        val out = Commit.commit("Commit")
//        assert(FileManager.isFileOrDirExists(".sgit/refs/heads/master"))
//        FileManager.readFile(".sgit/refs/heads/master") should include(out.get)
//      }
//      it("Should create a commit file in the objects/commits directory") {
//        val out = Commit.commit("Commit")
//        ".sgit/objects/commits/".toFile.children.toSeq should have length 1
//        assert(FileManager.isFileOrDirExists(".sgit/objects/commits/" + out.get))
//      }
//      it("Should be referenced by the last commit method") {
//        val out = Commit.commit("Commit")
//        CommitManager.lastCommit() should include (out.get)
//      }
//      it("Should have one child") {
//        Commit.commit("Commit")
//        val commit = CommitManager.getCommit(CommitManager.lastCommit()).get
//        commit.files should have length 1
//        commit.files.head.hash should include ("file.txt".toFile.sha1)
//      }
//    }

//    describe("And a commit that already exists") {
//      it("Should have 4 files in the directory (2 commits and 2 files)") {
//        Commit.commit("Commit")
//        "file.txt".toFile.appendLine("This is another test! :)")
//        Add.add(Seq("file.txt"))
//        Commit.commit("Commit")
//        ".sgit/objects/blobs/".toFile.children.toSeq should have length 2
//        ".sgit/objects/commits/".toFile.children.toSeq should have length 2
//      }
//      it("Should change the head") {
//        val first = Commit.commit("Commit")
//        "file.txt".toFile.appendLine("This is another test! :)")
//        Add.add(Seq("file.txt"))
//        Commit.commit("Commit")
//        CommitManager.lastCommit().get should not include first.get
//      }
//      it("Should reference the last commit as a parent") {
//        val parent = Commit.commit("Commit")
//        "file.txt".toFile.appendLine("This is another test! :)")
//        Add.add(Seq("file.txt"))
//        val curr = Commit.commit("Commit")
//        val commit = CommitManager.getCommit(CommitManager.lastCommit().get).get
//        commit.parent should include (parent.get)
//        commit.hash should include (curr.get)
//      }
//
//      describe("And a file with the same name"){
//        it("Should have only one file referenced"){
//          Commit.commit("Commit")
//          "file.txt".toFile.appendLine("This is another test! :)")
//          Add.add(Seq("file.txt"))
//          Commit.commit("Commit")
//          val commit = CommitManager.getCommit(CommitManager.lastCommit().get).get
//          commit.files should have length 1
//          commit.files.head.hash should include ("file.txt")
//        }
//      }
//
//      describe("And another file added"){
//        it("Should have 2 files referenced") {
//          Commit.commit("Commit")
//          "another.txt".toFile.createIfNotExists().appendLine("This is another file.")
//          Add.add(Seq("another.txt"))
//          Commit.commit("Commit")
//          val commit = CommitManager.getCommit(CommitManager.lastCommit().get).get
//          commit.files should have length 2
//          commit.files.head.hash should include ("file.txt")
//          commit.files.tail.head.hash should include ("another.txt")
//          "another.txt".toFile.delete()
//        }
//      }
//    }
//  }
//
//  describe("No files in the stage") {
//    it("should print an error and exit.") {
//      assert(Commit.commit("Commit").isEmpty)
//    }
//  }
}