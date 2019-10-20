import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import better.files._
import actions.{Add, Branch, Commit, Init}
import utils.{BranchManager, FileManager}

class BranchTest extends FunSpec with BeforeAndAfter with Matchers {

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

  describe("With no commit"){
    it("should do nothing"){
      assert(!Branch.branch(Some("master"), verbose = false))
    }
  }
  describe("With an existing branch") {
    it("should create a new branch and update the last commit") {
      val hash: String = Commit.commit("Commit").get
      Branch.branch(Some("dev"), verbose = false)
      FileManager.readFile(".sgit/refs/heads/dev") should include (hash)
    }
    it("Should be referenced as the HEAD"){
      Commit.commit("Commit")
      Branch.branch(Some("dev"), verbose = false)
      FileManager.getFile(".sgit/refs/heads/dev").get should be (".sgit/refs/heads/dev".toFile)
    }
  }
}