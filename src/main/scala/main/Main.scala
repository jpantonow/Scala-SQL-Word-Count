package main
import java.io.File
import components.commands.WordCount
object Main extends App {
  val wordCount = new WordCount()
  // wordCount.set_pathing(
  //   "src/test/scala/files/books/teste.txt",
  //   "src/test/scala/components/db/database.db",
  //   "Baby_Shark"
  // )
  

  wordCount.display_texts
  wordCount.set_limit
  wordCount.execute
}
