package components.commands
import org.scalatest.funsuite.AnyFunSuite

class WordCountTest extends AnyFunSuite {
  test("The most frequent world should be \"doodoo\""){
    val wordCount = new WordCount()
    wordCount.set_pathing(
      "src/test/scala/files/books/teste.txt",
      "src/test/scala/components/db/databasewordcount.db",
      "teste",
      "src/test/scala/files/spreadsheets/"
    )
    wordCount.set_limit(5)
    wordCount.run
    assert(wordCount.check_existence == true)
    var frequencias = wordCount.get_frequency
    var listaPalavras = frequencias.words
    assert(listaPalavras(0) == ("doodoo",81))
  }
}
