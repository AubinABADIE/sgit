import scopt.OParser
import actions._
import better.files.File

case class Config(
  mode: String = "",
  files: Seq[File] = Seq()
)

object App extends App {

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
        .action((_,c) => c.copy(mode = "add"))
        .text("Add a file or many files to the version control")
        .children(
          arg[String]("<file> ...")
            .text("File to add. Can be the current directory '.', a filename or a regular expression")
            .unbounded()
            .required()
            .action((x, c) => c.copy(files = c.files :+ File(x)))
        ),

      cmd("commit")
        .action((_, c) => c.copy(mode = "commit"))
        .text("Commit the staged changes"),

      cmd("status")
        .action((_, c) => c.copy(mode = "status"))
        .text("Shows the current files status")
    )
  }

  /**
   * Check the input and redirect to the corresponding method
   */
  OParser.parse(parser, args, Config()) match {
    case Some(config) => {
      config.mode match {
        case "init" => Init.init()
        case "add" => Add.add(Config().files)
        case "commit" =>
        case "status" =>
        case _ => println("sgit: '" + config.mode + "'is not a sgit command")
      }
    }
  }
}
