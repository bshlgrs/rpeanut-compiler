package boolExpr

import binOperator._
import counter._
import expr._
import interInstr._
import varOrLit._

abstract class BoolExpr {
  override def toString: String = this match {
    case BoolBinOp(op, e1, e2) => e1.toString + " " + op.toString + " " + e2.toString
    case AndExpr(lhs, rhs) => "("+ lhs.toString + " && " + rhs.toString + ")"
    case OrExpr(lhs, rhs) => "("+ lhs.toString + " || " + rhs.toString + ")"
  }

  def toIntermediate(thenLabel: String, elseLabel: String): List[InterInstr] = throw new Exception("unimplemented")

}

case class BoolBinOp(op: BoolBinOperator, lhs: Expr, rhs: Expr) extends BoolExpr {
  override def toIntermediate(thenLabel: String, elseLabel: String): List[InterInstr] = {
    val (lhsCode, lhsResult) = lhs.toIntermediate()
    val (rhsCode, rhsResult) = rhs.toIntermediate()
    val outputVar = Counter.getTempVarName()

    val comparisonInstrs: List[InterInstr] = op match {
      case Equals => List(BinOpInter(SubOp, lhsResult, rhsResult, outputVar),
                          JumpNZInter(elseLabel, VOLVar(outputVar)),
                          JumpInter(thenLabel))

      case GreaterThan => List(BinOpInter(SubOp, rhsResult, lhsResult, outputVar),
                               JumpNInter(thenLabel, VOLVar(outputVar)),
                               JumpInter(elseLabel))
    }

    (lhsCode ::: rhsCode ::: comparisonInstrs)
  }
}

case class AndExpr(lhs: BoolExpr, rhs: BoolExpr) extends BoolExpr

case class OrExpr(lhs: BoolExpr, rhs: BoolExpr) extends BoolExpr

abstract class BoolBinOperator {
  override def toString: String = this match {
    case Equals => "=="
    case GreaterThan => ">"
  }
}

case object Equals extends BoolBinOperator
case object GreaterThan extends BoolBinOperator
