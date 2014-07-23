package parser

import scala.util.parsing.combinator._
import statement._
import expr._
import binOperator._
import boolExpr._
import function._
import java.io._
import scala.sys.process._

class JSON extends JavaTokenParsers {

  def obj: Parser[Map[String, Any]] =
    "{"~> repsep(member, ",") <~"}" ^^ (Map() ++ _)

  def arr: Parser[List[Any]] =
    "["~> repsep(value, ",") <~"]"

  def member: Parser[(String, Any)] =
    stringLiteral~":"~value ^^
      { case name~":"~value => (name, value) }

  def value: Parser[Any] = ( obj
                           | arr
                           | stringLiteral
                           | floatingPointNumber ^^ (_.toDouble)
                           | "null"  ^^ (x => null)
                           | "true"  ^^ (x => true)
                           | "false" ^^ (x => false)
                           )
}

class CParser extends JavaTokenParsers {
  def expr: Parser[Expr] = (
             term~"+"~term ^^ {case e1~_~e2 => BinOp(AddOp, e1, e2)}
           | term~"-"~term ^^ {case e1~_~e2 => BinOp(SubOp, e1, e2)}
           | "("~boolExpr~")"~"?"~expr~":"~expr ^^ { case _~i~_~_~t~_~e => IfExpression(i,t,e) }
           | term )

  def term: Parser[Expr] = (
            factor~"*"~factor ^^ {case e1~_~e2 => BinOp(MulOp, e1, e2)}
          | factor~"/"~factor ^^ {case e1~_~e2 => BinOp(DivOp, e1, e2)}
          | factor~"%"~factor ^^ {case e1~_~e2 => BinOp(ModOp, e1, e2)}
          | factor
          )
  def factor: Parser[Expr] = (
             "true" ^^ (x => Lit(1))
           | "false" ^^ (x => Lit(0))
           | ident~"["~expr~"]" ^^ {case i~_~e~_ => Load(BinOp(AddOp, Var(i), e))}
           | "*"~expr ^^ {case _~e1 => Load(e1) }
           | ident ~ "("~ repsep(expr, ",")~")" ^^ { case x~_~a~_ => FunctionCall(x,a) }
           | ident ^^ { Var(_) }
           | wholeNumber ^^ (x => Lit(x.toInt))
           | """'\S'""".r ^^ (x => Lit(x.charAt(1).toInt) )
           | stringLiteral ^^ { StringLiteral(_)}
           | "("~expr~")" ^^ {case _~x~_ => x}
           | "-"~factor ^^ {case _~f => BinOp(SubOp, Lit(0), f)}
           )

  def boolExpr: Parser[BoolExpr] = (
      boolTerm~"&&"~boolTerm ^^ {case l~_~r => AndExpr(l,r)}
    | boolTerm~"||"~boolTerm ^^ {case l~_~r => OrExpr(l,r)}
    | "!"~boolExpr ^^ {case _~x => NotExpr(x)}
    | boolTerm
    )

  def boolTerm: Parser[BoolExpr] = (
        "("~expr~"=="~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(Equals,e1,e2)}
      | "("~expr~">"~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(GreaterThan,e1,e2)}
      | "("~expr~"<"~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(GreaterThan,e2,e1)}
      | "("~expr~">="~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(GreaterOrEqual,e1,e2)}
      | "("~expr~"<="~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(GreaterOrEqual,e2,e1)}
      | "("~expr~"!="~expr~")" ^^ {case _~e1~_~e2~_ => NotExpr(BoolBinOp(Equals,e2,e1))}
      | "("~boolExpr~")" ^^ {case _~x~_ => x }
    )

  def op: Parser[BinOperator] = ("+" ^^ (x => AddOp)
                                |"-" ^^ (x => SubOp)
                                |"*" ^^ (x => MulOp)
                                |"/" ^^ (x => DivOp)
                                |"%" ^^ (x => ModOp))

  def stat: Parser[statement.Statement] = (
          "return"~";" ^^ (_ => Return(None))
          | "return"~expr~";" ^^ {case _~e~_ => Return(Some(e))}
          | atomicStatement<~";"
          | "if"~ boolExpr~block~"else"~block
            ^^ {case _~b~i~_~e => IfElse(b,i,e) }
          | "if"~ boolExpr ~ block ^^ {case _~b~i => IfElse(b,i,Nil)}
          | "while"~boolExpr ~ block ^^ {case _~b~i => While(b,i)}
          | "for"~"("~atomicStatement~";"~boolExpr~";"~atomicStatement~")"~block ^^
              { case _~_~a~_~b~_~c~_~d => ForLoop(a,b,c,d) }
          | "*"~expr~ "=" ~ expr~";" ^^ {case _~a~_~b~_ => IndirectAssignment(a,b)}
          | ident~"["~expr~"]"~"="~expr~";" ^^
              {case a~_~b~_~_~e~_ => IndirectAssignment(BinOp(AddOp,Var(a),b),e)}
          )

  def atomicStatement : Parser[statement.Statement] = (
        ident~"="~expr ^^ {case i~_~e => Assignment(i,e)}
      | ident~op~"="~expr ^^ {case i~o~_~e => Assignment(i,BinOp(o,Var(i),e))}
      | ident<~"++" ^^ {case x => Assignment(x,BinOp(AddOp,Var(x),Lit(1)))}
      | ident<~"--" ^^ {case x => Assignment(x,BinOp(SubOp,Var(x),Lit(1)))}
      | ident ~ "("~ repsep(expr, ",")~")" ^^ { case x~_~a~_ => VoidFunctionCall(x,a) }
      )

  def block: Parser[List[statement.Statement]] = ( "{"~> rep(stat) <~"}"
                                       | stat ^^ { List(_) })
  def func: Parser[function.Function] = (
            ("int"|"void")~ ident ~ "(" ~ repsep(ident,",") ~ ")" ~ block
            ^^ {case _~name~_~args~_~bl => new function.Function(name, args, bl)})

  def program: Parser[List[function.Function]] = rep(func)
}

object RPeANutWrapper {
  def runAssembly(code: String):String = {
    val writer = new PrintWriter(new File("/tmp/test.s" ))

    writer.write(code)
    writer.close()

    "java -jar rPeANUt2.3.jar /tmp/test.s".!!
  }
}

