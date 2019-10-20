package utils

import better.files._
import objects.{Blob, Staged}

import scala.annotation.tailrec

case object ObjectManager {

  def getObject(hash: String): Option[File] = {
    if(FileManager.isFileOrDirExists(".sgit/objects/blobs")) Some((".sgit/objects/blobs/" + hash).toFile)
    else None
  }

  def getObjects(hashes: Seq[String]): Seq[Option[File]] = {
    hashes.map(hash => {
      if(FileManager.isFileOrDirExists(hash)) Some(getObject(hash).get)
      else None
    })
  }

  def getObjectsFromRegex(str: String): Seq[File] = {
    if(!str.contains('*') && (str.toFile.exists && !str.toFile.isDirectory)) Seq(str.toFile)
    else FileManager.wd().glob(str).toSeq
  }

  def createObjects(files: Seq[File]): Seq[Blob] = {
    files.iterator.map(file => {
      writeObject(file)
      Blob(file.sha1, FileManager.wd().relativize(file).toString, FileManager.readFile(file))
    }).toSeq
  }

  def writeObject(file: File): Unit = {
    (".sgit/objects/blobs/" + file.sha1)
      .toFile
      .overwrite(FileManager.wd().relativize(file).toString)
      .appendLine()
      .appendLine(FileManager.readFile(file))
  }

  def deleteObject(name: String): Option[Boolean] = {
    val file = getObject(name).get
    if(FileManager.isEmpty(file)) None
    else {
      file.delete()
      Some(true)
    }
  }

  def compareObjects(f1: File, f2: File): Option[Boolean] = {
    if (!f1.exists || !f2.exists) None
    else Some(f1.isSameContentAs(f2))
  }

  /**
   * Converts a blob file (in memory) to a staged file (in memory).
   * These files aren't actually written, it's their representation in memory.
   * It uses a tail recursion for improved performance.
   * @param blobs the sequence of blobs to convert.
   * @return the sequence of staged files representing the blobs.
   */
  def blobsToStaged(blobs: Seq[Blob]): Seq[Staged] = {
    @tailrec
    def convert(blobs: Seq[Blob], out: Seq[Staged]): Seq[Staged] = {
      if(blobs.isEmpty) out
      else convert(blobs.tail, out :+ Staged(blobs.head.hash, blobs.head.path))
    }
    convert(blobs, Seq())
  }

  /**
   * Converts a file to a staged file representation.
   * @param files the sequence of files to convert
   * @return the sequence of staged file returned.
   */
  def filesToStaged(files: Seq[File]): Seq[Staged] = {
    @tailrec
    def convert(files: Seq[File], out: Seq[Staged]): Seq[Staged] = {
      if (files.isEmpty) out
      else convert(files.tail, out :+ Staged(files.head.sha1, FileManager.wd().relativize(files.head).toString))
    }
    convert(files, Seq())
  }

  /**
   * Converts staged files to blobs.
   * @param staged the files to convert
   * @return the blobs
   */
  def stagedToBlobs(staged: Seq[Staged]): Seq[Blob] = {
    @tailrec
    def convert(stagedFiles: Seq[Staged], out: Seq[Blob]): Seq[Blob] = {
      if(stagedFiles.isEmpty) out
      else {
        val file: File = getObject(stagedFiles.head.hash).get
        val blob = Blob(stagedFiles.head.hash, stagedFiles.head.path, FileManager.readFile(file).substring(FileManager.readFile(file).indexOf("\n")))
        convert(stagedFiles.tail, out :+ blob)
      }
    }
    convert(staged, Seq())
  }

  /**
   * gets the names and the hash prints from a sequence of staged files.
   * @param stagedFiles the staged files.
   * @return a tuple, the first one containing the name and the second the hash prints.
   */
  def nameAndHashFromStageFiles(stagedFiles: Seq[Staged]): (Seq[String], Seq[String]) = (stagedFiles.map(_.path), stagedFiles.map(_.hash))
}
