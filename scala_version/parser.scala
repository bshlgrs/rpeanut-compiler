import scala.util.parsing.combinator._
import statement._
import expr._
import binOperator._
import boolExpr._
import function._

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
                           // | ident~op~expr ^^ {case i~o~e => BinOp(o, Var(i), e) }
                           | ident~"["~expr~"]" ^^ {case i~_~e~_ => Load(BinOp(AddOp, Var(i), e))}
                           | boolExpr~"?"~expr~":"~expr ^^ { case i~_~t~_~e => IfExpression(i,t,e) }
                           | "*"~expr ^^ {case _~e1 => Load(e1) }
                           | (ident ~ "("~ repsep(expr, ",")~")") ^^ { case x~_~a~_ => FunctionCall(x,a) }
                           | ident ^^ { Var(_) }
                           | wholeNumber ^^ (x => Lit(x.toInt))
                           )

  def boolExpr: Parser[BoolExpr] = "("~expr~boolOp~expr~")" ^^ {case _~e1~b~e2~_ => BoolBinOp(b,e1,e2)}

  def op: Parser[BinOperator] = ("+" ^^ (x => AddOp)
                                |"-" ^^ (x => SubOp)
                                |"*" ^^ (x => MulOp) )

  def boolOp: Parser[BoolBinOperator] = ( "==" ^^ (x => Equals)
                                        | ">"  ^^ (x => GreaterThan))

  def stat: Parser[statement.Statement] = ("return"~";" ^^ (_ => Return(None))
                                        | "return"~expr~";" ^^ {case _~e~_ => Return(Some(e))}
                                        | ident~"="~expr<~";" ^^ {case i~_~e => Assignment(i,e)}
                                        | "if"~ boolExpr~block~"else"~block
                                          ^^ {case _~b~i~_~e => IfElse(b,i,e) }
                                        | "if"~ boolExpr ~ block ^^ {case _~b~i => IfElse(b,i,Nil)}
                                        | "while"~ boolExpr ~ block ^^ {case _~b~i => While(b,i)}
                                        | "*"~expr~ "=" ~ expr~";" ^^ {case _~a~_~b~_ => IndirectAssignment(a,b)}
                                        | ident~"["~expr~"]"~"="~expr~";" ^^
                                            {case a~_~b~_~_~e~_ => IndirectAssignment(BinOp(AddOp,Var(a),b),e)}
                                        )
  def block: Parser[List[statement.Statement]] = ( "{"~> rep(stat) <~"}"
                                       | stat ^^ { List(_) })
  def func: Parser[function.Function] = (
            ("def")~ ident ~ "(" ~ repsep(ident,",") ~ ")" ~ block
            ^^ {case _~name~_~args~_~bl => new function.Function(name, args, bl)})
}

object Compile extends CParser {
  def main(args: Array[String]) {
    parseAll(func, args(0)) match {
      case Success(result, _) => {
        println(result.blocks.mkString("\n"))
        println(result.toAssembly.mkString("\n"))
      }
      case x => println("Parse error"); println(x)
    }
  }
}