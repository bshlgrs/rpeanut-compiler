; This is an implementation of a simpler version of malloc.

; A call to 'malloc' will return the address of a word in
; memory which has now been allocated for your usage.

; Calling 'free' will free up the used memory.

; This basically works by constructing a linked list in memory
; of free memory.

; Malloc and free are both constant time.

; My friend Ed Swernofsky came up with this algorithm, I just
; implemented it.

0x0100:
	push R0
	call malloc
	pop R0

	push R0
	call malloc
	pop R0

	push R0
	call malloc
	pop R0

	load #0x4001 R0
	push R0
	call free
	pop R0

	push R0
	call malloc
	pop R0
	halt

; malloc


; int malloc()
malloc:
	load next R0
	load R0 R1 ; heap[next]

	store R0 #-1 SP; return the mallocd location

	jumpn R1 malloc_move_frontier ; if next >= 0

	store R1 next;

	return

malloc_move_frontier:
	load frontier R2; R2 = frontier
	load #0x7000 R3

	sub R3 R2 R3
	jumpz R3 memory_overflow

	store R2 next; next = frontier
	store MONE R2; heap[next] = -1;
	add R2 ONE R2; frontier ++
	store R2 frontier;

	return;


; print "error: memory overflow" and exit
memory_overflow:
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
	block #-1
	block 0x2FFF

