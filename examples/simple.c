void main() {
  x = "abcdefgh";
  puts(x);
  reverse(x);
  puts(x);
  bubbleSort(x);
  puts(x);
}

int stringLength(string) {
  out = 0;
  while (*string != 0) {
    string++;
    out++;
  }
  return out;
}

void reverse(string) {
  length = stringLength(string);
  halfLength = length/2;
  for (x = 0; (x<halfLength); x++) {
    temp = string[x];
    string[x] = string[(length-x)-1];
    string[(length-x)-1] = temp;
  }
}

void bubbleSort(string) {
  length = stringLength(string);
  for(k = 0; (k<length); k++) {
    for(i = 0; (i < (length - 1)); i++) {
      if (string[i] > string[(i+1)]) {
        temp = string[i];
        string[i] = string[(i+1)];
        string[(i+1)] = temp;
      }
    }
  }
}