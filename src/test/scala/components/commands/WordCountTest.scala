package components.commands
import org.scalatest.funsuite.AnyFunSuite

class WordCountTest extends AnyFunSuite {
  val wordCount = new WordCount()

  wordCount.set_pathing(
    "src/test/scala/files/books/test.txt",
    "src/test/scala/components/db/WordCountTest.db",
    "test",
    "src/test/scala/files/spreadsheets-word-count/"
  )

  wordCount.set_limit(1)
  wordCount.run

  test("The most frequent word should be \"lord\"") {
    assert(wordCount.check_existence == true)
    assert(wordCount.get_frequency.get_words(0) == ("lord", 2))
  }

  test("The most frequent character should be \"o\"") {
    assert(wordCount.check_existence == true)
    assert(wordCount.get_frequency.get_characters(0) == ("o", 9))
  }

  test("The longest word should be \"corinthians\"") {
    assert(wordCount.check_existence == true)
    assert(wordCount.get_frequency.get_longest == "corinthians")
  }

}
