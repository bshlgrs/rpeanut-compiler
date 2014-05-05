package assemblyMaker

import interInstr._
import counter._
import assembly._

class Block(name: String, code: List[InterInstr], positions: Map[String, Int]) {
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

class BlockAssembler(block: Block, locals: Map[String, Int], registers: Array[String](10)) {

}

abstract class VariableMemoryPosition

case class ConstantPosition(pos: Integer) extends VariableMemoryPosition
case class StackPosition(pos: Integer) extends VariableMemoryPosition
