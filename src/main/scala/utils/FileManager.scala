package utils

import java.time.Instant

import better.files._

case object FileManager {

  def createDirectory(path: String): Unit = path.toFile.createIfNotExists(false,false)
  def createDirectories(paths: List[String]): Unit = paths.map(_.toFile.createIfNotExists(true,true))

  def createFile(path: String): File = path.toFile.createIfNotExists(false,false)
  def createFile(file: File): File = file.createIfNotExists(false,false)

  def readFile(path: String): String = path.toFile.contentAsString
  def readFile(file: File): String = file.contentAsString
  def readLineFile(path: String): List[String] = path.toFile.lines.toList
  def readLineFile(file: File): List[String] = file.lines.toList

  def writeFile(path: String, content: String): Unit = path.toFile.append(content)
  def writeFile(file: File, content: String): Unit = file.append(content)
  def writeLineFile(path: String, content: String): Unit = path.toFile.appendLine(content)
  def writeLineFile(file: File, content: String): Unit = file.appendLine(content)

  def deleteFileOrDir(path: String): Unit = path.toFile.delete()
  def deleteFileOrDir(file: File): Unit = file.delete()

  def isFileOrDirExists(path: String): Boolean = path.toFile.exists
  def isFileOrDirExists(file: File): Boolean = file.exists

  def isEmpty(path: String): Boolean = path.toFile.isEmpty
  def isEmpty(file: File): Boolean = file.isEmpty
  def isEmpty(files: Seq[Any]): Boolean = files.isEmpty

  def lastModifiedTime(path: String): Instant = path.toFile.lastModifiedTime
  def lastModifiedTime(file: File): Instant = file.lastModifiedTime

  def listFiles(path: File): Seq[File] = {
    if (path.isRegularFile) Array(path)
    else {
      val f = path.list(_.name != ".sgit").toList
      f ++ f.filter(_.isDirectory).flatMap(listFiles)
    }
  }
}
