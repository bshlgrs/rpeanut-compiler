	.section	__TEXT,__text,regular,pure_instructions
	.globl	_initializeHeap
	.align	4, 0x90
_initializeHeap:                        ## @initializeHeap
	.cfi_startproc
## BB#0:
	pushq	%rbp
Ltmp2:
	.cfi_def_cfa_offset 16
Ltmp3:
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
Ltmp4:
	.cfi_def_cfa_register %rbp
	movq	_heap@GOTPCREL(%rip), %rax
	movq	_next@GOTPCREL(%rip), %rcx
	movq	_heap_size@GOTPCREL(%rip), %rdx
	movq	_frontier@GOTPCREL(%rip), %rsi
	movl	$1, (%rsi)
	movl	$10, (%rdx)
	movl	$0, (%rcx)
	movl	$-1, (%rax)
	popq	%rbp
	ret
	.cfi_endproc

	.globl	_edMalloc
	.align	4, 0x90
_edMalloc:                              ## @edMalloc
	.cfi_startproc
## BB#0:
	pushq	%rbp
Ltmp7:
	.cfi_def_cfa_offset 16
Ltmp8:
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
Ltmp9:
	.cfi_def_cfa_register %rbp
	subq	$16, %rsp
	leaq	L_.str(%rip), %rdi
	movb	$0, %al
	callq	_printf
	movq	_heap@GOTPCREL(%rip), %rdi
	movq	_next@GOTPCREL(%rip), %rcx
	movl	(%rcx), %edx
	movl	%edx, -4(%rbp)
	movslq	(%rcx), %rcx
	cmpl	$-1, (%rdi,%rcx,4)
	movl	%eax, -8(%rbp)          ## 4-byte Spill
	jne	LBB1_4
## BB#1:
	movq	_heap_size@GOTPCREL(%rip), %rax
	movq	_frontier@GOTPCREL(%rip), %rcx
	movl	(%rcx), %edx
	cmpl	(%rax), %edx
	jne	LBB1_3
## BB#2:
	leaq	L_.str1(%rip), %rdi
	movb	$0, %al
	callq	_printf
	movl	$1, %edi
	movl	%eax, -12(%rbp)         ## 4-byte Spill
	callq	_exit
LBB1_3:
	movq	_frontier@GOTPCREL(%rip), %rax
	movq	_heap@GOTPCREL(%rip), %rcx
	movq	_next@GOTPCREL(%rip), %rdx
	movl	(%rax), %esi
	movl	%esi, (%rdx)
	movslq	(%rdx), %rdx
	movl	$-1, (%rcx,%rdx,4)
	movl	(%rax), %esi
	addl	$1, %esi
	movl	%esi, (%rax)
	jmp	LBB1_5
LBB1_4:
	movq	_next@GOTPCREL(%rip), %rax
	movq	_heap@GOTPCREL(%rip), %rcx
	movslq	(%rax), %rdx
	movl	(%rcx,%rdx,4), %esi
	movl	%esi, (%rax)
LBB1_5:
	leaq	L_.str2(%rip), %rdi
	movl	-4(%rbp), %esi
	movb	$0, %al
	callq	_printf
	movl	-4(%rbp), %esi
	movl	%eax, -16(%rbp)         ## 4-byte Spill
	movl	%esi, %eax
	addq	$16, %rsp
	popq	%rbp
	ret
	.cfi_endproc

	.globl	_edFree
	.align	4, 0x90
_edFree:                                ## @edFree
	.cfi_startproc
## BB#0:
	pushq	%rbp
Ltmp12:
	.cfi_def_cfa_offset 16
Ltmp13:
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
Ltmp14:
	.cfi_def_cfa_register %rbp
	subq	$16, %rsp
	leaq	L_.str3(%rip), %rax
	movl	%edi, -4(%rbp)
	movl	-4(%rbp), %esi
	movq	%rax, %rdi
	movb	$0, %al
	callq	_printf
	movq	_next@GOTPCREL(%rip), %rdi
	movq	_heap@GOTPCREL(%rip), %rcx
	movl	(%rdi), %esi
	movslq	-4(%rbp), %rdx
	movl	%esi, (%rcx,%rdx,4)
	movl	-4(%rbp), %esi
	movl	%esi, (%rdi)
	movl	%eax, -8(%rbp)          ## 4-byte Spill
	addq	$16, %rsp
	popq	%rbp
	ret
	.cfi_endproc

	.globl	_printHeap
	.align	4, 0x90
_printHeap:                             ## @printHeap
	.cfi_startproc
## BB#0:
	pushq	%rbp
Ltmp17:
	.cfi_def_cfa_offset 16
Ltmp18:
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
Ltmp19:
	.cfi_def_cfa_register %rbp
	subq	$16, %rsp
	movl	$0, -4(%rbp)
LBB3_1:                                 ## =>This Inner Loop Header: Depth=1
	movq	_heap_size@GOTPCREL(%rip), %rax
	movl	-4(%rbp), %ecx
	cmpl	(%rax), %ecx
	jge	LBB3_4
## BB#2:                                ##   in Loop: Header=BB3_1 Depth=1
	leaq	L_.str4(%rip), %rdi
	movq	_heap@GOTPCREL(%rip), %rax
	movslq	-4(%rbp), %rcx
	movl	(%rax,%rcx,4), %esi
	movb	$0, %al
	callq	_printf
	movl	%eax, -8(%rbp)          ## 4-byte Spill
## BB#3:                                ##   in Loop: Header=BB3_1 Depth=1
	movl	-4(%rbp), %eax
	addl	$1, %eax
	movl	%eax, -4(%rbp)
	jmp	LBB3_1
LBB3_4:
	leaq	L_.str5(%rip), %rdi
	movb	$0, %al
	callq	_printf
	movl	%eax, -12(%rbp)         ## 4-byte Spill
	addq	$16, %rsp
	popq	%rbp
	ret
	.cfi_endproc

	.globl	_main
	.align	4, 0x90
_main:                                  ## @main
	.cfi_startproc
## BB#0:
	pushq	%rbp
Ltmp22:
	.cfi_def_cfa_offset 16
Ltmp23:
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
Ltmp24:
	.cfi_def_cfa_register %rbp
	subq	$16, %rsp
	movl	$0, -4(%rbp)
	callq	_initializeHeap
	callq	_printHeap
	callq	_edMalloc
	movq	_heap@GOTPCREL(%rip), %rcx
	movslq	%eax, %rdx
	movl	$5, (%rcx,%rdx,4)
	callq	_printHeap
	callq	_edMalloc
	movq	_heap@GOTPCREL(%rip), %rcx
	movslq	%eax, %rdx
	movl	$7, (%rcx,%rdx,4)
	callq	_printHeap
	movl	$0, %edi
	callq	_edFree
	callq	_printHeap
	callq	_edMalloc
	movq	_heap@GOTPCREL(%rip), %rcx
	movslq	%eax, %rdx
	movl	$9, (%rcx,%rdx,4)
	callq	_printHeap
	movl	$0, %eax
	addq	$16, %rsp
	popq	%rbp
	ret
	.cfi_endproc

	.comm	_frontier,4,2           ## @frontier
	.comm	_heap_size,4,2          ## @heap_size
	.comm	_next,4,2               ## @next
	.comm	_heap,40,4              ## @heap
	.section	__TEXT,__cstring,cstring_literals
L_.str:                                 ## @.str
	.asciz	"mallocing... "

L_.str1:                                ## @.str1
	.asciz	"Error: out of memory\n"

L_.str2:                                ## @.str2
	.asciz	"allocated %d\n"

L_.str3:                                ## @.str3
	.asciz	"freeing %d\n"

L_.str4:                                ## @.str4
	.asciz	"%d "

L_.str5:                                ## @.str5
	.asciz	"\n"


.subsections_via_symbols
