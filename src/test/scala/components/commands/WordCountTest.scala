package components.commands
import org.scalatest.funsuite.AnyFunSuite

class WordCountTest extends AnyFunSuite {
  val wordCount = new WordCount()

  wordCount.set_pathing(
      "src/test/scala/files/books/teste4.txt",
      "src/test/scala/components/db/WordCountTest.db",
      "test",
      "src/test/scala/files/spreadsheets-word-count/"
  )

  test("The most frequent word should be \"lord\"") {
    wordCount.set_limit(1)
    wordCount.run
    assert(wordCount.check_existence == true)
    assert(wordCount.get_frequency.get_words(0) == ("lord", 2))
  }

  // test("The most frequent character should be \"z\"") {
  //   val wordCount = new WordCount()

  //   wordCount.set_pathing(
  //     "src/test/scala/files/books/teste5.txt",
  //     "src/test/scala/components/db/WordCountTest.db",
  //     "Abcdário",
  //     "src/test/scala/files/spreadsheets-word-count/"
  //   )
  //   wordCount.set_limit(1)
  //   wordCount.run
  //   assert(wordCount.check_existence == true)
  //   assert(wordCount.get_frequency.get_characters(0) == ("z", 26))
  // }

  // test("The longest word should be \"consequências\"") {
  //   val wordCount = new WordCount()

  //   wordCount.set_pathing(
  //     "src/test/scala/files/books/teste3.txt",
  //     "src/test/scala/components/db/WordCountTest.db",
  //     "Faroeste_Caboclo",
  //     "src/test/scala/files/spreadsheets-word-count/"
  //   )
  //   wordCount.set_limit(1)
  //   wordCount.run
  //   assert(wordCount.check_existence == true)
  //   assert(wordCount.get_frequency.get_longest == "consequências")
  // }
}
