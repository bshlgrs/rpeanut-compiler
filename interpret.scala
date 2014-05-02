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

abstract class Statement {
  override def toString: String = this match {
    case Assignment(name, rhs) => name + " = " + rhs.toString + ";"
    case IndirectAssignment(lhs, rhs) => "*" + lhs.toString + " = " + rhs.toString+";"
  }

}
case class Assignment(name: String, rhs: Expr) extends Statement
case class IndirectAssignment(lhs: Expr, rhs: Expr) extends Statement

// i = j * 4
val zerothLine = Assignment("i", BinOp(MulOp, Var("j"), Lit(4)))
// swap = array[i]
val firstLine = Assignment("temp", Load(BinOp(AddOp, Var("array"), Var("i"))))
// array[i] = array[i+1]
val secondLine = IndirectAssignment(BinOp(AddOp, Var("array"), Var("i")),
                                  Load(BinOp(AddOp,
                                      BinOp(AddOp, Lit(1), Var("array")),
                                      Var("i"))))
val exampleCode = List(zerothLine, firstLine, secondLine)

println (exampleCode.mkString("\n"))


abstract class VarOrLit {
  override def toString: String = this match {
    case VOLVar(n) => n
    case VOLLit(n) => "#" + n.toString
  }
}

case class VOLVar(n: String) extends VarOrLit
case class VOLLit(n: Int) extends VarOrLit

abstract class InterInstr {
  override def toString: String = this match {
    case BinOpInter(op, in1, in2, out) => s"${op.toString} ${in1.toString} ${in2.toString} $out"
    case LoadInter(source, target) => s"${target.toString} = *${source.toString}"
    case StoreInter(source, target) => s"*${target.toString} = ${source.toString}"
    case CopyInter(source, target) => s"${target.toString} = ${source.toString}"
  }
}

case class BinOpInter(op: BinOperator, in1: VarOrLit, in2: VarOrLit, out: String)
                                            extends InterInstr
case class LoadInter(sourceVar: String, targetVar: String) extends InterInstr
case class StoreInter(sourceVar: String, targetVar: String) extends InterInstr
case class CopyInter(sourceVar: VarOrLit, targetVar: String) extends InterInstr

def statementsToIntermediate(block: List[Statement]): List[InterInstr] = {
  var counter = 0
  def getCounter(): String = {
    counter = counter + 1
    "$temp_" + counter.toString
  }

  def statementToIntermediate(stat: Statement): List[InterInstr] = stat match {
    case Assignment(name, rhs) => {
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
      }
      var (exprInters, resultPlace) = exprToIntermediate(rhs)
      exprInters :+ CopyInter(resultPlace, name)
    }
  }

  block.flatMap(statementToIntermediate)
}

