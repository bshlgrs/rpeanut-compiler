; The IO interrupt jumps to 0x0001. However, the other
; interrupts jump to consecutive memory addresses. So
; the only thing I'm putting here is a jump to the handler.
0x0001:
	jump iodevhandler

; Main program: turn the IO interrupt on, then loop forever.
0x0100:
	store ONE 0xFFF2
loop:
	jump loop

; This just echoes whatever it's given to the terminal.
iodevhandler:
	push R0
	load 0xFFF0 R0 ; read in data
	store R0 0xFFF0 ; write data to stdout
	pop R0
	reset IM
	return

getChar:
  store ONE 0xFFF2
  load 0x0001 R0
  load #getCharHandler R1
  or R0 R1 R1
  store R1 0x0001
loop:
  jump loop
getCharHandler:
  load 0xFFF0 R0
  store R0 #-1 SP
  store ZERO 0xFFF2
  return