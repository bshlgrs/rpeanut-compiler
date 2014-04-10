printString :
  load SP #-1 R0; load location of string
  load R0 R1
printStringLoop:
  store R1 0xFFF0
  add R0 ONE R0
  load R0 R1
  jumpnz R1 printStringLoop
  return

; Heres how you just print a string:

  load #100 R0
  store R0 0xFFF0
  load #104 R0
  store R0 0xFFF0