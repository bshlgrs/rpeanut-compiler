package assembly

import binOperator._

abstract class Register {
  override def toString = this match {
    case GPRegister(n) => "R" + n.toString
    case StackPointer => "SP"
    case OneRegister => "ONE"
    case MOneRegister => "MONE"
    case ZeroRegister => "ZERO"
  }
}
case class GPRegister(n: Int) extends Register
case object StackPointer extends Register
case object OneRegister extends Register
case object MOneRegister extends Register
case object ZeroRegister extends Register

abstract class Assembly {
  val comment = "" : String;

  override def toString = this match {
    case ASM_Label(_) => toStringWithoutComment
    case ASM_Comment(_) => toStringWithoutComment + comment
    case _ => "\t" + toStringWithoutComment + "; " + comment
  }

  def toStringWithoutComment = this match {
    case ASM_BinOp(op, r1, r2, out) => s"${op.toAssembly} $r1 $r2 $out"
    case ASM_BPDLoad(source, displacement, target) => s"load $source #$displacement $target"
    case ASM_Load(location, out) => s"load $location $out"
    case ASM_LoadIm(num, out) => s"load #$num $out"
    case ASM_Store(in, location) => s"store $in $location"
    case ASM_BPDStore(value, displacement, position) => s"store $value #$displacement $position"
    case ASM_Jump(label) => s"jump $label"
    case ASM_JumpZ(in, label) => s"jumpz $in $label"
    case ASM_JumpNZ(in, label) => s"jumpnz $in $label"
    case ASM_JumpN(in, label) => s"jumpn $in $label"
    case ASM_Label(label) => s"$label:"
    case ASM_Push(in) => s"push $in"
    case ASM_Pop(out) => s"pop $out"
    case ASM_Call(label) => s"call $label"
    case ASM_Return => "return"
    case ASM_Comment(main_comment) => s"; $main_comment"
    case _ => "dafsdsa"
  }
}

case class ASM_BinOp(op: BinOperator, r1: Register, r2: Register, out: Register)
                                                          extends Assembly
case class ASM_BPDLoad(source: Register, displacement: Int, target: Register)
                                                          extends Assembly
case class ASM_Load(location: Register, out: Register) extends Assembly
case class ASM_LoadIm(num: Int, out: Register) extends Assembly
case class ASM_Store(in: Register, location: Register) extends Assembly
case class ASM_BPDStore(value: Register, displacement: Int, position: Register) extends Assembly
case class ASM_Jump(label: String) extends Assembly
case class ASM_JumpZ(in: Register, label: String) extends Assembly
case class ASM_JumpNZ(in: Register, label: String) extends Assembly
case class ASM_JumpN(in: Register, label: String) extends Assembly
case class ASM_Label(label: String) extends Assembly
case class ASM_Push(reg: Register) extends Assembly
case class ASM_Pop(reg: Register) extends Assembly
case class ASM_Call(label: String) extends Assembly
case object ASM_Return extends Assembly
case class ASM_Comment(main_comment: String) extends Assembly