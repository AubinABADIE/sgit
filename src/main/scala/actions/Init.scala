package actions

import utils.{FileManager, ConsoleOutput}

case object Init {

  def init(): Boolean = {
    ConsoleOutput.print("Initializing repository...")
    val res = createDir()
    if(res) ConsoleOutput.print("Repository initialized successfully!")
    else ConsoleOutput.printError("Error: .sgit directory already exists.")
    return res
  }

  def createDir(): Boolean = {
    if(FileManager.isFileOrDirExists(".sgit")) false
    else {
      FileManager.createDirectories (List (
      ".sgit/objects/commits",
      ".sgit/objects/blobs",
      ".sgit/refs/heads",
      ".sgit/refs/tags"
      ))

      FileManager.createFile(".sgit/refs/heads/master")
      FileManager.createFile (".sgit/INDEX")
      FileManager.createFile (".sgit/HEAD")
      FileManager.writeFile (".sgit/HEAD", "refs/heads/master")

      return true
    }
  }
}
