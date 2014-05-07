package main

import expr._
import binOperator._
import boolExpr._
import varOrLit._
import interInstr._
import counter.Counter
import statement._
import assemblyMaker._
import function._


object Main extends App {
  // // swap = array[i]
  // val firstLine = statement.Assignment("temp", Load(BinOp(AddOp, Var("array"), Var("i"))))
  // // array[i] = array[i+1]
  // val secondLine = statement.IndirectAssignment(BinOp(AddOp, Var("array"), Var("i")),
  //                                   Load(BinOp(AddOp,
  //                                       BinOp(AddOp, Lit(1), Var("array")),
  //                                       Var("i"))))
  // // array[i+1] = swap
  // val thirdLine = statement.IndirectAssignment(BinOp(AddOp,
  //                                       BinOp(AddOp, Lit(1), Var("array")),
  //                                       Var("i")),
  //                                       Var("swap"))

  // val exampleCode = List[statement.Statement](firstLine, secondLine, thirdLine)

  // // arr[i] > arr[i+1]
  // val exampleBool = boolExpr.BoolBinOp(boolExpr.GreaterThan,
  //                             Load(BinOp(AddOp, Var("array"), Var("i"))),
  //                             Load(BinOp(AddOp,
  //                                       BinOp(AddOp, Lit(1), Var("array")),
  //                                       Var("i")))) : boolExpr.BoolExpr

  // val moreComplexExampleCode = statement.IfElse(exampleBool, exampleCode, List())

  // println(moreComplexExampleCode)
  // // println(StatementHelper.statementsToIntermediate(exampleCode).mkString("\n"))
  // // println(moreComplexExampleCode.toIntermediate().mkString("\n"))

  // val blocks = AssemblyMaker.separateIntoBlocks(moreComplexExampleCode.toIntermediate())
  // println(blocks.mkString("\n"))

  val myFunction = new Function("addTwoNums", List("x","y"),
              List(
                  statement.Assignment("temp", BinOp(AddOp, Var("x"), Var("y"))),
                  Return(Some(Var("temp")))
                  )
  )

  println(myFunction.toString)

  println("\n\n--------------\n\n")
  // println(myFunction.toIntermediate.mkString("\n"))

  println(myFunction.blocks.mkString("\n\n"))

  println("\n\n--------------\n\n")

  println(myFunction.toAssembly.mkString("\n"))


}