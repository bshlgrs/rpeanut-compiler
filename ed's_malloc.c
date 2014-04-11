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