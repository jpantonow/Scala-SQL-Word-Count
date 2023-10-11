package main
import components.commands._
object Main extends App {
  val wordCount = new WordCount()
  // wordCount.set_pathing(
  //   "src/test/scala/files/books/teste.txt",
  //   "src/test/scala/components/db/database.db",
  //   "Baby_Shark"
  // )
  

  wordCount.display_texts
  wordCount.user_limit
  wordCount.execute
}
