package main
import java.io.File
import components.commands.WordCount
object Main extends App {
  val WordCount = new WordCount()
  // val wordLimits = scala.io.StdIn.readInt()
  WordCount.execute()
}
