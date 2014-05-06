package interInstr

import varOrLit._
import binOperator._

abstract class InterInstr {
  override def toString: String = this match {
    case BinOpInter(op, in1, in2, target) => s"\t$target\t= ${in1.toString} ${op.toString} ${in2.toString}"
    case LoadInter(source, target) => s"\t${target.toString}\t= *${source.toString}"
    case StoreInter(source, target) => s"\t*${target.toString}\t= ${source.toString}"
    case CopyInter(source, target) => s"\t${target.toString}\t= ${source.toString}"
    case LabelInter(label) => label+":"
    case JumpInter(label) => "\tjump "+label
    case JumpZInter(label, sourceVar) => "\tjump "+label+" if 0 == "+sourceVar
    case JumpNZInter(label, sourceVar) => "\tjump "+label+" if 0 != "+sourceVar
    case JumpNInter(label, sourceVar) => "\tjump "+label+" if 0 > "+sourceVar
    case CallWithValInter(name, args, targetVar) => {
                    targetVar + " = " + name + args.mkString(", ") + ")"
    }
    case CallVoidInter(name, args) => name + args.mkString(", ") + ")"
    case CommentInter(comment) => "// " + comment
    case _ => throw new Exception("unimplemented thing! oh god!")
  }

  def changeTarget(oldTarget: String, newTarget: String):InterInstr = this match {
    case BinOpInter(op, in1, in2, target) => {
        if (target == oldTarget)
            BinOpInter(op, in1, in2, newTarget)
        else
            this
    }
    case LoadInter(source, target) => {
        if (target == oldTarget)
            LoadInter(source, newTarget)
        else
            this
        }
    case CopyInter(source, target) => {
        if (target == oldTarget)
            CopyInter(source, newTarget)
        else
            this
        }
    case _ => this
  }
}

case class BinOpInter(op: BinOperator, in1: VarOrLit, in2: VarOrLit, targetVar: String)
                                                extends InterInstr
case class LoadInter(sourceVar: String, targetVar: String) extends InterInstr
case class StoreInter(sourceVar: String, targetVar: String) extends InterInstr
case class CopyInter(sourceVar: VarOrLit, targetVar: String) extends InterInstr
case class LabelInter(label: String) extends InterInstr
case class JumpInter(label: String) extends InterInstr
case class JumpZInter(label: String, sourceVar: VarOrLit) extends InterInstr
case class JumpNInter(label: String, sourceVar: VarOrLit) extends InterInstr
case class JumpNZInter(label: String, sourceVar: VarOrLit) extends InterInstr
case class CallWithValInter(name: String, args: List[VarOrLit], targetVar: String)
                                                extends InterInstr
case class CallVoidInter(name: String, args: List[VarOrLit]) extends InterInstr
case class CommentInter(comment: String) extends InterInstr
case class ReturnWithValInter(value: VarOrLit) extends InterInstr
case object ReturnVoidInter extends InterInstr