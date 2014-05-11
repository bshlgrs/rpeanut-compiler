package binOperator

sealed abstract class BinOperator {
  override def toString: String = this match {
    case AddOp => "+"
    case MulOp => "*"
    case SubOp => "-"
    case DivOp => "/"
    case ModOp => "%"
  }

  def toAssembly: String = this match {
    case AddOp => "add"
    case MulOp => "mult"
    case SubOp => "sub"
    case DivOp => "div"
    case ModOp => "mod"
  }
}

case object AddOp extends BinOperator
case object MulOp extends BinOperator
case object SubOp extends BinOperator
case object DivOp extends BinOperator
case object ModOp extends BinOperator