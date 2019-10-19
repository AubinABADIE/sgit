import org.scalatest._
import better.files._
import utils._
import actions.{Add, Init}

class AddTest extends FunSpec with BeforeAndAfter with Matchers {

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

  describe("First execution of the 'git add' command"){
    it("should create a new blob in objects/blobs") {
      val file: File = FileManager.getFile("file.txt").get
      val hash: String = file.sha1
      assert(FileManager.isFileOrDirExists(".sgit/objects/blobs/" + hash))
      FileManager.readFile(".sgit/objects/blobs/" + hash) should include ("Hello World!")
    }
    it("should add a new line in INDEX") {
      val file: File = FileManager.getFile("file.txt").get
      FileManager.readFile(".sgit/INDEX") should  include (file.sha1 + " file.txt")
    }
  }

  describe("Second execution of the 'git add' command"){
    describe("File has not been changed"){
      it("should do nothing") {
        val file: File = FileManager.getFile("file.txt").get
        val hash: String = file.sha1
        val indexContent = FileManager.readFile(".sgit/INDEX")
        val fileContent = FileManager.readFile(".sgit/objects/blobs/" + hash)

        Add.add(Seq(file))

        val indexContent2 = FileManager.readFile(".sgit/INDEX")

        assert(FileManager.isFileOrDirExists(".sgit/objects/blobs/" + hash))
        indexContent2 should include (indexContent)
        FileManager.readFile(".sgit/objects/blobs/" + hash) should include (fileContent)
      }
    }
    describe("File has been modified") {
      it("should add a new blob and update INDEX") {
        val file: File = FileManager.getFile("file.txt").get
        val hash = file.sha1

        FileManager.writeLineFile(file, "Toto")
        val hash2 = file.sha1
        assert(hash2 != hash)

        Add.add(Seq(file))

        assert(FileManager.isFileOrDirExists(".sgit/objects/blobs/" + hash2))
        FileManager.readFile(".sgit/INDEX") should include (hash2 + " file.txt")
      }
    }
  }
}
