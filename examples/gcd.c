def main() {
  printInt(gcd(10,15));
}

def gcd(a,b) {
  while (b != 0) {
    t = b;
    b = (a % b);
    a = t;
  }
  return a;
}