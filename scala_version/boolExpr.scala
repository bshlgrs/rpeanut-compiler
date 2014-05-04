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

  def toIntermediate(labelNumber: Int): List[InterInstr] = throw new Exception("not implemented")
  /*
  toIntermediate is given a label number, and assumes the block will set up the
  following labels for it:

  then-labelNumber
  else-labelNumber
  end-labelNumber

  */
}

case class BoolBinOp(op: BoolBinOperator, lhs: Expr, rhs: Expr) extends BoolExpr {
  override def toIntermediate(labelNumber: Int): List[InterInstr] = {
    val (lhsCode, lhsResult) = lhs.toIntermediate()
    val (rhsCode, rhsResult) = rhs.toIntermediate()
    val outputVar = Counter.getTempVarName()
    val subtractInstr = BinOpInter(SubOp, lhsResult, rhsResult, outputVar)

    val comparisonInstr = op match {
      case Equals => JumpNZInter("else-" + labelNumber.toString, VOLVar(outputVar))
      case GreaterThan => JumpNInter("else-" + labelNumber.toString, VOLVar(outputVar))
    }

    (lhsCode ::: rhsCode ::: List[InterInstr](subtractInstr, comparisonInstr))
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
