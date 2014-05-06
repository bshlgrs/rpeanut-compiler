package assemblyMaker

import interInstr._
import counter._
import assembly._
import varOrLit._

class Block(name: String, code: List[InterInstr]) {
  override def toString(): String = {
    "Block " + name + "\n" + code.mkString("\n")
  }

  // def shittyAllocation(): List[Assembly] = throw new Exception("not implemented yet")
}

object AssemblyMaker {
  def separateIntoBlocks(instrs: List[InterInstr]): List[Block] = {
    var output = List[Block]()
    var currentList = List[InterInstr]()
    var labelName = "starting-block-" + Counter.getCounter().toString

    for( line <- instrs ) {
      line match {
        case LabelInter(name) => {
          output = output :+ new Block(labelName, currentList)
          labelName = name
          currentList = List()
        }
        case _ => {
          currentList = currentList :+ line
        }
      }
    }

    output :+ new Block(labelName, currentList)
  }
}

class BlockAssembler(locals: Map[String, Int]) {
  var registers = Array.fill[Option[VarOrLit]](10)(None)
  var synched = collection.mutable.Map[String, Boolean]()
  var code = List[Assembly]()
  var position = 0 : Int

  def assemble(block: Block): List[Assembly] = {
    for((inter, index : Int) <- block.code.view.zipWithIndex) {
      position = index // Can I do this more elegantly?
      inter match {
        case BinOpInter(op, in1, in2, out) => {
          if (out != in1 && out != in2)
            deregister(VOLVar(out))

          var r1 = getInputRegister(in1)
          var r2 = getInputRegister(in2)

          if (out == in1 || out == in2)
            deregister(VOLVar(out))

          var r3 = getOutputRegister(out)
          emit(ASM_BinOp(op, r1, r2, r3))
        }
      }
    }
  }

  def deregister(vol: VarOrLit) = vol match {
    case VOLVar(name) => {
      if (registers contains Some(name)) {
        val pos = registers indexOf Some(name)
        if (isLocal(name) && !synched(name)) {
          emitStore(name, pos)
        }
        registers(pos) = None
      }
      else throw new Exception("trying to deregister variable which isn't in a register")
    }
    case VOLLit(num) => ()
  }


  def isLocal(name: String): Boolean = locals contains name

  def getInputRegister(vol: VarOrLit): Register = {
    if (registers contains Some(vol)) {
      return GPRegister(registers indexOf Some(vol))
    }
    else {
      vol match {
        case VOLVar(name) => {
          if (isLocal(name)) {
            var register = getRegister()
            emitLoad(vol, register)
            registers(register) = Some(vol)
            synched(name) = true
            return GPRegister(register)
          } else {
            throw new Exception("You're referring to a temporary variable which isn't loaded."+
                                    " This is probably the compiler's fault, not yours.")
          }
        }
        case VOLLit(num) => {
          num match {
            case 0 => ZeroRegister
            case 1 => OneRegister
            case -1 => MOneRegister
            case _ => {
              var register = getRegister()
              emitLoad(vol, register)
              return GPRegister(register)
            }
          }
        }
      }
    }
  }

  def getOutputRegister(name: String): Register = {
    var register = getRegister()
    registers(register) = Some(VOLVar(name))

    // Requesting a place to save a new value to a register means that it's about
    // to be overwritten in the register, which means it will get out of sync
    // with the value on the stack, if it has a place to live on the stack (ie
    // is local).
    if (isLocal(name))
      synched(name) = false

    GPRegister(register)
  }

  def getRegister(): Int = {
    // vol : ValOrLit
    for ((option_vol, index) <- registers.view.zipWithIndex) {
      option_vol match {
        case None => return index
        case Some(vol) => {
          if (isUnneeded(vol))
            return index
        }
      }
    }

    for ((option_vol, index) <- registers.view.zipWithIndex) {
      option_vol match {
        // Check this: Can I do the two levels of matching in one thing?
        case Some(VOLVar(name)) => {
          if (isLocal(name)) {
            if (! synched(name)) {
              emitStore(name, index)
            }
            return index
          }
        }
        case Some(VOLLit(num)) => return index
        case None => { throw new Exception("There is a weird bug in the code, "+
                                          "this should never be reached...")
        }
      }
    }

    throw new Exception("Register limit exceeded!")
  }

  def emit(instr : Assembly) {
    code = code :+ instr
  }

  def emitStore(name: String, index: Int) {
    emit(ASM_BPDStore(StackPointer, locals(name), GPRegister(index)))
    synched(name) = true
  }

  def emitLoad(vol: VarOrLit, index: Int) {
    vol match {
      case VOLVar(name) => {
        emit(ASM_BPDLoad(StackPointer, locals(name), GPRegister(index)))
      }
      case VOLLit(num) => {
        emit(ASM_LoadIm(num, GPRegister(index)))
      }
    }
  }

  def isUnneeded(vol: VarOrLit): Boolean {
    // todo
  }
}

abstract class VariableMemoryPosition

case class ConstantPosition(pos: Integer) extends VariableMemoryPosition
case class StackPosition(pos: Integer) extends VariableMemoryPosition

