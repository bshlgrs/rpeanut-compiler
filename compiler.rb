require './cparser/lib/cparser/parser'
parser = CParser::Parser.new

"int f() { return 3; }"
# goes to
 [{:function=>[{:type=>{:keyword=>"int"@0}}, {:identifier=>"f"@4},
            {:body=>[{:declarations=>nil}, {:return=>{:keyword=>"return"@10,
                :value=>{:decimal=>"3"@17}}}]}]}]


"int f(int x, int y) { int a = x+y+1; return a; }"
# goes to
[{:function=>[{:type=>{:keyword=>"int"@0}}, {:identifier=>"f"@4},
        {:type=>{:keyword=>"int"@6}}, {:identifier=>"x"@10},
        {:type=>{:keyword=>"int"@13}}, {:identifier=>"y"@17},
        {:body=>[{:declarations=>[
            {:type=>{:keyword=>"int"@22}},
            {:identifier=>"a"@26, :operator=>"="@28,
                :additive=>{:left=>{:identifier=>"x"@30},
                  :operator=>"+"@31,
                    :right=>{:additive=>{:left=>{:identifier=>"y"@32},
                      :operator=>"+"@33, :right=>{:decimal=>"1"@34}}}}}]},
            {:return=>{:keyword=>"return"@37, :value=>{:identifier=>"a"@44}}}]}]}]

# int g; int f(x) { int a; return a + g + x }
exampleScope = {:g => {:global => 0x0101},
                :x => {:local => -3},
                :a => {:local => -1}}
# The bool
registers = { :x => {:register => 0, :synced => false} }

# returns [code, output_register], and edits registers
def compileExpression(expression, scope, registers)

end