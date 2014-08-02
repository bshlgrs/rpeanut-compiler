; Compiled by Buck's rPeANUt compiler!!!
0x0001:
  jump 0x0100

0x0100:
main:
starting-block-48:
; x = "abcd";
  load #string-1065990462 R0;
; puts(x)
  push R0;
  push ZERO;
  push R0;
  call puts;
  sub SP ONE SP;
  pop ZERO;
  pop R0;
; reverse(x)
  push R0;
  push ZERO;
  push R0;
  call reverse;
  sub SP ONE SP;
  pop ZERO;
  pop R0;
; puts(x)
  push R0;
  push ZERO;
  push R0;
  call puts;
  sub SP ONE SP;
  pop ZERO;
  pop R0;
; bubbleSort(x)
  push R0;
  push ZERO;
  push R0;
  call bubbleSort;
  sub SP ONE SP;
  pop ZERO;
  pop R0;
; puts(x)
  push R0;
  push ZERO;
  push R0;
  call puts;
  sub SP ONE SP;
  pop ZERO;
  pop R0;
  return;
stringLength:
starting-block-6:
  load #0 R0;
; while (!(0 == (*string))) {
  store R0 #1 SP;
while-1:
  load SP #-1 R0;
  load R0 R1;
  sub ZERO R1 R0;
  jumpnz R0 while-loop-1-body;
  jump endWhile-1;
while-loop-1-body:
; string = (string+1);
  load SP #-1 R0;
  add R0 ONE R0;
; out = (out+1);
  store R0 #-1 SP;
  load SP #1 R0;
  add R0 ONE R0;
  store R0 #1 SP;
  jump while-1;
; }
endWhile-1:
; return out;
  load SP #1 R0;
  store R0 #-2 SP;
  return;
  return;
reverse:
starting-block-22:
; length = stringLength(string);
  load #4 R0;
  add R0 SP SP;
  push ZERO;
  load SP #-6 R1;
  push R1;
  call stringLength;
  sub SP ONE SP;
  pop R0;
  load #4 R1;
  sub SP R1 SP;
; halfLength = (length/2);
  load #2 R2;
  div R0 R2 R3;
  store R0 #2 SP;
  load #0 R0;
; while (halfLength > x) {
  store R0 #1 SP;
  store R3 #3 SP;
while-9:
  load SP #1 R0;
  load SP #3 R1;
  sub R0 R1 R2;
  jumpn R2 while-loop-9-body;
  jump endWhile-9;
while-loop-9-body:
; temp = (*(string+x));
  load SP #-1 R0;
  load SP #1 R1;
  add R0 R1 R2;
  load R2 R3;
; *(string+x) = (*(string+((length-x)-1)));
  add R0 R1 R2;
  load SP #2 R4;
  sub R4 R1 R5;
  sub R5 ONE R6;
  add R0 R6 R5;
  load R5 R6;
  store R6 R2;
; *(string+((length-x)-1)) = temp;
  sub R4 R1 R2;
  sub R2 ONE R4;
  add R0 R4 R2;
  store R3 R2;
; x = (x+1);
  add R1 ONE R1;
  store R1 #1 SP;
  jump while-9;
; }
endWhile-9:
  return;
bubbleSort:
starting-block-46:
; length = stringLength(string);
  load #3 R0;
  add R0 SP SP;
  push ZERO;
  load SP #-5 R1;
  push R1;
  call stringLength;
  sub SP ONE SP;
  pop R0;
  store R0 #-1 SP;
  load #3 R0;
  sub SP R0 SP;
  load #0 R1;
; while (length > k) {
  store R1 #3 SP;
while-24:
  load SP #3 R0;
  load SP #2 R1;
  sub R0 R1 R2;
  jumpn R2 while-loop-24-body;
  jump endWhile-24;
while-loop-24-body:
  load #0 R0;
; while ((length-1) > i) {
  store R0 #1 SP;
while-26:
  load SP #2 R0;
  sub R0 ONE R1;
  load SP #1 R0;
  sub R0 R1 R2;
  jumpn R2 while-loop-26-body;
  jump endWhile-26;
while-loop-26-body:
; if ((*(string+i)) > (*(string+(i+1))))
  load SP #-1 R0;
  load SP #1 R1;
  add R0 R1 R2;
  load R2 R3;
  add R1 ONE R2;
  add R0 R2 R1;
  load R1 R0;
  sub R0 R3 R1;
  jumpn R1 then-29;
  jump else-29;
; {
then-29:
; temp = (*(string+i));
  load SP #-1 R0;
  load SP #1 R1;
  add R0 R1 R2;
  load R2 R3;
; *(string+i) = (*(string+(i+1)));
  add R0 R1 R2;
  add R1 ONE R4;
  add R0 R4 R5;
  load R5 R4;
  store R4 R2;
; *(string+(i+1)) = temp;
  add R1 ONE R2;
  add R0 R2 R1;
  store R3 R1;
  jump end-29;
; } else {
else-29:
end-29:
; }
; i = (i+1);
  load SP #1 R0;
  add R0 ONE R0;
  store R0 #1 SP;
  jump while-26;
; }
endWhile-26:
; k = (k+1);
  load SP #3 R0;
  add R0 ONE R0;
  store R0 #3 SP;
  jump while-24;
; }
endWhile-24:
  return;


; Library functions:

puts :
  load SP #-1 R0; load location of string
  load R0 R1
putsLoop:
  store R1 0xFFF0
  add R0 ONE R0
  load R0 R1
  jumpnz R1 putsLoop
  load #'\n' R1
  store R1 0xFFF0
  return

; Data section:
string-1065990462: block #"abcd"