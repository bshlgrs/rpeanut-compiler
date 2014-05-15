package assemblyMaker

import interInstr._
import counter._
import assembly._
import varOrLit._
import binOperator._
import expr._

class Block(val name: String, val code: List[InterInstr]) {
  override def toString(): String = {
    "Block " + name + "\n" + code.mkString("\n")
  }

  val varsMentioned: List[String] = code.map(x => x.allVars()).flatten.distinct

  // def shittyAllocation(): List[Assembly] = throw new Exception("not implemented yet")
}

object AssemblyMaker {
  def separateIntoBlocks(instrs: List[InterInstr]): List[Block] = {
    var output = List[Block]()
    var currentList = List[InterInstr]()
    var labelName = "starting-block-" + Counter.getCounter().toString

    for(line <- instrs) {
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

class BlockAssembler(block: Block, locals: Map[String, Int],
                                  val globals: List[String],
                                  val returnPosition: Option[Int],
                                        val localsSize: Int) {
  def initialRegisterCondition() = {
    Array.fill[Option[VarOrLit]](8)(None)
  }
  var registers = Array.fill[Option[VarOrLit]](8)(None)
  var synched = collection.mutable.Map[String, Boolean]()
  var code = List[Assembly]()
  var position = 0 : Int

  // This tells us the difference between the SP when the function was called
  // and its current value
  var stackOffset = 0

  def assemble(): List[Assembly] = {
    emit(ASM_Label(block.name))

    for((inter, index : Int) <- block.code.view.zipWithIndex) {
      // inter match {
      //   case CommentInter(_) => ()
      //   case LabelInter(_) => ()
      //   case _ => emit(ASM_Comment("### "+inter.toString()))
      // }

      if (false) {
        println("Compiling "+inter)
        println("Registers are "+ registers.mkString(","))
      }

      // emit(ASM_Comment("   # registers are "+registers.mkString(",")))

      position = index // Can I do this more elegantly? More state is generally bad...

      inter match {
        case BinOpInter(op, in1, in2, out) => {
          var r1 = getInputRegister(in1)
          var r2 = getInputRegister(in2)

          var r3 = getOutputRegister(out)
          emit(ASM_BinOp(op, r1, r2, r3))
        }
        // target = *source
        case LoadInter(source, target) => {
          var r1 = getInputRegister(source)
          var r2 = getOutputRegister(target)
          emit(ASM_Load(r1, r2))
        }
        case StoreInter(source, target) => {
          var r1 = getInputRegister(source)
          var r2 = getInputRegister(target)
          emit(ASM_Store(r1, r2))
        }
        case CopyInter(source, target) => {
          source match {
            case VOLLit(n) => {
              var r2 = getOutputRegister(target)
              emit(ASM_LoadIm(n, r2))
            }
            case VOLVar(n) => {
              var r1 = getInputRegister(source)
              var r2 = getOutputRegister(target)
              // the next line is very unrealistic for a real microprocessor, but yolo.
              //         (mainly because I cbf implementing the copy instruction)
              emit(ASM_BinOp(AddOp, ZeroRegister, r1, r2))
            }
          }
        }
        case LabelInter(label) => emit(ASM_Label(label))
        case JumpInter(label) => {
          saveUnsynchedVariables()
          emit(ASM_Jump(label))
        }
        case JumpZInter(label, variable) => {
          saveUnsynchedVariables()

          // todo: static checks to determine if this is constant 0.
          // Maybe I should do that in the AST -> Intermediate conversion instead...
          var r1 = getInputRegister(variable)
          emit(ASM_JumpZ(r1, label))

        }
        case JumpNZInter(label, variable) => {
          saveUnsynchedVariables()
          var r1 = getInputRegister(variable)
          emit(ASM_JumpNZ(r1, label))
        }
        case JumpNInter(label, variable) => {
          saveUnsynchedVariables()
          var r1 = getInputRegister(variable)
          emit(ASM_JumpN(r1, label))
        }
        case AmpersandInter(name, target) => {
          if (locals contains name) {
            var r1 = getInputRegister(VOLLit(locals(name)))
            var out = getOutputRegister(target)

            emit(ASM_BinOp(AddOp, r1, StackPointer, out))
            emit(ASM_Load(out, out))
          } else {
            var out = getOutputRegister(target)
            emit(ASM_LoadGlobal("#"+name, out))
          }
        }
        case CallInter(name, args, returnValue) => {
          // Add the size of locals to the stack pointer.
          if (localsSize != 0)
            emit(ASM_BinOp(AddOp, getInputRegister(VOLLit(localsSize)), StackPointer, StackPointer))
          stackOffset += localsSize

          // Save temporary variables.
          var savedTempVariables = List[String]()

          for ((register, index) <- registers.view.zipWithIndex ) {
            register match {
              case Some(VOLVar(name)) => {
                if (!isUnneeded(VOLVar(name)) && !isPermanent(name)) {
                  emit(ASM_Push(GPRegister(index)))
                  stackOffset += 1
                  savedTempVariables = name +: savedTempVariables
                }
              }
              case _ => ()
            }
          }

          // Push a space on the stack for the return value.
          emit(ASM_Push(ZeroRegister))
          stackOffset += 1

          // Push all the arguments on the stack.
          for (arg <- args) {
            emit(ASM_Push(getInputRegister(arg)))
            stackOffset += 1
            deregister(arg)
          }

          saveUnsynchedVariables()

          emit(ASM_Call(name))

          registers = Array.fill[Option[VarOrLit]](8)(None)

          // Pop all the arguments from the stack.
          if (args.length != 0)
            emit(ASM_BinOp(SubOp, StackPointer, getInputRegister(VOLLit(args.length)), StackPointer))
          stackOffset -= args.length



          // Pop return value from the stack.
          returnValue match {
            case None => emit(ASM_Pop(ZeroRegister))
            case Some(name) => {
              var r1 = getOutputRegister(name)
              emit(ASM_Pop(r1))
            }
          }
          stackOffset -= 1

          // Restore temporary registers
          for (temp <- savedTempVariables) {
            val r = getOutputRegister(temp)
            emit(ASM_Pop(r))
            stackOffset -= 1
          }

          // Reduce the SP by the size of locals.
          if (localsSize != 0)
            emit(ASM_BinOp(SubOp, StackPointer, getInputRegister(VOLLit(localsSize)), StackPointer))
          stackOffset -= localsSize

          assert(stackOffset == 0)
        }
        case PushInter => emit(ASM_Push(ZeroRegister))
        case PopInter(target) => {
          var r = getOutputRegister(target)
          emit(ASM_Pop(r))
        }
        case CommentInter(comment) => emit(ASM_Comment(comment))
        case ReturnWithValInter(x) => {
          emit(ASM_BPDStore(getInputRegister(x),
                  returnPosition match {case Some(x) => x; case _ => throw new Exception("unimplemented")},
                  StackPointer))
          emit(ASM_Return)
        }
        case ReturnVoidInter => emit(ASM_Return)
      }
    }

    saveUnsynchedVariables()

    code
  }

  // This takes a thing out of the registers and stores it if necessary.
  def deregister(vol: VarOrLit) = vol match {
    case VOLVar(name) => {
      if (registers contains Some(name)) {
        val pos = registers indexOf Some(name)
        if (isPermanent(name) && !synched(name)) {
          emitStore(name, pos)
          synched(name) = true
        }
        // The next line is commented out because I think this works fine without it.
        // And commenting it out should give me some efficiency gains.

        // registers(pos) = None
      }
    }
    case VOLLit(num) => ()
  }

  def currentLine() : InterInstr = block.code(position)

  def isPermanent(name: String): Boolean = (locals contains name) || (globals contains name)

  def getPosition(name: String): Int = locals(name) - stackOffset

  def getInputRegister(vol: VarOrLit): Register = {
    if (registers contains Some(vol)) {
      return GPRegister(registers indexOf Some(vol))
    }
    else {
      vol match {
        case VOLVar(name) => {
          if (isPermanent(name)) {
            var register = getRegister()
            emitLoad(vol, register)
            registers(register) = Some(vol)
            synched(name) = true
            return GPRegister(register)
          } else {
            throw new Exception("You're referring to a temporary variable which isn't loaded."+
                                " This is probably the compiler's fault, not yours.\n"+
                                "The variable is "+name +", and the current allocation is:\n"+
                                registers.mkString(",")+"\n\n"+
                                "Here's the block that I was compiling: \n"+block.toString +
                                "\n\nI was up to line "+position+"\nLocals are "+locals.toString+
                                "\nGlobals are "+globals.mkString(", "))
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
              registers(register) = Some(vol)
              return GPRegister(register)
            }
          }

        }
      }
    }
  }

  def getOutputRegister(name: String): Register = {
    synched(name) = false
    if (registers contains Some(VOLVar(name))) {
      return GPRegister(registers indexOf Some(VOLVar(name)))
    } else {
      var register = getRegister()
      registers(register) = Some(VOLVar(name))

    // Requesting a place to save a new value to a register means that it's about
    // to be overwritten in the register, which means it will get out of sync
    // with the value on the stack, if it has a place to live on the stack (ie
    // is local).

      GPRegister(register)
    }
  }

  def getRegister(): Int = {
    // TODO: This function can be rewritten to be much cleverer and generate
    // more efficient code.

    // vol : ValOrLit
    for ((option_vol, index) <- registers.view.zipWithIndex) {
      option_vol match {
        case None => return index
        case Some(vol) => {
          if (isUnneeded(vol)) {
            vol match {
              case VOLVar(name) => {
                if (isPermanent(name) && ! synched(name))
                  emitStore(name, index)
                return index
              }
              case VOLLit(_) => {
                return index
              }
            }
          }
        }
      }
    }

    for ((option_vol, index) <- registers.view.zipWithIndex) {
      option_vol match {
        case Some(VOLVar(name)) => {
          if (isPermanent(name)) {
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
        case _ => throw new Exception("unimplemented") // this should never happen, I just want the compiler to shut up
      }
    }

    throw new Exception("Register limit exceeded!")
  }

  def emit(instr : Assembly) {
    code = code :+ instr
  }

  def emitStore(name: String, index: Int) {
    if (locals contains name) {
      emit(ASM_BPDStore(GPRegister(index), getPosition(name), StackPointer))
      synched(name) = true
    }
    else if (globals contains name) {
      emit(ASM_StoreGlobal(name, GPRegister(index)))
    }
    else {
      throw new Exception("Tried to emit store for "+name+", but it's not a local.\n"+
                        "Locals are "+locals)
    }
  }

  def emitLoad(vol: VarOrLit, index: Int) {
    vol match {
      case VOLVar(name) => {
        if (locals contains name) {
          emit(ASM_BPDLoad(StackPointer, getPosition(name), GPRegister(index)))
        }
        else if (globals contains name) {
          emit(ASM_LoadGlobal(name, GPRegister(index)))
        }
        else {
          throw new Exception("Tried to emit load for "+name+", but it's not a local.\n"+
                            "Locals are "+locals)
        }
      }
      case VOLLit(num) => {
        emit(ASM_LoadIm(num, GPRegister(index)))
      }
    }
  }

  def isUnneeded(vol: VarOrLit): Boolean = {
    vol match {
       // This is a shitty temporary solution which will sometimes break everything!
      case VOLLit(x) => false //currentLine().inputVars contains vol
      case VOLVar(name) => {
        if (block.code(position).inputVars contains(VOLVar(name)))
          return false

        for(line <- block.code.drop(position + 1)) {
          if (line.inputVars contains VOLVar(name))
            return false
          else if (line.outputVars contains name)
            return true
        }
        return true
      }
    }
  }

  def saveUnsynchedVariables() = {
    for ((optionVol, index) <- registers.view.zipWithIndex) optionVol match {
      case Some(VOLVar(name)) => {
        if (isPermanent(name) && !synched(name)) {
          emitStore(name, index)
          synched(name) = true
        }
      }
      case _ => ()
    }
  }
}

// abstract class VariableMemoryPosition

// case class ConstantPosition(pos: Integer) extends VariableMemoryPosition
// case class StackPosition(pos: Integer) extends VariableMemoryPosition

