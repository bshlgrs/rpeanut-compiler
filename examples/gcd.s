0x0001:
  jump 0x0100

0x0100:
main:
starting-block-2:
; printInt(gcd(10, 15))
  push ZERO;
  load #10 R0;
  push R0;
  load #15 R1;
  push R1;
  call gcd;
  load #2 R0;
  sub SP R0 SP;
  pop R1;
  push R1;
  push ZERO;
  push R1;
  call printInt;
  sub SP ONE SP;
  pop ZERO;
  pop R0;
  return;
gcd:
starting-block-6:
; while (!(0 == b)) {
while-3:
  load SP #-1 R0;
  sub ZERO R0 R1;
  jumpnz R1 while-loop-3-body;
  jump endWhile-3;
while-loop-3-body:
  load SP #-1 R0;
  add ZERO R0 R1;
; b = (a%b);
  load SP #-2 R2;
  mod R2 R0 R0;
  add ZERO R1 R2;
  store R0 #-1 SP;
  store R2 #-2 SP;
  jump while-3;
; }
endWhile-3:
; return a;
  load SP #-2 R0;
  store R0 #-3 SP;
  return;
  return;


; Library functions:

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
  return

; Data section: