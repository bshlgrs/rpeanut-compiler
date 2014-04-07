from pycparser import c_parser

parser = c_parser.CParser()

def compile_function(function):
  name = function.decl.name
  args_list = [x.name for x in function.decl.type.args.params]
  #print args_list

  local_scope = {}
  code = []

  for (index, name) in enumerate(args_list):
    local_scope[name] = index
    code.append("load SP #{} R{}".format(-1 - len(args_list) + index, index))

  print local_scope
  print code

  body = function.body
  for statement in body.block_items:
    code.extend(compile_statement(statement, local_scope))

def compile_statement(statement, local_scope):
  statement.show()
  print str(statement.__class__)
  print statement.__dict__
  return []

def compile_function_from_string(instr):
  function = parser.parse(instr).children()[0][1]
  compile_function(function)

compile_function_from_string("int f(x) {int a, b=0; a = x; return a + x; }");