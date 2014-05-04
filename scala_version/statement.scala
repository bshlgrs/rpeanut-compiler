package statement

import expr._
import binOperator._
import boolExpr._
import varOrLit._
import interInstr._
import counter.Counter
import statement._

abstract class Statement {
  override def toString: String = this match {
    case Assignment(name, rhs) => name + " = " + rhs.toString + ";"
    case IndirectAssignment(lhs, rhs) => "*" + lhs.toString + " = " + rhs.toString+";"
    case IfElse(condition, thenBlock, elseBlock) => ("if (" + condition.toString +
                ") { \n"+thenBlock.mkString("\n") + "} \nelse {\n" +
                        elseBlock.mkString("\n") + "\n}")
  }
  def toIntermediate(): List[InterInstr] = throw new Exception("not implemented")
}

case class Assignment(name: String, rhs: Expr) extends Statement {
  def toIntermediate(): List[InterInstr] = {
    var (exprInters, resultPlace) = rhs.toIntermediate()
    // This is a silly way of doing this. It generates extraneous copy
    // instructions.
    CommentInter(this.toString()) +: exprInters :+ CopyInter(resultPlace, name)
  }
}
case class IndirectAssignment(lhs: Expr, rhs: Expr) extends Statement {
  def toIntermediate(): List[InterInstr] = {
    val (lhsInstr, lhsVar) = lhs.toIntermediate()
    val (rhsInstr, rhsVar) = rhs.toIntermediate()
    CommentInter(this.toString()) +: (lhsInstr ::: rhsInstr) :+ StoreInter(lhsVar.getVar(), rhsVar.getVar())
  }
}

case class IfElse(condition: BoolExpr,
                  thenBlock: List[Statement],
                  elseBlock: List[Statement]) extends Statement {
  def toIntermediate(): List[InterInstr] = {
    val counter = Counter.getCounter();
    val conditionCode = condition.toIntermediate(counter) : List[InterInstr]
    val thenCode = StatementHelper.statementsToIntermediate(thenBlock)
    val elseCode = StatementHelper.statementsToIntermediate(elseBlock)

    return (conditionCode :::
            List(LabelInter("then-"+counter.toString)) :::
            (thenCode :+
            JumpInter("end-"+counter.toString) :+
            LabelInter("else-"+counter.toString)) :::
            (elseCode :+
            LabelInter("end-"+counter.toString)))
  }
}

object StatementHelper {
  def statementsToIntermediate(block: List[Statement]): List[InterInstr] = {
    var out = List[InterInstr]()
    for(statement <- block) {
      out = out ::: statement.toIntermediate()
    }
    out
  }
}