package counter

object Counter {
  var counter = 0
  def getCounter(): Int = {
    counter = counter + 1
    counter
  }
  def getTempVarName(): String = {
    counter = counter + 1
    "$tmp" + counter.toString
  }
}
