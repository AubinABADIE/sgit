package utils

import better.files.File
import utils.FileManager._

case object ConsoleOutput {

  /**
   * Print a text in console
   * @param toPrint a string to print
   */
  def print(toPrint: String): Unit = println(toPrint)

  /**
   * Print an error in console
   * @param errorMsg an error to print
   */
  def printError(errorMsg: String): Unit = System.err.println(errorMsg)

  /**
   * Print a green text in console
   * @param toPrint a string to print
   */
  def printGreen(toPrint: String): Unit = println("\u001B[32m" + toPrint + "\u001B[0m")

  /**
   * Print a red text in console
   * @param toPrint a string to print
   */
  def printRed(toPrint: String): Unit = println("\u001B[31m"+ toPrint + "\u001B[0m")

  /**
   * Print a yellow text in console
   * @param toPrint a string to print
   */
  def printYellow(toPrint: String): Unit = println("\u001B[33m" + toPrint +  "\u001B[0m")

  /**
   * Print the changes to be committed in green for the Status command
   * @param lines a Seq of string
   */
  def printChangesToBeCommitted(lines: Seq[String]): Unit = {
    print("Changes to be committed:\n  (use 'sgit reset HEAD <file>...' to unstaged")
    lines.foreach(l => printGreen(l))
  }

  /**
   * Print the untracked files in yellow for the Status command
   * @param lines a Seq of string
   */
  def printUntrackedFiles(lines: Seq[File]): Unit = {
    print("Untracked files:\n  (use 'sgit add <file>...' to add those files to the stage)")
    lines.foreach(file => printYellow("\tuntracked: " + wd().relativize(file).toString))
  }

  /**
   * Print the changes not staged in red for the Status command
   * @param lines a Seq of string
   */
  def printChangesNotStaged(lines: Seq[String]): Unit = {
    print("Changed not staged for commit:\n (Use 'sgit add <file>...' to update what will be committed)")
    lines.foreach(l => printRed(l))
  }
}