package objects

import java.util.Date

case class Commit(
  hash: String,
  timestamp: Date,
  message: String,
  parent: String,
  files: Seq[Staged]
)

//CommitObject(name: String, desc: String, parents: Seq[String], time: Date, files: List[StagedFile])
