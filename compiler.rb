require './cparser/lib/cparser/parser'
parser = CParser::Parser.new

p parser.parse("int f() { return 3; }")
