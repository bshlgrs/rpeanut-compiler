# I have explicitly chosen not to optimise the output of this compiler for the
# moment. That's the next project.

import random

class Counter:
    def __init__(self):
        self.count = 0
    def get_count(self):
        self.count += 1
        return self.count - 1

c = Counter()

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
        code.append("move R%d R%d"%(output, scope[statement[1]]))
        return code
    elif statement[0] == "IfElse":
        lhs, rhs, op, if_body, else_body = statement[1:]
        if op != "==":
            raise Exception("not implemented")

        name = c.get_count()

        code.append("; If Statement #%d"%name)

        new_code, x, output = compile_expression(("BinOp","-",lhs,rhs), scope, len(scope))
        code.extend(new_code)


        after_if_name = "IfStatementBodyEnd" + str(name)
        end_name = "IfStatementElseEnd" + str(name)

        code.append("jumpnz R%d %s"%(output, after_if_name))

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
    elif statement[0] == "If":
        lhs, rhs, op, if_body = statement[1:]
        return compile_statement(("IfElse", lhs, rhs, op, if_body, []), scope)
    elif statement[0] == "While":
        lhs, rhs, op, while_body = statement[1:]
        if op != "==":
            raise Exception("not implemented")

        name = c.get_count()

        start_name = "WhileStatementStart" + str(name)
        end_name = "WhileStatementBodyEnd" + str(name)

        new_code, x, output = compile_expression(("BinOp","-",lhs,rhs), scope, len(scope))

        code.append("%s:"%start_name)
        code.extend(new_code)

        code.append("jumpnz R%d %s"%(output, end_name))

        code.append("; WhileStatementBodyStart #%d"%name)

        for statement in while_body:
            code.extend(compile_statement(statement, scope))

        code.append("jump %s"%start_name)
        code.append("%s:"%end_name)

        return code
    else:
        raise Exception("Not implemented: %s"%str(statement))

def pretty_print_assembly(assembly):
    for line in assembly:
        if line[-1] == ":":
            print line
        elif line[0] == ";":
            print "  " + line
        else:
            print "\t" + line

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

compilation_value = compile_statement(
        ("While", ("Var", "x"), ("Const", 2), "==",
                [("Assignment", "x", example)]), {"x":0, "y":1})

pretty_print_assembly(compilation_value)