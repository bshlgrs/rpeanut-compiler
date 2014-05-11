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
                           | "true" ^^ (x => Lit(1))
                           | "false" ^^ (x => Lit(0))
                           )

  def boolExpr: Parser[BoolExpr] = ("("~expr~"=="~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(Equals,e1,e2)}
                                  | "("~expr~">"~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(GreaterThan,e1,e2)}
                                  | "("~expr~"<"~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(GreaterThan,e2,e1)}
                                  )

  def op: Parser[BinOperator] = ("+" ^^ (x => AddOp)
                                |"-" ^^ (x => SubOp)
                                |"*" ^^ (x => MulOp)
                                |"/" ^^ (x => DivOp)
                                |"%" ^^ (x => ModOp))

  def stat: Parser[statement.Statement] = ("return"~";" ^^ (_ => Return(None))
                                        | "return"~expr~";" ^^ {case _~e~_ => Return(Some(e))}
                                        | atomicStatement<~";"
                                        | "if"~ boolExpr~block~"else"~block
                                          ^^ {case _~b~i~_~e => IfElse(b,i,e) }
                                        | "if"~ boolExpr ~ block ^^ {case _~b~i => IfElse(b,i,Nil)}
                                        | "while"~ boolExpr ~ block ^^ {case _~b~i => While(b,i)}
                                        | "*"~expr~ "=" ~ expr~";" ^^ {case _~a~_~b~_ => IndirectAssignment(a,b)}
                                        | ident~"["~expr~"]"~"="~expr~";" ^^
                                            {case a~_~b~_~_~e~_ => IndirectAssignment(BinOp(AddOp,Var(a),b),e)}
                                        )

  def atomicStatement : Parser[statement.Statement] = (
        ident~"="~expr ^^ {case i~_~e => Assignment(i,e)}
      | ident~op~"="~expr ^^ {case i~o~_~e => Assignment(i,BinOp(o,Var(i),e))}
      | ident<~"++" ^^ {case x => Assignment(x,BinOp(AddOp,Var(x),Lit(1)))}
      | ident<~"--" ^^ {case x => Assignment(x,BinOp(SubOp,Var(x),Lit(1)))}
      )

  def block: Parser[List[statement.Statement]] = ( "{"~> rep(stat) <~"}"
                                       | stat ^^ { List(_) })
  def func: Parser[function.Function] = (
            ("def")~ ident ~ "(" ~ repsep(ident,",") ~ ")" ~ block
            ^^ {case _~name~_~args~_~bl => new function.Function(name, args, bl)})

  def program: Parser[List[function.Function]] = rep(func)
}

object Compile extends CParser {
  def main(args: Array[String]) {
    parseAll(program, io.Source.fromFile(args(0)).mkString) match {
      case Success(result, _) => {

        for (function <- result) {
          if (function.name == "main")
            println("0x0100:")
          println(function.toAssembly.mkString("\n"))
        }

        println(io.Source.fromFile("./examples/stdio.h").mkString)

      }
      case x => println("Parse error"); println(x)
    }
  }
}