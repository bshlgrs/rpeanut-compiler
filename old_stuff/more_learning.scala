// Why no equals sign needed in function definition?
def sort(xs: Array[Int]): Array[Int] = {
  if (xs.length <= 1) xs
  else {
    val pivot = xs(xs.length/2)
    Array.concat(sort(xs filter (pivot >)),
                 xs filter (pivot ==),
                 sort(xs filter (pivot <)))
  }
}

def two_sum(array: Array[Int]) : Array[(Int, Int)] = {
  (for {
      i <- 0 to array.length - 1;
      j <- i + 1 to array.length - 1;
      if array(i) + array(j) == 0
    } yield (i, j)).toArray
}

def uniq(array: List[Int]) : List[Int] = array match {
  case Nil => Nil
  case x :: xs => {
    val xs_uniq = uniq(xs)
    if (xs_uniq.contains(x)) xs_uniq else (x +: xs_uniq)
  }
}

def myTranspose(matrix: Array[Array[Int]]) : Array[Array[Int]] = {
  Array.ofDim[Int](matrix(0).length, matrix.length)
}

println(two_sum(Array(1,2,0,-1,-1,-2)).mkString("\n"))

println(uniq(List(1,2,0,-1,-1, -1,-2)).mkString(" "))

