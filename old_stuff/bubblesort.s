; int sum;
; int array[3];
0x0100 :
  load #string R0
  push R0
  call printString
  pop R0

  push R0
  load #8 R1
  push R1
  call bubbleSort
  pop R0
  pop R0

  load #string R0
  push R0
  call printString
  pop R0


; void bubblesort(int *array, int length) {
; using 0 = sorted, 1 = i, 6 = array, 7 = length
bubbleSort:
  load SP #-2 R6
  load SP #-1 R7 ; length = 5

bubbleSortOuterLoop: ; I'm doing this as a do while
  load #1 R0 ; sorted = 1

  load #2 R1
  sub R7 R1 R1 ; int i = length - 2

bubbleSortInnerLoop:
  add R6 R1 R2; &array + i
  add ONE R2 R3; &array + i + 1

  load R2 R4; R4 = array[i]
  load R3 R5; R5 = array[i+1]
  sub R4 R5 R3; R3 = array[i] - array[i+1]

  ; If that's negative, we don't need to swap them.
  jumpn R3 bubbleSortEndOfIf;

  add ONE R2 R3; &array + i + 1
  ; Otherwise, we do.
  store R4 R3; swap them
  store R5 R2

  load #0 R0; sorted = 0

bubbleSortEndOfIf:
  sub R1 ONE R1;


bubbleSortEndOfInnerLoop:
  add R1 ONE R2;
  jumpnz R2 bubbleSortInnerLoop

bubbleSortEndOfOuterLoop:
  jumpz R0 bubbleSortOuterLoop

  return


string : block #"hgfedcba"

array: block 3

printString :
  load SP #-1 R0; load location of string
  load R0 R1
printStringLoop:
  store R1 0xFFF0
  add R0 ONE R0
  load R0 R1
  jumpnz R1 printStringLoop
  load #'\n' R1
  store R1 0xFFF0
  return