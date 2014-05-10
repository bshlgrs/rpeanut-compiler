puts "Enter string to print:"

string = gets

string.each_char do |c|
  puts "load #'#{c.inspect[1..-2]}' R0"
  puts "store R0 0xFFF0"
end