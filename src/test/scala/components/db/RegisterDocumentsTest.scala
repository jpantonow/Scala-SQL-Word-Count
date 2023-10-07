package components.db
import org.scalatest.funsuite.AnyFunSuite

class RegisterDocumentsTest extends AnyFunSuite {
  test("The count_words method should return true") {
    val db_register = new Register_Documents(
      "src/main/scala/files/books/test.txt",
      "src/test/scala/components/db/databasetest.db",
      "test.txt"
    )
    assert(db_register.count_words == true)
  }
}
