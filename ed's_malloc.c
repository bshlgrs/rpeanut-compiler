// This is an implementation of a simple variation of malloc, which can only
// allocate blocks of a fixed size.

// The basic idea is that within the space for dynamic memory, every free space
// is a pointer to the next free space. We have two pointers in memory, frontier
// and next. Next points to the beginning of the linked list. Frontier points to
// the next place in our "heap" which hasn't been allocated ever.

// To malloc, we look at the memory location pointed to by next. If it's
// pointing to -1, then we set it to the frontier and move the frontier pointer
// along. If it's pointing to another memory location, we set next to point to
// this second node. In both cases, we return the original value of next. This
// operation is basically equivalent to removing the first item in the linked
// list.

// To free, we push the freed location to the beginning of the linked list.

// Both of these operations take constant time, and malloc can provide a warning
// if too much space is used.

#include <stdio.h>
#include <stdlib.h>

int heap[10];
int frontier;
int heap_size;
int next;


void initializeHeap() {
  frontier = 1;
  heap_size = 10;
  next = 0;
  heap[0] = -1;
}

int edMalloc() {
  printf("mallocing... ");
  int out = next;

  if (heap[next] == -1) {
    if (frontier == heap_size) {
      printf("Error: out of memory\n");
      exit(1);
    }
    next = frontier;
    heap[next] = -1;
    frontier++;
  }
  else {
    next = heap[next];
  }
  printf("allocated %d\n", out);

  return out;
}

void edFree(int pos) {
  printf("freeing %d\n", pos);
  heap[pos] = next;
  next = pos;
}

void printHeap() {
  for (int i = 0; i < heap_size; i++) {
    printf("%d ", heap[i]);
  }
  printf("\n");
}

int main () {
  initializeHeap();
  printHeap();

  heap[edMalloc()] = 5;

  printHeap();

  heap[edMalloc()] = 7;

  printHeap();

  edFree(0);
  printHeap();

  heap[edMalloc()] = 9;

  printHeap();
  return 0;
}