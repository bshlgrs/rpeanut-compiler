package varOrLit

abstract class VarOrLit {
  override def toString: String = this match {
    case VOLVar(n) => n
    case VOLLit(n) => n.toString
  }

  def getVar(): String = this match {
    case VOLLit(n) => throw new Exception("gah")
    case VOLVar(n) => n
  }

  def varListIfVar(): List[String] = this match {
    case VOLLit(n) => List()
    case VOLVar(n) => List(n)
  }
}

case class VOLVar(n: String) extends VarOrLit
case class VOLLit(n: Int) extends VarOrLit
