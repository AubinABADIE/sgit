package objects

import java.util.Date

case class Commit(
  hash: String,
  timestamp: Date,
  message: String,
  parent: String,
  files: Seq[Staged]
)