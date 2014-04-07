This might compile C to rPeANUt one day!

Here's what I have at the moment:

```python
for x in compile_statement(
        ("IfElse", ("Var", "x"), ("Const", 2), "==",
                [("Assignment", "x", example)],
                [("Assignment", "x", ("Const", 2))]), {"x":0, "y":1}):
    print x
```
outputs

```
; IfElse
load #2 R2
sub R0 R2 R2
jumpnz R2 IfStatementBodyEnd0.808708101953
; Assignment
load #5 R2
mod R0 R2 R2
load #7 R3
mod R1 R3 R3
mult R2 R3 R2
move R2 R0
jump IfStatementElseEnd0.460291366317
IfStatementBodyEnd0.808708101953:
; Assignment
load #2 R2
move R2 R0
IfStatementElseEnd0.460291366317:
```


##Dependencies:

pycparser

##Plan:

To start with, I just want to be able to compile functions on integers which return integers, like this one:

int gcd(int x, int y)
{
  // x >= y and y >= 0
  if (y == 0)
    return x;
  else
    return gcd(y, x % y);
}

Assume no more than 10 variables are in scope at any given time.

Mapping is a hash from variables to register numbers.

I need to write:

compile_function(function, mapping) => code
compile_statement(statement, mapping) => code, new mapping
compile_expression(expression, mapping) => code, new mapping

