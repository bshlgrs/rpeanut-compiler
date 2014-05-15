def main() {
  x = "edcba";
  puts(x);
  bubbleSort(x);
  puts(x);
}

def stringLength(string) {
  out = 0;
  while (*string != 0) {
    string++;
    out++;
  }
  return out;
}

def bubbleSort(string) {
  length = stringLength(string);
  sorted = false;
  while(sorted == false) {
    sorted = true;
    for(i = 0; (i < (length - 1)); i++) {
      if (string[i] > string[(i+1)]) {
        temp = string[i];
        string[i] = string[(i+1)];
        string[(i+1)] = temp;
        sorted = false;
      }
    }
  }
}