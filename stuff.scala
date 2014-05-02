def factorial(x: Int): Int = {
  if (x == 0) 1;
  else x * factorial(x-1);
}

def sort[T <% Ordered[T]] (xs: Array[T]): Array[T] = {
  if (xs.length <= 1) xs
  else {
    val pivot = xs(xs.length / 2)
    Array.concat(
      sort(xs filter (pivot >)),
           xs filter (pivot ==),
           xs filter (pivot <))
  }
}

def max(x: Int, y: Int) : Int = {
  if (x > y) x
  else y
}

def greet() = println("Hello, world")

println(factorial(5))
greet()