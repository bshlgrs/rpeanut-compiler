

void main() (x (array 10) y) {
  y = 5;
  for(x = 0; (x<10); x++) {
    putsInt(x);
    array[x] = x;
    putsInt(array[x]);
    putsInt(y);
    puts("");
  }
}