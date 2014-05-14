def main() {
  x = "hello";
  reverse(x);
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

def reverse(string) {
  length = stringLength(string);
  halfLength = (length/2);
  for (x = 0; (x<length); x++) {
    puts(string);
    temp = string[x];
    string[x] = string[((length-x)-1)];
    string[((length-x)-1)] = temp;
  }
}

def bubbleSort(string) {
  length = stringLength(string);

}