package expr

import binOperator._
import interInstr._
import varOrLit._
import counter.Counter
import boolExpr._
import statement._

sealed abstract class Expr {
  override def toString: String = this match {
    case Lit(n) => n.toString
    case Var(n) => n
    case BinOp(op, l, r) => "(" + l.toString + op.toString + r.toString + ")"
    case Load(exp) => "(*" + exp.toString + ")"
    case FunctionCall(name, args) => name + "(" + args.mkString(", ") + ")"
    case IfExpression(condition, thenExpr, elseExpr) => ("(" + condition.toString
          + ") ? (" + thenExpr.toString + ") : (" + elseExpr.toString + ")")
    case StringLiteral(value) => value
    case PointerToName(name) => "&" + name
  }

  def allExpressions: List[Expr] = this match {
    case BinOp(_, l, r) => (this +: l.allExpressions) ::: r.allExpressions
    case Load(e) => this +: e.allExpressions
    case FunctionCall(_, args) => args.map{_.allExpressions}.flatten :+ this
    case IfExpression(condition, thenExpr, elseExpr) => ( (this +:
                    condition.allExpressions) ::: thenExpr.allExpressions :::
                    elseExpr.allExpressions)
    case _ => List(this)
  }

  def strings: List[String] = {
    for (StringLiteral(x) <- allExpressions) yield (
      x
    )
  }

  val isPrimitive: Boolean = this match {
    case Lit(_) => true
    case Var(_) => true
    case StringLiteral(_) => true
    case PointerToName(_) => true
    case _ => false
  }

  def toIntermediate(): (List[InterInstr], VarOrLit) = this match {
    case Lit(n) => (Nil, VOLLit(n))
    case BinOp(op, e1, e2) => {
      val (lhsInstr, lhsVar) = e1.toIntermediate()
      val (rhsInstr, rhsVar) = e2.toIntermediate()
      val out = Counter.getTempVarName()
      (lhsInstr ::: rhsInstr ::: List(BinOpInter(op, lhsVar, rhsVar, out)),
        VOLVar(out))
    }
    case Var(n) => (Nil, VOLVar(n))
    case Load(exp) => {
      val (rhsInstr, rhsVar) = exp.toIntermediate()
      val out = Counter.getTempVarName()
      rhsVar match {
        case VOLLit(_) => throw new Exception("illegal stuff")
        case VOLVar(n) => {
          (rhsInstr :+ LoadInter(VOLVar(n), out), VOLVar(out))
        }
      }
    }
    case FunctionCall(name, args) => {
      val arg_code = for( arg <- args ) yield arg.toIntermediate()
      val code = List.concat(for ((code, varOrLit) <- arg_code) yield code).flatten
      val vars = for ((code, varOrLit) <- arg_code) yield varOrLit
      val out = Counter.getTempVarName()
      val callInstruction = CallInter(name, vars, Some(out))

      (code :+ callInstruction, VOLVar(out))
    }
    case IfExpression(condition, thenExpr, elseExpr) => {
      val newVal = Counter.getTempVarName()
      (IfElse(condition, List(Assignment(newVal, thenExpr)),
                        List(Assignment(newVal, elseExpr))).toIntermediate, VOLVar(newVal))
    }
    case StringLiteral(value) => {
      val out = Counter.getTempVarName()
      val name = "string-"+value.hashCode()

      (List(AmpersandInter(name, out)), VOLVar(out))
    }

    case PointerToName(name) => {
      val out = Counter.getTempVarName()
      (List(AmpersandInter(name, out)), VOLVar(out))
    }
  }
}

case class Lit(n: Int) extends Expr
case class BinOp(op: BinOperator, e1: Expr, e2: Expr) extends Expr
case class Var(name: String) extends Expr
case class Load(exp: Expr) extends Expr
case class FunctionCall(name: String, args: List[Expr]) extends Expr
case class IfExpression(condition: BoolExpr, thenExpr: Expr, elseExpr: Expr) extends Expr
case class StringLiteral(value: String) extends Expr
case class PointerToName(name: String) extends Expr