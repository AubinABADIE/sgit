import actions.{Add, Commit, Init, Tag}
import better.files.File
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import utils.{CommitManager, FileManager, TagManager}

class TagTest extends FunSpec with BeforeAndAfter with Matchers {

  before {
    if (FileManager.isFileOrDirExists(".sgit")) FileManager.deleteFileOrDir(".sgit")
    if (FileManager.isFileOrDirExists("file.txt")) FileManager.deleteFileOrDir("file.txt")

    Init.init()
    val file: File = FileManager.createFile("file.txt")
    FileManager.writeLineFile(file, "Hello World!")
    Add.add(Seq(file))
  }
  after {
    if (FileManager.isFileOrDirExists(".sgit")) FileManager.deleteFileOrDir(".sgit")
    if (FileManager.isFileOrDirExists("file.txt")) FileManager.deleteFileOrDir("file.txt")
  }

  describe("Before any commit"){
    it("should return false"){
      assert(!Tag.tag(Some("tag")))
    }
    it("should get an empty Seq of tags") {
      assert(TagManager.getAllTags.isEmpty)
    }
  }
  describe("After a commit") {
    it("should create a tag") {
      Commit.commit("")
      assert(Tag.tag(Some("A tag")))
      FileManager.getFile(".sgit/refs/tags").get.children.toSeq should have length 1
    }
    it("should include the last tag") {
      Commit.commit("")
      Tag.tag(Some("tag"))
      FileManager.readFile(".sgit/refs/tags/tag") should include (CommitManager.lastCommit())
    }
    it("should list 1 tag") {
      Commit.commit("")
      Tag.tag(Some("tag"))
      assert(TagManager.getAllTags.get.size == 1)
    }
  }
}
