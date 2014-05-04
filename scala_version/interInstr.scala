package interInstr

import varOrLit._
import binOperator._

abstract class InterInstr {
  override def toString: String = this match {
    case BinOpInter(op, in1, in2, out) => s"\t$out\t= ${in1.toString} ${op.toString} ${in2.toString}"
    case LoadInter(source, target) => s"\t${target.toString}\t= *${source.toString}"
    case StoreInter(source, target) => s"\t*${target.toString}\t= ${source.toString}"
    case CopyInter(source, target) => s"\t${target.toString}\t= ${source.toString}"
    case LabelInter(label) => label+":"
    case JumpInter(label) => "\tjump "+label
    case JumpZInter(label, sourceVar) => "\tjump "+label+" if 0 == "+sourceVar
    case JumpNZInter(label, sourceVar) => "\tjump "+label+" if 0 != "+sourceVar
    case JumpNInter(label, sourceVar) => "\tjump "+label+" if 0 > "+sourceVar
    case CommentInter(comment) => "// " + comment

  }
}

case class BinOpInter(op: BinOperator, in1: VarOrLit, in2: VarOrLit, out: String)
                                            extends InterInstr
case class LoadInter(sourceVar: String, targetVar: String) extends InterInstr
case class StoreInter(sourceVar: String, targetVar: String) extends InterInstr
case class CopyInter(sourceVar: VarOrLit, targetVar: String) extends InterInstr
case class LabelInter(label: String) extends InterInstr
case class JumpInter(label: String) extends InterInstr
case class JumpZInter(label: String, sourceVar: VarOrLit) extends InterInstr
case class JumpNInter(label: String, sourceVar: VarOrLit) extends InterInstr
case class JumpNZInter(label: String, sourceVar: VarOrLit) extends InterInstr
case class CommentInter(comment: String) extends InterInstr

