; This code switches from one IO behavior to another:
; it echoes the terminal until you type 'e', and then
; it prints out every character you enter twice.

; This behavior can be used in functions which read stdin.

0x0001:
	jump IoHandler1

; Main program: turn the IO interrupt on, then loop forever.
0x0100:
	store ONE 0xFFF2
	
loop:
	jump loop


; This just echoes whatever it's given to the terminal.
IoHandler1:
	push R0
	push R1
	

	load 0xFFF0 R0 ; read in data

	; If they typed 'e', change handler.
	load #'e' R1;
	sub R0 R1 R1;
	jumpz R1 IoHandlerChangeTo2
	store R0 0xFFF0 ; write data to stdout

IoHandler1End:
	pop R1	
	pop R0
	reset IM
	return

IoHandlerChangeTo2:
	load 0x0001 R0
	load #0xFF00 R1
	and R0 R1 R1
	load #IoHandler2 R0
	or R0 R1 R1
	store R1 0x0001
	jump IoHandler1End

IoHandler2:
	push R0
	load 0xFFF0 R0 ; read in data
	store R0 0xFFF0 ; write data to stdout
	store R0 0xFFF0 ; write data to stdout
	pop R0
	reset IM
	return
