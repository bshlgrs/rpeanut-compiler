package statement

import function._
import expr._
import binOperator._
import boolExpr._
import varOrLit._
import interInstr._
import counter.Counter
import statement._
import module._

sealed abstract class Statement {
  override def toString: String = this match {
    case Assignment(name, rhs) => name + " = " + rhs.toString + ";"
    case VoidFunctionCall(name, args) => name + "(" + args.mkString(", ") + ")"
    case IndirectAssignment(lhs, rhs) => "*" + lhs.toString + " = " + rhs.toString+";"
    case IfElse(condition, thenBlock, elseBlock) => ("if (" + condition.toString +
                ") { \n"+thenBlock.mkString("\n") + "} \nelse {\n" +
                        elseBlock.mkString("\n") + "\n}")
    case While(condition, block) => ("while (" + condition.toString + ") {\n" +
                        block.mkString("\n") + "\n}")
    case ForLoop(a,b,c,d) => "for ("+a+"; "+b+"; "+c+") {\n"+d.mkString("\n")+"}"
    case Return(thing) => thing match {
      case Some(expr) => "return " + expr.toString + ";"
      case None => "return;"
    }
    case ArrayAssignment(name, stuff) => (name + " = { " + stuff.mkString(", ")
                                              + " };")
  }
  def toIntermediate(module: Module): List[InterInstr]

  def toIntermediateWithFixed(fixed: List[String]): (List[InterInstr], List[InterInstr]) = {
    throw new Exception("not implemented")
  }

  def allExpressions: List[Expr] =  this match {
    case Assignment(_,rhs) => List(rhs)
    case VoidFunctionCall(_,args) => args
    case IndirectAssignment(lhs,rhs) => List(lhs, rhs)
    case IfElse(condition, thenBlock, elseBlock) => (condition.allExpressions
                            ::: thenBlock.map{_.allExpressions}.flatten
                            ::: elseBlock.map{_.allExpressions}.flatten)
    case While(condition, block) => (condition.allExpressions :::
                                     block.map{_.allExpressions}.flatten)
    case ForLoop(a,b,c,d) => (
      a.allExpressions ::: b.allExpressions ::: c.allExpressions :::
                  d.map{_.allExpressions}.flatten)
    case Return(Some(x)) => List(x)
    case Return(None) => List()
    case ArrayAssignment(_, stuff) => stuff
  }

  def variablesModified: List[String] = this match {
    case Assignment(x,_) => List(x)
    case IfElse(_, t, e) => ( t.map{_.variablesModified} ::: t.map{_.variablesModified}
                                    ).flatten
    case While(_, block) => block.map{_.variablesModified}.flatten
    case ForLoop(a,b,c,d) => (a +: c +: d).map{_.variablesModified}.flatten
    case _ => Nil
  }
}

case class Assignment(name: String, rhs: Expr) extends Statement {
  override def toIntermediate(module: Module): List[InterInstr] = {
    var (exprInters, resultPlace) = rhs.toIntermediate(module)
    if (exprInters.length == 0)
      return List(CopyInter(resultPlace, name))

    resultPlace match {
      case VOLVar(x) => {
        var changedInters = exprInters.map {_.changeTarget(x, name)}
        CommentInter(this.toString()) +: changedInters
      }
      case VOLLit(n) => {
        List(CommentInter(this.toString()), CopyInter(VOLLit(n), name))
      }
    }
  }

  override def toIntermediateWithFixed(fixed:List[String]): (List[InterInstr], List[InterInstr]) = {
    var (exprInters, exprFixed, resultPlace) = rhs.toIntermediateWithFixed(fixed)
    if (exprInters.length == 0)
      return (List(CopyInter(resultPlace, name)), exprFixed)

    resultPlace match {
      case VOLVar(x) => {
        var changedInters = exprInters.map {_.changeTarget(x, name)}
        // maybe I need to change fixed here too?
        (CommentInter(this.toString()) +: changedInters, exprFixed)
      }
      case VOLLit(n) => {
        (List(CommentInter(this.toString()), CopyInter(VOLLit(n), name)), Nil)
      }
    }
  }
}

case class IndirectAssignment(lhs: Expr, rhs: Expr) extends Statement {
  override def toIntermediate(module: Module): List[InterInstr] = {
    val (lhsInstr, lhsVar) = lhs.toIntermediate(module)
    val (rhsInstr, rhsVar) = rhs.toIntermediate(module)
    CommentInter(this.toString()) +: (lhsInstr ::: rhsInstr) :+
                                    StoreInter(rhsVar, lhsVar)
  }
}

case class IfElse(condition: BoolExpr,
                  thenBlock: List[Statement],
                  elseBlock: List[Statement]) extends Statement {
  override def toIntermediate(module: Module): List[InterInstr] = {
    val counter = Counter.getCounter();
    val conditionCode = condition.toIntermediate("then-"+counter.toString,
                                            "else-"+counter.toString, module) : List[InterInstr]
    val thenCode = StatementHelper.statementsToIntermediate(thenBlock, module)
    val elseCode = StatementHelper.statementsToIntermediate(elseBlock, module)

    return (List(CommentInter("if (" + condition.toString + ")")) :::
            conditionCode :::
            List(CommentInter("{")) :::
            List(LabelInter("then-"+counter.toString)) :::
            (thenCode :+
            JumpInter("end-"+counter.toString) :+
            CommentInter("} else {") :+
            LabelInter("else-"+counter.toString)) :::
            (elseCode :+
            LabelInter("end-"+counter.toString)) :::
            List(CommentInter("}")))
  }
}

case class While(condition: BoolExpr, block: List[Statement]) extends Statement {
  override def toIntermediate(module: Module): List[InterInstr] = {
    val counter = Counter.getCounter();
    val conditionCode = condition.toIntermediate("while-loop-" + counter.toString + "-body",
                                          "endWhile-"+counter.toString, module) : List[InterInstr]

    val blockCode = (for (line <- block) yield line.toIntermediate(module)).flatten

    (List(CommentInter("while ("+ condition.toString + ") {"),
         LabelInter("while-" + counter.toString)) :::
      (conditionCode :+
             LabelInter("while-loop-" + counter.toString + "-body")) :::
      blockCode :::
      List(JumpInter("while-" + counter.toString),
        CommentInter("}"),
        LabelInter("endWhile-"+counter.toString)))
  }
}

case class Return(value: Option[Expr]) extends Statement {
  override def toIntermediate(module: Module): List[InterInstr] = value match {
    case None => {
      List(CommentInter(this.toString), ReturnVoidInter)
    }
    case Some(expr) => {
      val (exprCode, returnPlace) = expr.toIntermediate(module)
      (List(CommentInter(this.toString)) ::: exprCode ::: List(ReturnWithValInter(returnPlace)))
    }
  }
}

case class VoidFunctionCall(name: String, args: List[Expr]) extends Statement {
  override def toIntermediate(module: Module): List[InterInstr] = {
      val arg_code = for( arg <- args ) yield arg.toIntermediate(module)
      val code = List.concat(for ((code, varOrLit) <- arg_code) yield code).flatten
      val vars = for ((code, varOrLit) <- arg_code) yield varOrLit
      val callInstruction = CallInter(name, vars, None)

      CommentInter(this.toString()) +: code :+ callInstruction
  }
}

case class ForLoop(instr: Statement, cond: BoolExpr, iterator: Statement,
                          block: List[Statement]) extends Statement {
  override def toIntermediate(module: Module) = {
    instr.toIntermediate(module) ::: While(cond, block :+ iterator).toIntermediate(module)
  }
}

case class ArrayAssignment(name: String, exprs: List[Expr]) extends Statement {
  override def toIntermediate(module: Module): List[InterInstr] = {
    if (exprs.length == 0) {
      return Nil
    } else if (exprs.length == 1) {
      return new IndirectAssignment(Var(name), exprs(0)).toIntermediate(module)
    } else {
      val counter = name+"-"+Counter.getCounter()
      val start = CopyInter(VOLVar(name), counter)

      val blockCode = (for {
        (expr, index) <- exprs.view.zipWithIndex
        val (instrs, resultPlace) = expr.toIntermediate(module)
        } yield (instrs ::: List(StoreInter(resultPlace, VOLVar(counter))) ::: (
                  List(BinOpInter(
                      AddOp, VOLVar(counter), VOLLit(1), counter))))
        ).toList.flatten // forget the last one

      CommentInter(this.toString()) +: (start +: blockCode)
    }
  }
}

object StatementHelper {
  def statementsToIntermediate(block: List[Statement], module: Module): List[InterInstr] = {
    var out = List[InterInstr]()
    for(statement <- block) {
      out = out ::: statement.toIntermediate(module)
    }
    out
  }
}