package components.commands
import org.scalatest.funsuite.AnyFunSuite

class WordCountTest extends AnyFunSuite {
  test("The most frequent word should be \"propeller\"") {
    val wordCount = new WordCount()

    wordCount.set_pathing(
      "src/test/scala/files/books/teste4.txt",
      "src/test/scala/components/db/databasewordcount.db",
      "My_propeller",
      "src/test/scala/files/spreadsheets/"
    )
    wordCount.set_limit(1)
    wordCount.run
    assert(wordCount.check_existence == true)
    assert(wordCount.get_frequency.get_words(0) == ("propeller", 6))
  }

  test("The most frequent character should be \"z\"") {
    val wordCount = new WordCount()

    wordCount.set_pathing(
      "src/test/scala/files/books/teste5.txt",
      "src/test/scala/components/db/databasewordcount.db",
      "Abcdário",
      "src/test/scala/files/spreadsheets/"
    )
    wordCount.set_limit(1)
    wordCount.run
    assert(wordCount.check_existence == true)
    assert(wordCount.get_frequency.get_characters(0) == ("z", 26))
  }

  test("The longest word should be \"consequências\"") {
    val wordCount = new WordCount()

    wordCount.set_pathing(
      "src/test/scala/files/books/teste3.txt",
      "src/test/scala/components/db/databasewordcount.db",
      "Faroeste_Caboclo",
      "src/test/scala/files/spreadsheets/"
    )
    wordCount.set_limit(1)
    wordCount.run
    assert(wordCount.check_existence == true)
    assert(wordCount.get_frequency.get_longest == "consequências")
  }
}
