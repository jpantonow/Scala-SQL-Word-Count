package components.commands
import org.scalatest.funsuite.AnyFunSuite

class WordCountTest extends AnyFunSuite {
  test("The most frequent word should be 'capitú'") {
    val WordCount = new WordCount()
    WordCount.execute()
    var listaFrequencia = WordCount.get_frequency.words(5)
    assert(listaFrequencia(0) == ("capitú", 340))
  }
}
