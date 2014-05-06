require './cparser/lib/cparser/parser'
$parser = CParser::Parser.new

def getMainBody(instr)
  ($parser.parse(instr))[0][:function]
end

class TransformToPrettierAST < Parslet::Transform
  rule(:function => subtree(:stuff)) do
    {:function => {
      :type => stuff[0][:type],
      :name => stuff[1][:identifier],
      :body => TransformToPrettierAST.new.apply(stuff[2][:body])
      }
    }
  end

  rule(:body => )

  rule(:decimal => simple(:num)) { {:decimal => num.str.to_i } }
end