package actions

import utils.{FileManager, ConsoleOutput}

case object Init {

  /**
   * Initialize the repo in the working directory
   * @return true if the repo has been initialized, false otherwise.
   */
  def init(): Boolean = {
    ConsoleOutput.print("Initializing repository...")
    val res = createDir()
    if(res) ConsoleOutput.print("Repository initialized successfully!")
    else ConsoleOutput.printError("Error: .sgit directory already exists.")
    return res
  }

  /**
   * Create the folder structure of .sgit
   * @return true if the directories has been created, false otherwise.
   */
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
