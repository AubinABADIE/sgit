import scopt.OParser
import actions._
import better.files._

case class Config(
  mode: String = "",
  stagedFiles: Seq[File] = Seq(),
  commitMessage: String = "",
  branchName: String = "",
  tagName: String = "",
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
        .text("Display the stage status"),

      cmd("branch")
        .action((_, c) => c.copy(mode = "branch"))
        .text("Create a new branch")
        .children(
          arg[String]("<branch name>")
            .required()
            .action((x, c) => c.copy(branchName = x))
            .text("Branch to be created"),
          opt[String]("av")
            .text("List all existing branches and tags")
        ),

      cmd(name = "tag")
        .action((_, c) => c.copy(mode = "tag"))
        .text("Add a tag reference in refs/tags/.")
        .children(
          arg[String]("<tag name>")
            .optional()
            .action((x, c) => c.copy(tagName = x))
            .text("name of the tag")
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
        case "log" =>
        case _ => println("sgit: '" + config.mode + "'is not a sgit command.")
      }
    }
    case _ => println("Something went wrong, please try again.")
  }
}
