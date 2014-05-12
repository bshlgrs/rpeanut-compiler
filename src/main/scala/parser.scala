import scala.util.parsing.combinator._
import scala.collection.mutable.ListBuffer
import statement._
import expr._
import binOperator._
import boolExpr._
import function._
import java.io._
import scala.sys.process._
import standardLibrary._

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
  def expr: Parser[Expr] = (
             "("~expr~op~expr~")" ^^ {case _~e1~op~e2~_ => BinOp(op, e1, e2)}
           | ident~"["~expr~"]" ^^ {case i~_~e~_ => Load(BinOp(AddOp, Var(i), e))}
           | boolExpr~"?"~expr~":"~expr ^^ { case i~_~t~_~e => IfExpression(i,t,e) }
           | "*"~expr ^^ {case _~e1 => Load(e1) }
           | ident ~ "("~ repsep(expr, ",")~")" ^^ { case x~_~a~_ => FunctionCall(x,a) }
           | ident ^^ { Var(_) }
           | wholeNumber ^^ (x => Lit(x.toInt))
           | "true" ^^ (x => Lit(1))
           | "false" ^^ (x => Lit(0))
           | """'\S'""".r ^^ (x => Lit(x.charAt(1).toInt) )
           | stringLiteral ^^ { StringLiteral(_)}
           )

  def boolExpr: Parser[BoolExpr] = (
      "("~expr~"=="~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(Equals,e1,e2)}
    | "("~expr~">"~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(GreaterThan,e1,e2)}
    | "("~expr~"<"~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(GreaterOrEqual,e2,e1)}
    | "("~expr~">="~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(GreaterOrEqual,e1,e2)}
    | "("~expr~"<="~expr~")" ^^ {case _~e1~_~e2~_ => BoolBinOp(GreaterThan,e2,e1)}
    | "("~expr~"!="~expr~")" ^^ {case _~e1~_~e2~_ => NotExpr(BoolBinOp(Equals,e2,e1))}
    | "("~boolExpr~"&&"~boolExpr~")" ^^ {case _~l~_~r~_ => AndExpr(l,r)}
    | "("~boolExpr~"||"~boolExpr~")" ^^ {case _~l~_~r~_ => OrExpr(l,r)}
    | "!"~boolExpr ^^ {case _~x => NotExpr(x)}
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
          | "while"~ boolExpr ~ block ^^ {case _~b~i => While(b,i)}
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
            ("def")~ ident ~ "(" ~ repsep(ident,",") ~ ")" ~ block
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

object Compile extends CParser {
  val output = new StringBuilder()
  val stringSection = new StringBuilder()
  val globals: ListBuffer[String] = new ListBuffer()
  def main(args: Array[String]) {
    output.append("; Compiled by Buck's rPeANUt compiler!!!\n")
    output.append("0x0001:\n\tjump 0x0100\n\n")
    parseAll(program, io.Source.fromFile(args(0)).mkString) match {
      case Success(result, _) => {
        for (function <- result) {

          for (x <- function.strings) {
            val hash = "string-"+x.hashCode()
            globals.append(hash)
            stringSection.append("block "+hash+": \""+x+"\"\n")
          }


          if (function.name == "main")
            output.append("0x0100:\n")
          output.append(function.toAssembly(globals.toList).mkString("\n")+"\n")
        }



        // output.append(io.Source.fromFile("./examples/stdio.h").mkString)

        for ((function, implementation) <- StandardLibrary.standardLibrary) {
          if (output.toString.contains(function))
            output.append(implementation)
        }


        // println(RPeANutWrapper.runAssembly(output.toString()))

      }
      case x => println("Parse error"); println(x)
    }

    println(output.toString + stringSection.toString)
  }
}