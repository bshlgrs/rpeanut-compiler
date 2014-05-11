package rPeANutWrapper

import java.io._
import scala.sys.process._

object RPeANutWrapper {
  def runAssembly(code: String):String = {
    val writer = new PrintWriter(new File("/tmp/test.s" ))

    writer.write(code)
    writer.close()

    "java -jar rPeANUt2.3.jar /tmp/test.s".!!
  }
}