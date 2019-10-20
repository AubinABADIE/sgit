package utils

import better.files.File
import utils.FileManager._

case object ConsoleOutput {

  def print(toPrint: String): Unit = println(toPrint)

  def printError(errorMsg: String): Unit = System.err.println(errorMsg)

  def printGreen(toPrint: String): Unit = println("\u001B[32m" + toPrint + "\u001B[0m")

  def printRed(toPrint: String): Unit = println("\u001B[31m"+ toPrint + "\u001B[0m")

  def printYellow(toPrint: String): Unit = println("\u001B[33m" + toPrint +  "\u001B[0m")

  def printChangesToBeCommitted(lines: Seq[String]): Unit = {
    print("Changes to be committed:\n  (use 'sgit reset HEAD <file>...' to unstaged")
    lines.foreach(l => printGreen(l))
  }

  def printUntrackedFiles(lines: Seq[File]): Unit = {
    print("Untracked files:\n  (use 'sgit add <file>...' to add those files to the stage)")
    lines.foreach(file => printYellow("\tuntracked: " + wd().relativize(file).toString))
  }

  def printChangesNotStaged(lines: Seq[String]): Unit = {
    print("Changed not staged for commit:\n (Use 'sgit add <file>...' to update what will be committed)")
    lines.foreach(l => printRed(l))
  }
}