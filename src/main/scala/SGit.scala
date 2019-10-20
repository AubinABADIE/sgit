import scopt.OParser
import actions._
import better.files._

case class Config(
  mode: String = "",
  stagedFiles: Seq[File] = Seq(),
  commitMessage: String = "",
  branchName: Option[String] = None,
  verbose: Boolean = false,
  tagName: Option[String] = None,
  checkout: String = "",
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

      cmd("status")
        .action((_, c) => c.copy(mode = "status"))
        .text("Display the stage status"),

      cmd(name = "commit")
        .action((_, c) => c.copy(mode = "commit"))
        .text("Commit the staged changes")
        .children(
          opt[String]('m', name = "message")
            .action((x, c) => c.copy(commitMessage = x))
            .text("Commit message")
            .required()
        ),

      cmd("log")
        .action((_, c) => c.copy(mode = "log"))
        .text("Display the commit logs")
        .children(
          opt[Unit]("p")
            .text("Show changes overtime"),
          opt[Unit]("stat")
            .text("Show stats about changes overtime")
        ),

      cmd("branch")
        .action((_, c) => c.copy(mode = "branch"))
        .text("Create a new branch")
        .children(
          arg[String]("<branch name>")
            .optional()
            .action((x, c) => c.copy(branchName = Some(x)))
            .text("Branch to be created"),
          opt[Unit]("verbose")
            .abbr("av")
            .action((_, c) => c.copy(verbose = true))
            .text("List all existing branches and tags")
        ),

      cmd(name = "tag")
        .action((_, c) => c.copy(mode = "tag"))
        .text("Add a tag reference in refs/tags/.")
        .children(
          arg[String]("<tag name>")
            .optional()
            .action((x, c) => c.copy(tagName = Some(x)))
            .text("name of the tag")
        ),
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
        case "status" => Status.status()
        case "commit" => Commit.commit(config.commitMessage)
        case "log" => Log.logs()
        case "branch" => Branch.branch(config.branchName, config.verbose)
        case "tag" => Tag.tag(config.tagName)
        case _ => println("sgit: '" + config.mode + "'is not a sgit command.")
      }
    }
    case _ => println("Something went wrong, please try again.")
  }
}
