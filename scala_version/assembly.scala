package assembly

import binOperator._

abstract class Register
case class GPRegister(n: Int) extends Register
case object StackPointer extends Register

abstract class Assembly

case class ASM_BinOp(op: BinOperator, r1: Register, r2: Register, out: Register)
                                                          extends Assembly
case class ASM_BPDLoad(in1: Register, displacement: Int, out: Register)
                                                          extends Assembly
case class ASM_Load(location: Register, out: Register) extends Assembly
case class ASM_LoadIm(num: Int, out: Register) extends Assembly
case class ASM_Store(in: Register, location: Register) extends Assembly
case class ASM_Jump(label: String)
case class ASM_Jumpz(in: Register, label: String)
case class ASM_Jumpnz(in: Register, label: String)
case class ASM_Jumpn(in: Register, label: String)
