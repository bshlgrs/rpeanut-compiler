import parser._
import scala.collection.mutable.ListBuffer
import standardLibrary._

object Compile extends CParser {
  val output = new StringBuilder()
  val stringSection: ListBuffer[String] = new ListBuffer()
  val globals: ListBuffer[String] = new ListBuffer()
  def main(args: Array[String]) {
    args(0) match {
      case "readAndRun" => {
        val inputString = io.Source.stdin.getLines.mkString("\n")
        compile(inputString)
        println(RPeANutWrapper.runAssembly(output.toString()))
      }
      case "compile" => {
        compile(io.Source.fromFile(args(1)).mkString)
        println(output.toString)
        return;
      }
    }
  }

  def compile(inputString: String) {
    output.append("; Compiled by Buck's rPeANUt compiler!!!\n")
    output.append("0x0001:\n\tjump 0x0100\n\n")
    parseAll(program, inputString) match {
      case Success(result, _) => {
        for (function <- result) {
          for (x <- function.strings) {
            val hash = "string-"+x.hashCode()
            globals.append(hash)
            stringSection.append(hash+": block #"+x)
          }

          // println(function.toIntermediate().mkString("\n"))
          if (function.name == "main")
            output.append("0x0100:\n")
          output.append(function.toAssembly(globals.toList).mkString("\n")+"\n")

          println(function.name+ " is procedure: " +function.isProcedure())
        }

        output.append("\n\n; Library functions:\n")
        for ((function, implementation) <- StandardLibrary.standardLibrary) {
          if (output.toString.contains(function))
            output.append(implementation)
        }

        output.append("\n; Data section: \n"+stringSection.distinct.mkString("\n\n"))
        // println(RPeANutWrapper.runAssembly(output.toString()))

      }
      case x => println("Parse error"); println(x)
    }
  }
}