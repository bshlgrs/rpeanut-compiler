package binOperator

abstract class BinOperator {
  override def toString: String = this match {
    case AddOp => "+"
    case MulOp => "*"
    case SubOp => "-"
  }
}

case object AddOp extends BinOperator
case object MulOp extends BinOperator
case object SubOp extends BinOperator