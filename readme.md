This might compile C to rPeANUt one day!

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

