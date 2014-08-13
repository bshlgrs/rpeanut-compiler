package function

import statement._
import interInstr._
import assembly._
import assemblyMaker._
import expr._

class Function(val name: String, params: List[String], val vars: Map[String, Integer],
                                                           body: List[Statement]) {
  override def toString() = ("def " + name + "(" + params.mkString(", ") +
              ") (" + vars.mkString(", ") + ") {\n" + body.mkString("\n") + "\n}")

  // Style question: should this be def or val?
  def toIntermediate(): List[InterInstr] = {
    var out = List[InterInstr]()
    for (line <- body) {
      out = out ::: line.toIntermediate
    }
    out
  }

  val allExpressions: List[Expr] = body.map{_.allExpressions}.flatten

  val strings: List[String] = allExpressions.map{_.strings}.flatten

  val blocks: List[Block] = AssemblyMaker.separateIntoBlocks(toIntermediate)

  val returnPosition = - params.length - 1

  // Trust me, I write compilers, I know what I'm doing...
  // val localVars: List[String] =  {
  //   allVars.filter{isLocal(_)}
  // }

    // Trust me, I write compilers, I know what I'm doing...
  val localVars: List[String] =  {
    blocks.map{_.varsMentioned}.flatten.groupBy{x=>x}
      .mapValues (_.length).filter{_._2 > 1}.keys.toList.filterNot(params.contains(_))
  }

  def allVars: List[String] = {
    blocks.map{_.varsMentioned}.flatten.distinct
  }

  def isLocal(name: String): Boolean = {
    blocks.map{_.varsMentioned}.flatten.count{_ == name} > 1
  }

  val localsMap: Map[String, Int] = {
    var dict = collection.mutable.Map[String, Int]()

    for ((x:String, i:Int) <- params.view.zipWithIndex) {
      dict(x) = i - params.length
    }

    var currentPlace = 1

    for ((x:String, i:Int) <- (localVars ++ vars.keys).removeDuplicates.zipWithIndex) {
      if (!dict.contains(x)) {
        dict(x) = currentPlace
        if (vars.contains(x))
          currentPlace = currentPlace + vars(x)
        else
          currentPlace = currentPlace + 1
      }
    }

    dict.toMap
  }

  def toAssembly(globals: List[String]): List[Assembly] = {
    val lol = (vars.values.toList.asInstanceOf[List[Int]]).sum
    ASM_Label(name) +:
    (for (block <- blocks) yield { new
      BlockAssembler(block,
                     localsMap,
                     globals,
                     Some(returnPosition),
                     localVars.length - vars.size + lol)
                              .assemble() }).flatten :+ ASM_Return
  }

  def functionDependencies(): List[String] = {
    for (CallInter(x, _, _) <- this.toIntermediate()) yield x
  }

  def isProcedure(): Boolean = {
    functionDependencies().length == 0
  }
}