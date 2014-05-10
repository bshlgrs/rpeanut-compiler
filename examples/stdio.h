printInt:
  load #0 R3
  load #10 R1
  load SP #-1 R0
printIntLoop:
  mod R0 R1 R2
  div R0 R1 R0
  push R2
  add ONE R3 R3
  jumpnz R0 printIntLoop
  load #'0' R0
printIntLoop2:
  pop R1
  add R0 R1 R1
  store R1 0xFFF0
  sub R3 ONE R3
  jumpnz R3 printIntLoop2
  load #' ' R0
  store R0 0xFFF0
  return