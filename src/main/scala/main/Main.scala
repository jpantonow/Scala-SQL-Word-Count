package main
import components.commands._
object Main extends App {
  val wordCount = new WordCount
  wordCount.display_texts
  wordCount.user_limit
  wordCount.execute
}
