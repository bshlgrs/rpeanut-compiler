package binOperator

abstract class BinOperator {
  override def toString: String = this match {
    case AddOp => "+"
    case MulOp => "*"
    case SubOp => "-"
    case DivOp => "/"
  }

  def toAssembly: String = this match {
    case AddOp => "add"
    case MulOp => "mult"
    case SubOp => "sub"
    case DivOp => "div"
  }
}

case object AddOp extends BinOperator
case object MulOp extends BinOperator
case object SubOp extends BinOperator
case object DivOp extends BinOperator