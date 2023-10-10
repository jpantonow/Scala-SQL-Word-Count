package main
import java.io.File
import components.commands._
object Main extends App {
  val wordCount = new WordCount
  // val wordLimits = scala.io.StdIn.readInt()
  wordCount.display_texts
  wordCount.user_limit
  wordCount.execute
}
