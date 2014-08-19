import parser._
import scala.collection.mutable.ListBuffer
import standardLibrary._
import module._

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
    parseAll(program, inputString) match {
      case Success(functions, _) => {
        val module = new Module(functions)
        println("successfully parsed, now compiling")
        for (function <- functions) {
          for (x <- function.strings) {
            val hash = "string-"+x.hashCode()
            globals.append(hash)
            stringSection.append(hash+": block #"+x)
          }

          val compiled_code = function.toAssembly(globals.toList, module)

          if (function.name == "main") {
            output.insert(0, compiled_code.mkString("\n")+"\n")
            output.insert(0, "0x0100:\n")
          }
          else {
            output.append(compiled_code.mkString("\n")+"\n")
          }

        }

        output.append("\n\n; Library functions:\n")
        for ((function, implementation) <- StandardLibrary.standardLibrary) {
          if (output.toString.contains(function))
            output.append(implementation)
        }

        output.insert(0,"0x0001:\n\tjump 0x0100\n\n")
        output.insert(0, "; Compiled by Buck's rPeANUt compiler!!!\n")

        output.append("\n; Data section: \n"+stringSection.distinct.mkString("\n\n"))

//         output.append("""
// ; This is a heap, which is used by malloc
// 0x3FF0:
// frontier:
//   block #0x4001
// next:
//   block #0x4000
// 0x4000:
//   block #-1
//   block 0x2FFF

// """)
        // println(RPeANutWrapper.runAssembly(output.toString()))

      }
      case x => println("Parse error"); println(x)
    }
  }
}