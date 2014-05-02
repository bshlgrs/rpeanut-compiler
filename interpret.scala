abstract class Expr {
  override def toString: String = this match {
    case Lit(n) => n.toString
    case Var(n) => n
    case BinOp(op, l, r) => "(" + l.toString + op.toString + r.toString + ")"
    case Load(exp) => "(*" + exp.toString + ")"
  }

  val isPrimitive: Boolean = this match {
    case Lit(_) => true
    case Var(_) => true
    case _ => false
  }
}
case class Lit(n: Int) extends Expr
case class BinOp(op: BinOperator, e1: Expr, e2: Expr) extends Expr
case class Var(name: String) extends Expr
case class Load(exp: Expr) extends Expr

abstract class BinOperator {
  override def toString: String = this match {
    case AddOp => "+"
    case MulOp => "*"
  }
}

case object AddOp extends BinOperator
case object MulOp extends BinOperator

abstract class BoolExpr {
  override def toString: String = this match {
    case BoolBinOp(op, e1, e2) => e1.toString + " " + op.toString + " " + e2.toString
  }
}

case class BoolBinOp(op: BoolBinOperator, lhs: Expr, rhs: Expr) extends BoolExpr

abstract class BoolBinOperator {
  override def toString: String = this match {
    case Equals => "=="
    case GreaterThan => ">"
  }
}

case object Equals extends BoolBinOperator
case object GreaterThan extends BoolBinOperator


abstract class Statement {
  override def toString: String = this match {
    case Assignment(name, rhs) => name + " = " + rhs.toString + ";"
    case IndirectAssignment(lhs, rhs) => "*" + lhs.toString + " = " + rhs.toString+";"
    case IfElse(condition, thenBlock, elseBlock) => ("if (" + condition.toString +
                ") { \n"+thenBlock.mkString("\n") + "} \nelse {\n" +
                        elseBlock.mkString("\n") + "\n}")
  }

}
case class Assignment(name: String, rhs: Expr) extends Statement
case class IndirectAssignment(lhs: Expr, rhs: Expr) extends Statement
case class IfElse(condition: BoolExpr,
                  thenBlock: List[Statement],
                  elseBlock: List[Statement]) extends Statement


// swap = array[i]
val firstLine = Assignment("temp", Load(BinOp(AddOp, Var("array"), Var("i"))))
// array[i] = array[i+1]
val secondLine = IndirectAssignment(BinOp(AddOp, Var("array"), Var("i")),
                                  Load(BinOp(AddOp,
                                      BinOp(AddOp, Lit(1), Var("array")),
                                      Var("i"))))
// array[i+1] = swap
val thirdLine = IndirectAssignment(BinOp(AddOp,
                                      BinOp(AddOp, Lit(1), Var("array")),
                                      Var("i")),
                                      Var("swap"))

val exampleCode = List(firstLine, secondLine, thirdLine)

// arr[i] > arr[i+1]
val exampleBool = BoolBinOp(GreaterThan,
                            Load(BinOp(AddOp, Var("array"), Var("i"))),
                            Load(BinOp(AddOp,
                                      BinOp(AddOp, Lit(1), Var("array")),
                                      Var("i"))))

val moreComplexExampleCode = IfElse(exampleBool, exampleCode, List())

abstract class VarOrLit {
  override def toString: String = this match {
    case VOLVar(n) => n
    case VOLLit(n) => n.toString
  }

  def getVar(): String = this match {
    case VOLLit(n) => throw new Exception("gah")
    case VOLVar(n) => n
  }
}

case class VOLVar(n: String) extends VarOrLit
case class VOLLit(n: Int) extends VarOrLit

abstract class InterInstr {
  override def toString: String = this match {
    case BinOpInter(op, in1, in2, out) => s"$out\t= ${in1.toString} ${op.toString} ${in2.toString}"
    case LoadInter(source, target) => s"${target.toString}\t= *${source.toString}"
    case StoreInter(source, target) => s"*${target.toString}\t= ${source.toString}"
    case CopyInter(source, target) => s"${target.toString}\t= ${source.toString}"
  }
}

case class BinOpInter(op: BinOperator, in1: VarOrLit, in2: VarOrLit, out: String)
                                            extends InterInstr
case class LoadInter(sourceVar: String, targetVar: String) extends InterInstr
case class StoreInter(sourceVar: String, targetVar: String) extends InterInstr
case class CopyInter(sourceVar: VarOrLit, targetVar: String) extends InterInstr

// global counter, why not
var counter = 0
def getCounter(): String = {
  counter = counter + 1
  "$tmp" + counter.toString
}

def exprToIntermediate(expr: Expr): (List[InterInstr], VarOrLit) = expr match {
  case Lit(n) => (Nil, VOLLit(n))
  case BinOp(op, e1, e2) => {
    val (lhsInstr, lhsVar) = exprToIntermediate(e1)
    val (rhsInstr, rhsVar) = exprToIntermediate(e2)
    val out = getCounter()
    (lhsInstr ::: rhsInstr ::: List(BinOpInter(op, lhsVar, rhsVar, out)),
      VOLVar(out))
  }
  case Var(n) => (Nil, VOLVar(n))
  case Load(exp) => {
    val (rhsInstr, rhsVar) = exprToIntermediate(exp)
    val out = getCounter()
    rhsVar match {
      case VOLLit(_) => throw new Exception("illegal stuff")
      case VOLVar(n) => {
        (rhsInstr :+ LoadInter(n, out), VOLVar(out))
      }
    }
  }
}

def statementToIntermediate(stat: Statement): List[InterInstr] = stat match {
  case Assignment(name, rhs) => {
    var (exprInters, resultPlace) = exprToIntermediate(rhs)
    // This is a silly way of doing this. It generates extraneous copy
    // instructions.
    exprInters :+ CopyInter(resultPlace, name)
  }
  case IndirectAssignment(lhs, rhs) => {
    val (lhsInstr, lhsVar) = exprToIntermediate(lhs)
    val (rhsInstr, rhsVar) = exprToIntermediate(rhs)
    (lhsInstr ::: rhsInstr) :+ StoreInter(lhsVar.getVar(), rhsVar.getVar())
  }
}

def statementsToIntermediate(block: List[Statement]): List[InterInstr] = {
  block.flatMap(statementToIntermediate)
}

println(moreComplexExampleCode)
// println(statementsToIntermediate(exampleCode).mkString("\n"))
