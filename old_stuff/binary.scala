class Tree[+T]
case object Empty extends Tree[Nothing]
case class Node[T](val elem: T, val left: Tree[T], val right: Tree[T]) extends Tree[T]

def inOrder[T] (t: Tree[T]): List[T] = t match {
  case Empty => Nil
  case Node(e, left, right) => inOrder(left) ::: List(e) ::: inOrder(right)
}

def preOrder[T] (t: Tree[T]): List[T] = t match {
  case Empty => Nil
  case Node(e, left, right) => e :: preOrder(left) ::: preOrder(right)
}


def postOrder[T] (t: Tree[T]): List[T] = t match {
  case Empty => Nil
  case Node(e, left, right) => postOrder(left) ::: postOrder(right) ::: List(e)
}

def size[T] (t: Tree[T]): Int = t match {
  case Empty => 0
  case Node(_, left, right) => 1 + size(left) + size(right)
}

def print[T] (t: Tree[T]): String = t match {
  case Empty => ""
  case Node(e, left, right) => e.toString + " (" + print(left) + " " + print(right) + ")"
}

def insert[T] (t: Tree[T]): Tree[T] = t match {
  case Empty
}