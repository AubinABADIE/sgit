package utils

import better.files._
import objects.{Blob, Staged}

import scala.annotation.tailrec

case object ObjectManager {

  /**
   * Get file from hash ID
   * @param hash the hash ID of the blob
   * @return the file or None
   */
  def getObject(hash: String): Option[File] = {
    if(FileManager.isFileOrDirExists(".sgit/objects/blobs")) Some((".sgit/objects/blobs/" + hash).toFile)
    else None
  }

  /**
   * Get files from many hashes ID
   * @param hashes the hashes ID
   * @return a Seq of the files or None
   */
  def getObjects(hashes: Seq[String]): Seq[Option[File]] = {
    hashes.map(hash => {
      if(FileManager.isFileOrDirExists(hash)) Some(getObject(hash).get)
      else None
    })
  }

  /**
   * Create many blobs
   * @param files a Seq of files
   * @return a Seq of Blob
   */
  def createObjects(files: Seq[File]): Seq[Blob] = {
    files.iterator.map(file => {
      writeObject(file)
      Blob(file.sha1, FileManager.wd().relativize(file).toString, FileManager.readFile(file))
    }).toSeq
  }

  /**
   * Write the path and content of a file in a blob
   * @param file a file
   */
  def writeObject(file: File): Unit = {
    (".sgit/objects/blobs/" + file.sha1)
      .toFile
      .overwrite(FileManager.wd().relativize(file).toString)
      .appendLine()
      .appendLine(FileManager.readFile(file))
  }

  /**
   * Delete an object
   * @param name hash ID of the object to delete
   * @return true if the object has been delete, None otherwise
   */
  def deleteObject(name: String): Option[Boolean] = {
    val file = getObject(name).get
    if(FileManager.isEmpty(file)) None
    else {
      file.delete()
      Some(true)
    }
  }

  /**
   * Convert a Seq of blob file to a Seq staged file
   * @param blobs the sequence of blobs to convert
   * @return the Seq of staged files
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
   * Get the names and the hash IDs from a sequence of staged files
   * @param stagedFiles the staged files
   * @return Seq of tuples with path and hash ID
   */
  def nameAndHashFromStagedFiles(stagedFiles: Seq[Staged]): (Seq[String], Seq[String]) = (stagedFiles.map(_.path), stagedFiles.map(_.hash))
}
