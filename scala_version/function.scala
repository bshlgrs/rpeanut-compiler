package function

import statement._
import interInstr._

class Function(name: String, params: List[String], body: List[Statement]) {
  override def toString() = ("def " + name + "(" + params.mkString(", ") +
              ") {\n" + body.mkString("\n") + "\n}")

  def toIntermediate: List[InterInstr] = {
    var out = List[InterInstr]()
    for (line <- body) {
      out = out ::: line.toIntermediate
    }
    out
  }
}