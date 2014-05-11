def main() {
  x = printInt(factorial2(6));
}

def factorial2(x) {
  if (x==0) {
    return 1;
  }
  return (x*factorial2((x-1)));
}
