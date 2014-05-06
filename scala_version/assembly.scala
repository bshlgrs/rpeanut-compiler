package assembly

import binOperator._

abstract class Register {
  val comment : String;
}
case class GPRegister(n: Int) extends Register
case object StackPointer extends Register
case object OneRegister extends Register
case object MOneRegister extends Register
case object ZeroRegister extends Register

abstract class Assembly

case class ASM_BinOp(op: BinOperator, r1: Register, r2: Register, out: Register)
                                                          extends Assembly
case class ASM_BPDLoad(source: Register, displacement: Int, target: Register)
                                                          extends Assembly
case class ASM_Load(location: Register, out: Register) extends Assembly
case class ASM_LoadIm(num: Int, out: Register) extends Assembly
case class ASM_Store(in: Register, location: Register) extends Assembly
case class ASM_BPDStore(target: Register, displacement: Int, source: Register) extends Assembly
case class ASM_Jump(label: String) extends Assembly
case class ASM_Jumpz(in: Register, label: String) extends Assembly
case class ASM_Jumpnz(in: Register, label: String) extends Assembly
case class ASM_Jumpn(in: Register, label: String) extends Assembly
case class ASM_Label(label: String) extends Assembly