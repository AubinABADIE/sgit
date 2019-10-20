package actions

import better.files._
import objects.Staged
import utils.{BranchManager, CommitManager, ConsoleOutput, FileManager, ObjectManager, StageManager}

case object Status {

  /**
   * Display the status of the repository
   */
  def status(): Unit = {
    if (!FileManager.isFileOrDirExists(".sgit")) ConsoleOutput.printError("Repository had not been initialized yet. Please run 'sgit init'.")
    else {
      val rootDir: File = ".sgit".toFile.parent
      val allFiles: Seq[File] = FileManager.listFilesInDirectory(rootDir)
      val untrackedFiles: Seq[File] = StageManager.getUntrackedFiles(allFiles)
      val stagedFiles: Seq[Staged] = StageManager.getStagedFiles()
      val lastCommit = CommitManager.lastCommit()
      val branch = BranchManager.getCurrentBranch()

      ConsoleOutput.print("On branch " + branch)
      if(lastCommit.isEmpty) ConsoleOutput.print("\nNo commit\n")
      else ConsoleOutput.print("Your branch is up-to-date on " + branch + "\n")

      // Untracked files
      if (lastCommit.isEmpty && stagedFiles.isEmpty) ConsoleOutput.printUntrackedFiles(untrackedFiles)

      else if(lastCommit.nonEmpty) {
        val commit = CommitManager.getCommit(lastCommit)
        val notInCurrentBranch = findFilesNotInBranch(allFiles, if(stagedFiles.nonEmpty) commit.get.files.concat(stagedFiles) else commit.get.files)
        val commitFiles: Seq[Staged] = commit.get.files
        val deletedFiles: Option[Seq[Staged]] = getDeletedFiles(allFiles, stagedFiles)

        // Changes to be committed
        val changesToBeCommitted = getChangesToBeCommitted(stagedFiles, commitFiles)
        if (changesToBeCommitted.nonEmpty) ConsoleOutput.printChangesToBeCommitted(changesToBeCommitted.get)

        // Changes not staged
        val changesNotChanged: (Seq[String], Seq[File]) = getChangesNotStaged(notInCurrentBranch, commitFiles, stagedFiles)
        if (changesNotChanged._1.nonEmpty && deletedFiles.isEmpty)
          ConsoleOutput.printChangesNotStaged(changesNotChanged._1)
        else if(changesNotChanged._1.nonEmpty && deletedFiles.nonEmpty) {
          val deleted: Seq[String] = deletedFiles.get.map(f => "\tdeleted: " + f.path)
          ConsoleOutput.printChangesNotStaged(changesNotChanged._1.concat(deleted))
        } else if(changesNotChanged._1.isEmpty && deletedFiles.nonEmpty)
          ConsoleOutput.printChangesNotStaged(deletedFiles.get.map(f => "\tdeleted: " + f.path))

        // Untracked files
        if (changesNotChanged._2.nonEmpty)  ConsoleOutput.printUntrackedFiles(changesNotChanged._2)
      }

      // Nothing was add
      else ConsoleOutput.print("Nothing to staged (create/copy files and use 'sgit add' to tracked them")
    }
  }

  /**
   * Finds the deleted files in the working directory.
   * @param allFiles in the working directory
   * @param stagedFiles the files in object/blobs
   * @return
   */
  def getDeletedFiles(allFiles: Seq[File], stagedFiles: Seq[Staged]): Option[Seq[Staged]] = {
    if(stagedFiles.isEmpty) return None
    val filesNames: Seq[String] = allFiles.map(f => FileManager.wd().relativize(f).toString)
    val deleted = stagedFiles.filterNot(f => filesNames.contains(f.path))
    if(deleted.isEmpty) None
    else Some(deleted)
  }

  /**
   * Lists the files ready to be committed.
   * if no commit, all files are listed as added.
   *
   * @param stagedFiles    the staged files
   * @param committedFiles the committed files, or none if there is no commit.
   * @return a sequence of strings to print.
   */
  def getChangesToBeCommitted(stagedFiles: Seq[Staged], committedFiles: Seq[Staged]): Option[Seq[String]] = {
    if (stagedFiles.isEmpty) None
    else if (committedFiles.isEmpty) Some(stagedFiles.map(file => "\tadded: " + file.path))
    else {
      val committed = ObjectManager.nameAndHashFromStagedFiles(committedFiles)
      //The name is not in the committed files
      val addedFiles: Seq[Staged] = stagedFiles.filterNot(file => committed._1.contains(file.path))
      //The name is in the committed files but the sha print isn't
      val modifiedFiles: Seq[Staged] = stagedFiles.filter(file => committed._1.contains(file.path) && !committed._2.contains(file.hash))
      //Delete: ?
      Some(addedFiles.map(file => "\tadded: " + file.path).concat(modifiedFiles.map(file => "\tmodified: " + file.path)))
    }
  }

  /**
   * Gets the files that are not staged for commit, by comparing the files from the working dir with the
   * staged and the committed (stored) ones.
   *
   * @param untrackedFiles   the files that are not in object (the modified and the untracked)
   * @param committedFiles the committed files
   * @param stagedFiles    the staged files
   * @return a tuple of sequences. The first is the lines to print, the second is the untracked files
   */
  def getChangesNotStaged(untrackedFiles: Seq[File], committedFiles: Seq[Staged], stagedFiles: Seq[Staged]): (Seq[String], Seq[File]) = {

    def calculateDiff(untrackedFiles: Seq[File], files: Seq[Staged]): (Seq[String], Seq[File]) = {
      val namesAndHash = ObjectManager.nameAndHashFromStagedFiles(files)
      val modified: Seq[File] = untrackedFiles.filter(file => namesAndHash._1.contains(FileManager.wd().relativize(file).toString) && !namesAndHash._2.contains(file.sha1))
      val added: Seq[File] = untrackedFiles
        .filter(file => namesAndHash._1.contains(FileManager.wd().relativize(file).toString))
        .diff(modified)

      val res1 = added
        .map(file => "\tadded: " + FileManager.wd().relativize(file))
        .concat(modified.map(file => "\tmodified: " + FileManager.wd().relativize(file)))

      (res1, untrackedFiles.diff(added).diff(modified))
    }

    if (committedFiles.isEmpty && stagedFiles.isEmpty) {
      (Seq(), untrackedFiles)
    }
    else if(committedFiles.nonEmpty && stagedFiles.isEmpty) {
      val res = calculateDiff(untrackedFiles, committedFiles)
      if(res._1.isEmpty) (Seq(), res._2)
      else (res._1, res._2)
    }
    else if(committedFiles.isEmpty && stagedFiles.nonEmpty) {
      val res = calculateDiff(untrackedFiles, stagedFiles)
      if(res._1.isEmpty) (Seq(), res._2)
      else (res._1, res._2)
    }
    else {
      val stage = calculateDiff(untrackedFiles, stagedFiles)
      val commit = calculateDiff(untrackedFiles, committedFiles)
      val files = stage._1.map(line =>
        line.split(" ").last.toFile)
        .concat(commit._1.map(line =>
          line.split(" ").last.toFile))
      (stage._1.concat(commit._1).distinct, stage._2.concat(commit._2).distinct.diff(files))
    }
  }

  /**
   * Finds the files that are not in the branch (possibly present in the objects folder but not in the last commit or stage)
   * @param files the files to check
   * @param storedFiles the stored files
   * @return the files that are not in the current branch.
   */
  def findFilesNotInBranch(files: Seq[File], storedFiles: Seq[Staged]): Seq[File] = {
    val storedSha = storedFiles.map(store => store.hash)
    files.filterNot(file => storedSha.contains(file.sha1))
  }
}
