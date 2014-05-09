# rPeANUt compiler

This sorta compiles a language like C to rPeANUt assembly:

```
$ scala Compile "def difference(x,y) { if (x > y) { return (x-y) ;} else { return (y-x); } }"

difference:
starting-block-5:
  load SP #0 R0;
  load SP #1 R1;
  sub R0 R1 R2;
  jumpn R2 else-1;
then-1:
  load SP #0 R0;
  load SP #1 R1;
  sub R0 R1 R2;
  store SP #0 R2;
  return;
  jump end-1;
else-1:
  load SP #1 R0;
  load SP #0 R1;
  sub R0 R1 R2;
  store SP #0 R2;
  return;
end-1:

```

It's not very fully featured, but it's coming along slowly!

## Things that do work

## Incomplete list of things that don't work
- Ternary operator
- Positions of variables on the stack are all made up and don't correspond much to reality
- Global variables
- String literals or array literals
