package assemblyMaker

import interInstr._
import counter._
import assembly._

class Block(name: String, code: List[InterInstr]) {
  override def toString(): String = {
    "Block " + name + "\n" + code.mkString("\n")
  }

  def shittyAllocation(): List[Assembly] = throw new Exception("not implemented yet")
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

class BlockAssembler(block: Block, locals: Map[String, Int]) {
  var registers = Array.fill[Option[String]](10)(None)
  var synched = Map[String, Boolean]()
  var code = List[Assembly]()
  var position = 0;

  def assemble() = {
    for((inter, index) <- block.code.view.zipWithIndex) {
      position = index // Can I do this more elegantly?
      inter match {
        case BinOp(op, in1, in2, out) => {
          if (out != in1 && out != in2)
            deregister(out)

          var r1 = getInputRegister(in1)
          var r2 = getInputRegister(in2)

          if (out == in1 || out == in2)
            deregister(out)

          var r3 = getOutputRegister(out)
          emit(ASM_BinOp(op, r1, r2, r3))
        }
      }
    }
  }

  def deregister(name: String) = {
    if (registers contains Some(name)) {
      val pos = registers indexOf Some(name)
      if isLocal(name) && !synched(name) {
        emit_store(locals(name), pos)
        synched(name) = true
      }
      registers(pos) = None
    }
    else throw new Exception("trying to deregister variable which isn't in a register")
  }

  def isLocal(name: String): Boolean = locals contains name

  def getInputRegister(name: String): Int = {
    if (registers contains Some(name)) {
      return registers indexOf Some(name)
    }
    else {
      if (isLocal(name)) {
        var register = getRegister()
        emit_load(locals(name), register)
        registers(register) = Some(name)
        synched(name) = true
        return register
      } else {
        throw new Exception("You're referring to a temporary variable which isn't loaded."+
                                " This is probably the compiler's fault, not yours.")
      }
    }
  }

  def getOutputRegister(name: String): Int = {
    var register = getRegister()
    registers(register) = Some(name)

    // Requesting a place to save a new value to a register means that it's about
    // to be overwritten in the register, which means it will get out of sync
    // with the value on the stack, if it has a place to live on the stack (ie
    // is local).
    if (isLocal(name))
      synched(name) = false

    register
  }

  def getRegister(): Int = {
    for ((name, index) <- registers.view.zipWithIndex) {
      name match {
        None => return index
        (Some name) => {
          if isDeadOrWillBeOverwritten(name)
            return index
        }
      }
    }

    for ((name, index) <- registers.view.zipWithIndex) {
      if (isLocal(name)) {
        if (! synched(name))
          emitStore(locals(name), index)
        return index
      }
    }
    throw new Exception("Register limit exceeded!")
  }

  def emit(instr : Assembly) {
    code = code :+ instr
  }

  /// This doesn't fix the synching!
  def emitStore(name: String, index: Int) {

  }
}

abstract class VariableMemoryPosition

case class ConstantPosition(pos: Integer) extends VariableMemoryPosition
case class StackPosition(pos: Integer) extends VariableMemoryPosition

