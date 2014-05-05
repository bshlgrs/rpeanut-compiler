package assembly

import binOperator._

abstract class Register
case class GPRegister(n: Int) extends Register
case object StackPointer extends Register

abstract class Assembly

case class Instr_BinOp(op: BinOperator, r1: Register, r2: Register, out: Register)
                                                          extends Assembly
case class Instr_BPDLoad(in1: Register, displacement: Int, out: Register)
                                                          extends Assembly
case class Instr_Load(location: Register, out: Register) extends Assembly
case class Instr_LoadIm(num: Int, out: Register) extends Assembly
case class Instr_Store(in: Register, location: Register) extends Assembly
case class Instr_Jump(label: String)
case class Instr_Jumpz(in: Register, label: String)
case class Instr_Jumpnz(in: Register, label: String)
case class Instr_Jumpn(in: Register, label: String)
