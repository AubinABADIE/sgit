import actions.Init
import org.scalatest._
import utils._

class InitTest extends FunSpec with BeforeAndAfter with Matchers {

  before {
    if(FileManager.isFileOrDirExists(".sgit")) FileManager.deleteFileOrDir(".sgit")
  }
  after {
    if(FileManager.isFileOrDirExists(".sgit")) FileManager.deleteFileOrDir(".sgit")
  }

  describe("First execution of the 'sgit init' command") {
    it("should create a '.sgit' directory with correct structure") {
      if(FileManager.isFileOrDirExists(".sgit")) FileManager.deleteFileOrDir(".sgit")

      Init.init()

      assert(FileManager.isFileOrDirExists(".sgit/objects/blobs"))
      assert(FileManager.isFileOrDirExists(".sgit/objects/commits"))
      assert(FileManager.isFileOrDirExists(".sgit/refs/heads"))
      assert(FileManager.isFileOrDirExists(".sgit/refs/tags"))
      assert(FileManager.isFileOrDirExists(".sgit/INDEX"))
      assert(FileManager.isFileOrDirExists(".sgit/HEAD"))
      assert(FileManager.readFile(".sgit/HEAD") == "ref: refs/heads/master")
    }
  }

  describe("Second execution of the 'sgit init' command") {
    it("should not do anything") {
      assert(Init.init())
      assert(!Init.init())
    }
  }
}