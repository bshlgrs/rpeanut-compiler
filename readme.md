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

The following code works fine:
```
def main() {
  x = printInt(factorial2(6));
}

def factorial2(x) {
  if (x==0) {
    return 1;
  }
  return (x*factorial2((x-1)));
}
```

## Incomplete list of things that don't work
- Ternary operator
- Global variables
- String literals or array literals


## Notes
- I don't promise to calculate expressions in the order they were inputted.