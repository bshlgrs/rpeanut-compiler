package main

import expr._
import binOperator._
import boolExpr._
import varOrLit._
import interInstr._
import counter.Counter
import statement._


object Main extends App {
  // swap = array[i]
  val firstLine = statement.Assignment("temp", Load(BinOp(AddOp, Var("array"), Var("i"))))
  // array[i] = array[i+1]
  val secondLine = statement.IndirectAssignment(BinOp(AddOp, Var("array"), Var("i")),
                                    Load(BinOp(AddOp,
                                        BinOp(AddOp, Lit(1), Var("array")),
                                        Var("i"))))
  // array[i+1] = swap
  val thirdLine = statement.IndirectAssignment(BinOp(AddOp,
                                        BinOp(AddOp, Lit(1), Var("array")),
                                        Var("i")),
                                        Var("swap"))

  val exampleCode = List[statement.Statement](firstLine, secondLine, thirdLine)

  // arr[i] > arr[i+1]
  val exampleBool = boolExpr.BoolBinOp(GreaterThan,
                              Load(BinOp(AddOp, Var("array"), Var("i"))),
                              Load(BinOp(AddOp,
                                        BinOp(AddOp, Lit(1), Var("array")),
                                        Var("i"))))

  val moreComplexExampleCode = statement.IfElse(exampleBool, exampleCode, List())

  println(moreComplexExampleCode)
  println(StatementHelper.statementsToIntermediate(exampleCode).mkString("\n"))
}