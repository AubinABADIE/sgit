# SGIT

## Description
As part of our Functional Programming course, we had to develop a GIT-like in Scala.

## Installation
The first step is to download the source code by cloning this repository.

To launch the sbt console, make sure you have **Java 1.8** and **SBT** installed. The following commands are available:
- `sbt test`: runs the tests and shows the results
- `sbt run`: runs the application.
- `sbt compile`: produces the JVM code for the application.
- `sbt assembly`: produces a script file with all the dependencies inside, that can be executed as a standalone application.

## Using assembly script
Use the `sbt assembly` command to produce a script. You can find it at `/target/scala-2.13/sgit-x.x`, with `x.x` depending on the current version.

Then, you need to create an alias for the script for the sake of simplicity. 
On Linux, edit `.bashrc` by executing `nano ~/.bashrc`.

Add the following line: `alias sgit={project-path}/target/scala-2.13/sgit-x.x`

## Execute SGIT
From now on SGIT can be used in any folder on your computer.

To learn more about commands, run `sgit --help`.
#### Create:
- `sgit init`

#### Local Changes:
- `sgit status`
- `sgit diff` -> _not implemented_
- `sgit add <filename/filenames or . or regexp>`
- `git commit`

#### Commit History:
- `sgit log`
- `sgit log -p` -> _not implemented_
- `sgit log --stat` -> _not implemented_

#### Branches & Tags:
- `sgit branch <branch name>`
- `sgit branch -av`
- `sgit checkout <branch or tag or commit hash>` -> _not implemented_
- `sgit tag <tag name>` -> _not implemented_

#### Merge & Rebase:
- `sgit merge <branch>` -> _not implemented_
- `sgit rebase <branch>` -> _not implemented_
- `sgit rebase -i <commit hash or banch name>` -> _not implemented_