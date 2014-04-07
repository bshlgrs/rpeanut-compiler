# class Scope:
#   def __init__(self, num_registers=10):
#     self.scope = {}
#     self.num_registers = num_registers

#   def get_var_position(var):
#     return scope[var]

#   def get_next_position():
#     for i in range(num_registers):
#       if i not in scope:
#         return i
#     raise "No more registers"

binOps = {"*" : "mult", "%": "mod"}

# return (code, next_register, output)
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

for line in compile_expression(example, {"x":0, "y":1}, 2)[0]:
    print line
# =>
"""
LOAD #5 R2
MOD R0 R2 R2
LOAD #7 R3
MOD R1 R3 R3
MULT R2 R3 R2"""