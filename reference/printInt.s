0x0100:
	load #1234 R0
	push R0
	call printNum
	pop R0
	halt

; void printInt(int x) {
printInt:
	load #0 R3
	
	load #10 R1
	load SP #-1 R0
printNumLoop:
	mod R0 R1 R2
	div R0 R1 R0
	push R2
	add ONE R3 R3
	jumpnz R0 printNumLoop
	load #'0' R0
printNumLoop2:
	pop R1
	add R0 R1 R1
	store R1 0xFFF0
	sub R3 ONE R3
	jumpnz R3 printNumLoop2

	return
