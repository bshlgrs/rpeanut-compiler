package standardLibrary

object StandardLibrary {

  val standardLibrary = Map(
// void printInt(int x)
"printInt" -> """
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
  """,

// void putsInt(int x)
"putsInt" -> """
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
  """,

// void printChar(char x)
"printChar" -> """
printChar:
  load SP #-1 R0
  store R0 0xFFF0
  return
""",

// char getChar()
"getChar" -> """
; this can probably be done more efficiently
getChar:
  store ONE 0xFFF2
  load #getCharHandler R1
  load 0x0001 R0
  load #0xFFFF R2
  and R2 R0 R0
  or R0 R1 R1
  store R1 0x0001
loop:
  jump loop
getCharHandler:
  load 0xFFF0 R0
  pop ZERO; because
  store R0 #-1 SP
  store ZERO 0xFFF2
  return
  """,

// void putsString(*char string)
"puts" -> """
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
  """
  )
}