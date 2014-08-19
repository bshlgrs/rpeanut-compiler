package function

import statement._
import interInstr._
import assembly._
import assemblyMaker._
import expr._
import varOrLit._
import counter._
import module._

class Function(val name: String, params: List[String], val vars: Map[String, Integer],
                             body: List[Statement]) {
  override def toString() = ("def " + name + "(" + params.mkString(", ") +
              ") (" + vars.mkString(", ") + ") {\n" + body.mkString("\n") + "\n}")

  def toIntermediate(module: Module): List[InterInstr] = {
    var out = List[InterInstr]()
    for (statement <- body) {
      out = out ::: statement.toIntermediate(module)
    }
    out
  }

  def toInline(vars: List[VarOrLit], module: Module): (List[InterInstr], VarOrLit) = {
    val newVarNames = vars map (t => t -> (t match {
          case VOLVar(x) => VOLVar(Counter.getTempVarName())
          case VOLLit(x) => VOLLit(x)
        })) toMap : Map[varOrLit.VarOrLit,varOrLit.VarOrLit];

    val endLabel = Counter.getCounter() + "-end-inline-" + name;
    val out = Counter.getTempVarName()

    val copies = (for ( (inVar, hereVar) <- vars.zip(params)) yield {
      CopyInter(inVar, hereVar)
    }) : List[InterInstr]

    val mainCode = toIntermediate(module).map{_.inline(newVarNames, out, endLabel)
              }.flatten : List[InterInstr]

    (copies ++ mainCode, VOLVar(out))
  }

  val allExpressions: List[Expr] = body.map{_.allExpressions}.flatten

  val strings: List[String] = allExpressions.map{_.strings}.flatten

  def blocks(module: Module): List[Block] = {
    AssemblyMaker.separateIntoBlocks(toIntermediate(module))
  }

  // assumes uniform size
  val returnPosition = - params.length - 1

  // Trust me, I write compilers, I know what I'm doing...
  // val localVars: List[String] =  {
  //   allVars.filter{isLocal(_)}
  // }

    // Trust me, I write compilers, I know what I'm doing...
  def localVars(module: Module): List[String] =  {
    blocks(module).map{_.varsMentioned}.flatten.groupBy{x=>x}
      .mapValues (_.length).filter{_._2 > 1}.keys.toList.filterNot(params.contains(_))
  }

  def allVars(module: Module): List[String] = {
    blocks(module).map{_.varsMentioned}.flatten.distinct
  }

  def varsModified(module: Module): List[String] = {
    blocks(module).map{_.code.map{_.outputVars()}}.flatten.flatten.distinct
  }

  def isLocal(name: String, module: Module): Boolean = {
    blocks(module).map{_.varsMentioned}.flatten.count{_ == name} > 1
  }

  def localsMap(module: Module): Map[String, Int] = {
    var dict = collection.mutable.Map[String, Int]()

    for ((x:String, i:Int) <- params.view.zipWithIndex) {
      dict(x) = i - params.length
    }

    var currentPlace = 1

    for ((x:String, i:Int) <- (localVars(module) ++ vars.keys).removeDuplicates.zipWithIndex) {
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

  def toAssembly(globals: List[String], module: Module): List[Assembly] = {
    // todo: rename lol
    val lol = (vars.values.toList.asInstanceOf[List[Int]]).sum
    ASM_Label(name) +:
    (for (block <- blocks(module)) yield { new
      BlockAssembler(block,
                     localsMap(module),
                     globals,
                     Some(returnPosition),
                     localVars(module).length - vars.size + lol)
                              .assemble() }).flatten :+ ASM_Return
  }

  // needs rewriting
  // def functionDependencies(): List[String] = {
  //   for (CallInter(x, _, _) <- this.toIntermediate(module)) yield x
  // }

  // def isProcedure(): Boolean = {
  //   functionDependencies().length == 0
  // }
}