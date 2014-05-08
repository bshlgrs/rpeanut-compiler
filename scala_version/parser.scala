import scala.util.parsing.combinator._
import expr._
import binOperator._
import boolExpr._

class JSON extends JavaTokenParsers {

  def obj: Parser[Map[String, Any]] =
    "{"~> repsep(member, ",") <~"}" ^^ (Map() ++ _)

  def arr: Parser[List[Any]] =
    "["~> repsep(value, ",") <~"]"

  def member: Parser[(String, Any)] =
    stringLiteral~":"~value ^^
      { case name~":"~value => (name, value) }

  def value: Parser[Any] = (
    obj
  | arr
  | stringLiteral
  | floatingPointNumber ^^ (_.toDouble)
  | "null"  ^^ (x => null)
  | "true"  ^^ (x => true)
  | "false" ^^ (x => false)
  )
}

class CParser extends JavaTokenParsers {
  def expr: Parser[Expr] = ("("~expr~op~expr~")" ^^ {case _~e1~op~e2~_ => BinOp(op, e1, e2)}
                           | ident~"["~expr~"]" ^^ {case i~_~e~_ => Load(BinOp(AddOp, Var(i), e))}
                           | boolExpr~"?"~expr~":"~expr ^^ { case i~_~t~_~e => IfExpression(i,t,e) }
                           | "*"~expr ^^ {case _~e1 => Load(e1) }
                           | ident ^^ { Var(_) }
                           | wholeNumber ^^ (x => Lit(x.toInt) )
                           )

  def boolExpr: Parser[BoolExpr] = "("~expr~boolOp~expr~")" ^^ {case _~e1~b~e2~_ => BoolBinOp(b,e1,e2)}

  def op: Parser[BinOperator] = ("+" ^^ (x => AddOp)
                                |"-" ^^ (x => SubOp)
                                |"*" ^^ (x => MulOp) )

  def boolOp: Parser[BoolBinOperator] = ( "==" ^^ (x => Equals)
                                        | ">"  ^^ (x => GreaterThan))

  // val processTerm = (x => case (start~rest) => rest.foldList(start)
  //         ((e1,tuple) => tuple match {
  //           case ("*", e2) => BinOp(MulOp, e1, e2)
  //           case ("/", e2) => BinOp(DivOp, e1, e2)
  //         }))
}

object ParseExpr extends CParser {
  def main(args: Array[String]) {
    println("input : "+ args(0))
    println(parseAll(expr, args(0)))
  }
}