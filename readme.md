# rPeANUt compiler

This sorta compiles a language like C to rPeANUt assembly:

```
$ compile "def difference(x,y) { if (x > y) { return (x-y) ;} else { return (y-x); } }"

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

Check out `examples/simple.c` for an example pseudo-C file, and `examples/simple.s` for what it translates to.

## Incomplete list of things that don't work
- Ternary operator
- Global variables
- String literals or array literals

## Things which should be in the standard library
- printString
- printInt
- printChar
- readChar
- readInt
- readLine
- drawDot

## Notes
- I don't promise to calculate expressions in the order they were inputted.

# Time taken

I'll keep updating the steps that examples/simple.c takes to run:
- 14th May: 2002