package binOperator

abstract class BinOperator {
  override def toString: String = this match {
    case AddOp => "+"
    case MulOp => "*"
    case SubOp => "-"
  }

  def toAssembly: String = this match {
    case AddOp => "add"
    case MulOp => "mul"
    case SubOp => "sub"
  }
}

case object AddOp extends BinOperator
case object MulOp extends BinOperator
case object SubOp extends BinOperator