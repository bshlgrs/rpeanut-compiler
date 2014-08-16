; Compiled by Buck's rPeANUt compiler!!!
0x0001:
  jump 0x0100

0x0100:
main:
starting-block-96:
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
; putsInt(longest_palindromic_substring("abcdca"))
  load #string--2080284256 R0;
  add R1 SP SP;
  push R0;
  push ZERO;
  push R0;
  call longest_palindromic_substring;
  sub SP ONE SP;
  pop R0;
  pop R1;
  load #19 R2;
  sub SP R2 SP;
  add R2 SP SP;
  push R0;
  push ZERO;
  push R0;
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
isPalindrome:
starting-block-17:
; while (j > i) {
while-7:
  load SP #-2 R0;
  load SP #-1 R1;
  sub R0 R1 R2;
  jumpn R2 while-loop-7-body;
  jump endWhile-7;
while-loop-7-body:
; if (!((*(str+j)) == (*(str+i))))
  load SP #-3 R0;
  load SP #-1 R1;
  add R0 R1 R2;
  load R2 R1;
  load SP #-2 R2;
  add R0 R2 R3;
  load R3 R0;
  sub R1 R0 R2;
  jumpnz R2 then-9;
  jump else-9;
; {
then-9:
; return 0;
  store ZERO #-4 SP;
  return;
  jump end-9;
; } else {
else-9:
; i = (i+1);
  load SP #-2 R0;
  add R0 ONE R0;
; j = (j-1);
  store R0 #-2 SP;
  load SP #-1 R0;
  sub R0 ONE R0;
  store R0 #-1 SP;
end-9:
; }
  jump while-7;
; }
endWhile-7:
; return 1;
  store ONE #-4 SP;
  return;
  return;
longest_palindromic_substring:
starting-block-34:
  load #0 R0;
; len = stringLength(str);
  store R0 #1 SP;
  load #4 R0;
  add R0 SP SP;
  push ZERO;
  load SP #-6 R1;
  push R1;
  call stringLength;
  sub SP ONE SP;
  pop R0;
  store R0 #0 SP;
  load #4 R0;
  sub SP R0 SP;
  load #0 R1;
; while (len > i) {
  store R1 #3 SP;
while-19:
  load SP #3 R0;
  load SP #4 R1;
  sub R0 R1 R2;
  jumpn R2 while-loop-19-body;
  jump endWhile-19;
while-loop-19-body:
; j = (i+1);
  load SP #3 R0;
  add R0 ONE R1;
; while (len > j) {
  store R1 #2 SP;
while-22:
  load SP #2 R0;
  load SP #4 R1;
  sub R0 R1 R2;
  jumpn R2 while-loop-22-body;
  jump endWhile-22;
while-loop-22-body:
  load SP #2 R0;
  add ZERO R0 R1;
; if ((j-i) > best)
  store R1 #5 SP;
  load SP #3 R1;
  sub R0 R1 R2;
  load SP #1 R0;
  sub R0 R2 R1;
  jumpn R1 then-24;
  jump else-24;
; {
then-24:
; if (isPalindrome(str, i, j) == 1)
  load #4 R0;
  add R0 SP SP;
  push ZERO;
  load SP #-6 R1;
  push R1;
  load SP #-3 R2;
  push R2;
  load SP #-5 R3;
  push R3;
  call isPalindrome;
  load #3 R0;
  sub SP R0 SP;
  pop R1;
  load #4 R2;
  sub SP R2 SP;
  sub R1 ONE R3;
  jumpnz R3 else-27;
  jump then-27;
; {
then-27:
; best = (j-i);
  load SP #2 R0;
  load SP #3 R1;
  sub R0 R1 R2;
  store R2 #1 SP;
  jump end-27;
; } else {
else-27:
end-27:
; }
  jump end-24;
; } else {
else-24:
end-24:
; }
; j = (j+1);
  load SP #2 R0;
  add R0 ONE R0;
  store R0 #2 SP;
  jump while-22;
; }
endWhile-22:
; i = (i+1);
  load SP #3 R0;
  add R0 ONE R0;
  store R0 #3 SP;
  jump while-19;
; }
endWhile-19:
; return (best+1);
  load SP #1 R0;
  add R0 ONE R1;
  store R1 #-2 SP;
  return;
  return;
longest_common_substring:
starting-block-90:
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
while-37:
  load SP #4 R0;
  load SP #5 R1;
  sub R0 R1 R2;
  jumpn R2 while-loop-37-body;
  jump endWhile-37;
while-loop-37-body:
  load #0 R0;
; while (len2 > j) {
  store R0 #2 SP;
while-39:
  load SP #2 R0;
  load SP #6 R1;
  sub R0 R1 R2;
  jumpn R2 while-loop-39-body;
  jump endWhile-39;
while-loop-39-body:
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
  jumpnz R2 else-41;
  jump then-41;
; {
then-41:
; if ((i*j) > 0)
  load SP #4 R0;
  load SP #2 R1;
  mult R0 R1 R2;
  sub ZERO R2 R0;
  jumpn R0 then-47;
  jump else-47;
; {
then-47:
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
  jump end-47;
; } else {
else-47:
  load #1 R0;
  store R0 #1 SP;
end-47:
; }
  jump end-41;
; } else {
else-41:
  load #0 R0;
  store R0 #1 SP;
end-41:
; }
; if ((i > 0 && (*(table+((len2*(i-1))+j))) > best))
  load SP #4 R0;
  sub ZERO R0 R1;
  jumpn R1 and-57;
  jump else-57;
and-57:
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
  jumpn R2 then-57;
  jump else-57;
; {
then-57:
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
  jump end-57;
; } else {
else-57:
end-57:
; }
; if ((j > 0 && (*(table+(((len2*i)+j)-1))) > best))
  load SP #2 R0;
  sub ZERO R0 R1;
  jumpn R1 and-70;
  jump else-70;
and-70:
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
  jumpn R2 then-70;
  jump else-70;
; {
then-70:
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
  jump end-70;
; } else {
else-70:
end-70:
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
  jumpn R2 then-86;
  jump else-86;
; {
then-86:
  load SP #1 R0;
  add ZERO R0 R1;
  store R1 #3 SP;
  jump end-86;
; } else {
else-86:
end-86:
; }
; j = (j+1);
  load SP #2 R0;
  add R0 ONE R0;
  store R0 #2 SP;
  jump while-39;
; }
endWhile-39:
; i = (i+1);
  load SP #4 R0;
  add R0 ONE R0;
  store R0 #4 SP;
  jump while-37;
; }
endWhile-37:
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

string--2080284256: block #"abcdca"
; This is a heap, which is used by malloc
0x3FF0:
frontier:
  block #0x4001
next:
  block #0x4000
0x4000:
  block #-1
  block 0x2FFF