; malloc


; int malloc()
malloc:
	load next R0
	load R0 R1 ; heap[next]

	store R0 SP #-1 ; return the malloc'd location

	jumpn R1 malloc_move_frontier ; if next >= 0 
	
	store R1 next;
	
	return

malloc_move_frontier:
	load R0 R1 ; R1 = heap[next]
	load frontier R2; R2 = frontier
	load #0x8000 R3

	sub R3 R2 R3
	jumpz R3 memory_overflow
	
	store R2 next; next = frontier
	store MONE R0; heap[next] = -1;
	add R2 ONE R2; frontier ++
	store R2 frontier;
	
	return;
	
	
; print "error: memory overflow" and exit
memory_overflow:
load #'E' R0
store R0 0xFFF0
load #'r' R0
store R0 0xFFF0
load #'r' R0
store R0 0xFFF0
load #'o' R0
store R0 0xFFF0
load #'r' R0
store R0 0xFFF0
load #':' R0
store R0 0xFFF0
load #' ' R0
store R0 0xFFF0
load #'m' R0
store R0 0xFFF0
load #'e' R0
store R0 0xFFF0
load #'m' R0
store R0 0xFFF0
load #'o' R0
store R0 0xFFF0
load #'r' R0
store R0 0xFFF0
load #'y' R0
store R0 0xFFF0
load #' ' R0
store R0 0xFFF0
load #'o' R0
store R0 0xFFF0
load #'v' R0
store R0 0xFFF0
load #'e' R0
store R0 0xFFF0
load #'r' R0
store R0 0xFFF0
load #'f' R0
store R0 0xFFF0
load #'l' R0
store R0 0xFFF0
load #'o' R0
store R0 0xFFF0
load #'w' R0
store R0 0xFFF0
load #'\n' R0
store R0 0xFFF0
halt

; free(int pos)
free:
	load SP #-1 R0
	load next R1; R1 = next
	store R1 R0; heap[pos] = net;
	store R0 next;
	return

0x3FF0:
frontier:
	block #0x4001
next:
	block #0x4000
0x4000:
	block 0x4000

