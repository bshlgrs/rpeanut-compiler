; Compiled by Buck's rPeANUt compiler!!!
0x0001:
  jump 0x0100

0x0100:
main:
starting-block-66:
; putsInt(longest_common_substring("abcd", "efbce", table))
  load #string-1065990462 R0;
  load #string--1195851999 R1;
  load #19 R2;
  add R2 SP SP;
  push R0;
  push R1;
  push ZERO;
  push R0;
  push R1;
  load SP #-23 R3;
  push R3;
  call longest_common_substring;
  load #3 R0;
  sub SP R0 SP;
  pop R1;
  pop R2;
  pop R3;
  load #19 R4;
  sub SP R4 SP;
  add R4 SP SP;
  push R1;
  push ZERO;
  push R1;
  call putsInt;
  sub SP ONE SP;
  pop ZERO;
  pop R0;
  load #19 R1;
  sub SP R1 SP;
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
longest_common_substring:
starting-block-62:
; len1 = stringLength(str1);
  load #6 R0;
  add R0 SP SP;
  push ZERO;
  load SP #-10 R1;
  push R1;
  call stringLength;
  sub SP ONE SP;
  pop R0;
  store R0 #-1 SP;
  load #6 R0;
  sub SP R0 SP;
; len2 = stringLength(str2);
  add R0 SP SP;
  push ZERO;
  load SP #-9 R1;
  push R1;
  call stringLength;
  sub SP ONE SP;
  pop R0;
  store R0 #0 SP;
  load #6 R0;
  sub SP R0 SP;
  load #0 R1;
  store R1 #3 SP;
  load #0 R1;
; while (len1 > i) {
  store R1 #4 SP;
while-9:
  load SP #4 R0;
  load SP #5 R1;
  sub R0 R1 R2;
  jumpn R2 while-loop-9-body;
  jump endWhile-9;
while-loop-9-body:
  load #0 R0;
; while (len2 > j) {
  store R0 #2 SP;
while-11:
  load SP #2 R0;
  load SP #6 R1;
  sub R0 R1 R2;
  jumpn R2 while-loop-11-body;
  jump endWhile-11;
while-loop-11-body:
  load #0 R0;
; if ((*(str1+i)) == (*(str2+j)))
  store R0 #1 SP;
  load SP #-3 R0;
  load SP #4 R1;
  add R0 R1 R2;
  load R2 R0;
  load SP #-2 R1;
  load SP #2 R2;
  add R1 R2 R3;
  load R3 R1;
  sub R0 R1 R2;
  jumpnz R2 else-13;
  jump then-13;
; {
then-13:
; if ((i*j) > 0)
  load SP #4 R0;
  load SP #2 R1;
  mult R0 R1 R2;
  sub ZERO R2 R0;
  jumpn R0 then-19;
  jump else-19;
; {
then-19:
; best = ((*(table+((len2*(i-1))+(j-1))))+1);
  load SP #4 R0;
  sub R0 ONE R1;
  load SP #6 R0;
  mult R0 R1 R2;
  load SP #2 R0;
  sub R0 ONE R1;
  add R2 R1 R0;
  load SP #-1 R1;
  add R1 R0 R2;
  load R2 R0;
  add R0 ONE R1;
  store R1 #1 SP;
  jump end-19;
; } else {
else-19:
  load #1 R0;
  store R0 #1 SP;
end-19:
; }
  jump end-13;
; } else {
else-13:
  load #0 R0;
  store R0 #1 SP;
end-13:
; }
; if ((i > 0 && (*(table+((len2*(i-1))+j))) > best))
  load SP #4 R0;
  sub ZERO R0 R1;
  jumpn R1 and-29;
  jump else-29;
and-29:
  load SP #4 R0;
  sub R0 ONE R1;
  load SP #6 R0;
  mult R0 R1 R2;
  load SP #2 R0;
  add R2 R0 R1;
  load SP #-1 R0;
  add R0 R1 R2;
  load R2 R0;
  load SP #1 R1;
  sub R1 R0 R2;
  jumpn R2 then-29;
  jump else-29;
; {
then-29:
; best = (*(table+((len2*(i-1))+j)));
  load SP #4 R0;
  sub R0 ONE R1;
  load SP #6 R0;
  mult R0 R1 R2;
  load SP #2 R0;
  add R2 R0 R1;
  load SP #-1 R0;
  add R0 R1 R2;
  load R2 R0;
  store R0 #1 SP;
  jump end-29;
; } else {
else-29:
end-29:
; }
; if ((j > 0 && (*(table+(((len2*i)+j)-1))) > best))
  load SP #2 R0;
  sub ZERO R0 R1;
  jumpn R1 and-42;
  jump else-42;
and-42:
  load SP #6 R0;
  load SP #4 R1;
  mult R0 R1 R2;
  load SP #2 R0;
  add R2 R0 R1;
  sub R1 ONE R0;
  load SP #-1 R1;
  add R1 R0 R2;
  load R2 R0;
  load SP #1 R1;
  sub R1 R0 R2;
  jumpn R2 then-42;
  jump else-42;
; {
then-42:
; best = (*(table+((len2*i)+(j-1))));
  load SP #6 R0;
  load SP #4 R1;
  mult R0 R1 R2;
  load SP #2 R0;
  sub R0 ONE R1;
  add R2 R1 R0;
  load SP #-1 R1;
  add R1 R0 R2;
  load R2 R0;
  store R0 #1 SP;
  jump end-42;
; } else {
else-42:
end-42:
; }
; *(table+((len2*i)+j)) = best;
  load SP #6 R0;
  load SP #4 R1;
  mult R0 R1 R2;
  load SP #2 R0;
  add R2 R0 R1;
  load SP #-1 R0;
  add R0 R1 R2;
  load SP #1 R0;
  store R0 R2;
; if (best > biggest)
  load SP #3 R1;
  sub R1 R0 R2;
  jumpn R2 then-58;
  jump else-58;
; {
then-58:
  load SP #1 R0;
  add ZERO R0 R1;
  store R1 #3 SP;
  jump end-58;
; } else {
else-58:
end-58:
; }
; j = (j+1);
  load SP #2 R0;
  add R0 ONE R0;
  store R0 #2 SP;
  jump while-11;
; }
endWhile-11:
; i = (i+1);
  load SP #4 R0;
  add R0 ONE R0;
  store R0 #4 SP;
  jump while-9;
; }
endWhile-9:
; return biggest;
  load SP #3 R0;
  store R0 #-4 SP;
  return;
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

putsInt:
  load #0 R3
  load #10 R1
  load SP #-1 R0
putsIntLoop:
  mod R0 R1 R2
  div R0 R1 R0
  push R2
  add ONE R3 R3
  jumpnz R0 putsIntLoop
  load #'0' R0
putsIntLoop2:
  pop R1
  add R0 R1 R1
  store R1 0xFFF0
  sub R3 ONE R3
  jumpnz R3 putsIntLoop2
  load #' ' R0
  store R0 0xFFF0
  return

; Data section:
string-1065990462: block #"abcd"

string--1195851999: block #"efbce"
; This is a heap, which is used by malloc
0x3FF0:
frontier:
  block #0x4001
next:
  block #0x4000
0x4000:
  block #-1
  block 0x2FFF