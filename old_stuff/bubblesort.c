#include <stdio.h>


int length = 5;
int myArray[5] = { 5, 4, 3, 2, 1};

void bubblesort(int array[5]) {
  int sorted = 0;
  int swap;

  do {
    sorted = 1;
    int i = length - 2;
    do {
      if (array[i] > array[i+1]) {
        swap = array[i];
        array[i] = array[i+1];
        array[i+1] = swap;
        sorted = 0;
      }
      i = i - 1;
    } while (i >= 0);
  } while (sorted == 0);
}

int main () {
  for (int i = 0; i < 5; i++) {
    printf("%d ", myArray[i]);
  }
  printf("\n");

  bubblesort(myArray);

  for (int i = 0; i < 5; i++) {
    printf("%d ", myArray[i]);
  }
  printf("\n");
}