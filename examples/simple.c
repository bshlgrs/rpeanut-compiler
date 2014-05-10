def main() {
  x = printInt(factorial(5));
}

def factorial(x) {
  out = 1;
  while(x > 0) {
    out = (out * x);
    x = (x-1);
  }
  return out;
}