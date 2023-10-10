package components.commands
import org.scalatest.funsuite.AnyFunSuite

class WordCountTest extends AnyFunSuite {
  test("The most frequent world should be \"tubarão\""){
    val wordCount = new WordCount()
    wordCount.set_pathing(
      "src/test/scala/files/books/teste.txt",
      "src/test/scala/components/db/databasewordcount.db",
      "teste",
      "src/test/scala/files/spreadsheets/"
    )
    assert(wordCount.check_existence == true)
    var listaFrequencia = wordCount.get_frequency.words
    assert(listaFrequencia(0) == ("tubarão", 20))
  }
  // test("The most frequent word should be \"tubarão\"") {
  //    val wordCount = new WordCount(
  //     true
  //     "teste.txt",
  //     "baby-shark",
  //     "src/test/scala/components/db/database.db"
  //   )
  //   wordCount.execute()
  //   var listaFrequencia = wordCount.get_frequency.words(5)
  //   assert(listaFrequencia(0) == ("tubarão", 20))
  // }
}
