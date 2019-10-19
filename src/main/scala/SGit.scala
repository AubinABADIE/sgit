import scopt.OParser
import actions._
import better.files._

case class Config(
  mode: String = "",
  stagedFiles: Seq[File] = Seq(),
  commitMessage: String = ""
){}

object SGit extends App {

  val builder = OParser.builder[Config]
  val parser = {
    import builder._
    OParser.sequence(
      programName("sgit"),
      head("sgit", "0.1"),

      help("help")
        .text(""),

      cmd("init")
        .action((_, c) => c.copy(mode = "init"))
        .text("Initializes a new sgit in the directory"),

      cmd("add")
        .action((_, c) => c.copy(mode = "add"))
        .text("Add a file or many files to the version control")
        .children(
          arg[String]("<file>...")
            .action((x, c) => c.copy(stagedFiles = c.stagedFiles :+ x.toFile))
            .text("File to add. Can be the current directory '.', a filename or a regular expression")
            .unbounded()
            .required()
        ),

      cmd(name = "commit")
        .action((_, c) => c.copy(mode = "commit"))
        .text("Commit the staged changes")
        .children(
          opt[String]('m', name = "message")
            .action((x, c) => c.copy(commitMessage = x))
            .text("Commit message")
            .required()
        ),

      cmd("status")
        .action((_, c) => c.copy(mode = "status"))
        .text("Shows the current files status"),

//      cmd(name = "tag")
//        .action((_, c) => c.copy(mode = "tag"))
//        .text("Add a tag reference in refs/tags/.")
//        .children(
//          arg[String]("<tag name>")
//            .optional()
//            .action((x, c) => c.copy(tagName = x))
//            .text("name of the tag")
//        ),
//
//        cmd(name = "branch")
//        .action((_, c) => c.copy(mode = "branch", showBranch = true))
//        .text("Create a new branch")
//        .children(
//          arg[String](name = "<branch name>")
//            .action((x, c) => c.copy(file = x))
//            .optional()
//            .text("name of the branch you are creating"),
//          opt[Unit]('a', name = "all")
//            .action((_, c) => c.copy(showBranch = true, showTag = true))
//            .text("List all branches and tags"),
//          opt[Unit]('v', name = "verbose")
//            .action((_, c) => c.copy(verbose = true))
//        ),
    )
  }

  /**
   * Check the input and redirect to the corresponding method
   */
  OParser.parse(parser, args, Config()) match {
    case Some(config) => {
      config.mode match {
        case "init" => Init.init()
        case "add" => Add.add(config.stagedFiles)
        case "commit" => Commit.commit(config.commitMessage)
        case "status" =>
        case "branch" =>
        case "tag" =>
        case _ => println("sgit: '" + config.mode + "'is not a sgit command.")
      }
    }
    case _ => println("Something went wrong, please try again.")
  }
}
