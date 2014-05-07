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
    case While(condition, block) => ("while (" + condition.toString + ") {\n" +
                        block.mkString("\n") + "\n}")
    case Return(thing) => thing match {
      case Some(expr) => "return " + expr.toString + ";"
      case None => "return;"
    }
  }
  def toIntermediate(): List[InterInstr] = throw new Exception("not implemented")
}

case class Assignment(name: String, rhs: Expr) extends Statement {
  override def toIntermediate(): List[InterInstr] = {
    var (exprInters, resultPlace) = rhs.toIntermediate()

    resultPlace match {
      case VOLVar(x) => {
        var changedInters = exprInters.map {_.changeTarget(x, name)}
        CommentInter(this.toString()) +: changedInters
      }
      case VOLLit(n) => {
        List(CopyInter(VOLLit(n), name))
      }
    }

  }
}
case class IndirectAssignment(lhs: Expr, rhs: Expr) extends Statement {
  override def toIntermediate(): List[InterInstr] = {
    val (lhsInstr, lhsVar) = lhs.toIntermediate()
    val (rhsInstr, rhsVar) = rhs.toIntermediate()
    CommentInter(this.toString()) +: (lhsInstr ::: rhsInstr) :+
                                    StoreInter(rhsVar, lhsVar)
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

case class Return(value: Option[Expr]) extends Statement {
  override def toIntermediate(): List[InterInstr] = value match {
    case None => {
      List(CommentInter(this.toString), ReturnVoidInter)
    }
    case Some(expr) => {
      val (exprCode, returnPlace) = expr.toIntermediate
      (List(CommentInter(this.toString)) ::: exprCode ::: List(ReturnWithValInter(returnPlace)))
    }
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