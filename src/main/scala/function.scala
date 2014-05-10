package function

import statement._
import interInstr._
import assembly._
import assemblyMaker._

class Function(val name: String, params: List[String], body: List[Statement]) {
  override def toString() = ("def " + name + "(" + params.mkString(", ") +
              ") {\n" + body.mkString("\n") + "\n}")

  def toIntermediate: List[InterInstr] = {
    var out = List[InterInstr]()
    for (line <- body) {
      out = out ::: line.toIntermediate
    }
    out
  }

  val blocks: List[Block] = AssemblyMaker.separateIntoBlocks(toIntermediate)

  val returnPosition = - params.length - 1

  // Trust me, I write compilers, I know what I'm doing...
  val localVars: List[String] =  {
    blocks.map{_.varsMentioned}.flatten.groupBy{x=>x}
      .mapValues (_.length).filter{_._2 > 1}.keys.toList.filterNot(params.contains(_))
  }

  val localsMap: Map[String, Int] = {
    var dict = collection.mutable.Map[String, Int]()

    for ((x:String, i:Int) <- params.view.zipWithIndex) {
      dict(x) = i - params.length
    }

    for ((x:String, i:Int) <- localVars.view.zipWithIndex) {
      dict(x) = i + 1
    }

    dict.toMap
  }

  def toAssembly: List[Assembly] = {
    ASM_Label(name) +:
    (for (block <- blocks) yield { new
                    BlockAssembler(block,
                                   localsMap,
                                   Some(returnPosition),
                                   localVars.length)
                                      .assemble() }).flatten :+ ASM_Halt
  }
}