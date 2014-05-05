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
  override def toIntermediate(): List[InterInstr] = {
    // This is wrong! It needs to replace the target everywhere, not just in the
    // last line.
    var (exprInters, resultPlace) = rhs.toIntermediate()
    var endInter = exprInters.last.changeTarget(name)
    CommentInter(this.toString()) +: exprInters.dropRight(1) :+ endInter
  }
}
case class IndirectAssignment(lhs: Expr, rhs: Expr) extends Statement {
  override def toIntermediate(): List[InterInstr] = {
    val (lhsInstr, lhsVar) = lhs.toIntermediate()
    val (rhsInstr, rhsVar) = rhs.toIntermediate()
    CommentInter(this.toString()) +: (lhsInstr ::: rhsInstr) :+ StoreInter(rhsVar.getVar(), lhsVar.getVar())
  }
}

case class IfElse(condition: BoolExpr,
                  thenBlock: List[Statement],
                  elseBlock: List[Statement]) extends Statement {
  override def toIntermediate(): List[InterInstr] = {
    val counter = Counter.getCounter();
    val conditionCode = condition.toIntermediate(counter) : List[InterInstr]
    val thenCode = StatementHelper.statementsToIntermediate(thenBlock)
    val elseCode = StatementHelper.statementsToIntermediate(elseBlock)

    return (List(CommentInter("if (" + condition.toString + ") {")) :::
            conditionCode :::
            List(CommentInter("} else {")) :::
            List(LabelInter("then-"+counter.toString)) :::
            (thenCode :+
            JumpInter("end-"+counter.toString) :+
            LabelInter("else-"+counter.toString)) :::
            (elseCode :+
            LabelInter("end-"+counter.toString)) :::
            List(CommentInter("}")))
  }
}

case class While(condition: BoolExpr, block: List[Statement]) extends Statement {
  override def toIntermediate(): List[InterInstr] = {
    val counter = Counter.getCounter();
    val conditionCode = condition.toIntermediate(counter) : List[InterInstr]

    (List(CommentInter("while ("+ condition.toString + ") {"),
         LabelInter("while-" + counter.toString)) :::
      conditionCode :::
      List(JumpInter("while-" + counter.toString),
        CommentInter("}"),
        LabelInter("else-"+counter.toString)))

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