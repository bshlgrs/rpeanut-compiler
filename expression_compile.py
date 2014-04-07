# I have explicitly chosen not to optimise the output of this compiler for the
# moment. That's the next project.

import random

binOps = {"*" : "mult", "%": "mod", "-": "sub"}

# returns (code, next_register, output)
def compile_expression(expression, scope, next_register):
    if expression[0] == "Var":
        return ([], next_register, scope[expression[1]])
    elif expression[0] == "Const":
        code = ["load #%d R%d"%(expression[1], next_register)]
        return (code, next_register + 1, next_register)
    elif expression[0] == "BinOp":
        code = []
        new_code, new_next_register, left_register = (
                    compile_expression(expression[2], scope, next_register))
        code.extend(new_code)

        new_code, new_next_register, right_register = (
                    compile_expression(expression[3], scope, new_next_register))
        code.extend(new_code)
        code.append("%s R%d R%d R%d"%(binOps[expression[1]],
                        left_register, right_register, next_register))
        return (code, next_register + 1, next_register)

# returns code
def compile_statement(statement, scope):
    code = ["; %s"%str(statement[0])]
    if statement[0] == "Empty":
        return code
    elif statement[0] == "Assignment":
        new_code, x, output = compile_expression(statement[2], scope, len(scope))
        code.extend(new_code)
        code.append("load R%d R%d"%(output, scope[statement[1]]))
        return code
    elif statement[0] == "IfElse":
        lhs, rhs, op, if_body, else_body = statement[1:]
        if op != "==":
            raise "not implemented"

        new_code, x, output = compile_expression(("BinOp","-",lhs,rhs), scope, len(scope))
        code.extend(new_code)

        after_if_name = "IfStatementBodyEnd" + str(random.random())
        end_name = "IfStatementElseEnd" + str(random.random())

        code.append("jumpz R%d %s"%(output, after_if_name))

        for statement in if_body:
            code.extend(compile_statement(statement, scope))

        if else_body:
            code.append("jump %s"%end_name)
            code.append("%s:"%after_if_name)

            for statement in else_body:
                code.extend(compile_statement(statement, scope))

            code.append("%s:"%end_name)
        else:
            code.append("%s:"%after_if_name)

        return code



"x % 5 * y % 7"
example = \
      ("BinOp", "*",
          ("BinOp", "%",
              ("Var", "x"),
              ("Const", 5)
          ),
          ("BinOp", "%",
              ("Var", "y"),
              ("Const", 7)
          )
      )

assert compile_expression(("Var", "x"), {"x":0, "y":1}, 2) == ([], 2, 0)
assert compile_expression(("Var", "y"), {"x":0, "y":1}, 2) == ([], 2, 1)

for x in compile_statement(
        ("IfElse", ("Var", "x"), ("Const", 2), "==",
                [("Assignment", "x", example)],
                [("Assignment", "x", ("Const", 2))]), {"x":0, "y":1}):
    print x
# =>
"""
LOAD #5 R2
MOD R0 R2 R2
LOAD #7 R3
MOD R1 R3 R3
MULT R2 R3 R2"""