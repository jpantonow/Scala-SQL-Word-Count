package components.db
import org.scalatest.funsuite.AnyFunSuite

class DbCommandsTest extends AnyFunSuite {
    test("The execute method from CreateTables should return true") {
        val db_create = new CreateTables(
        "src/test/scala/files/books/test.txt",
        "src/test/scala/components/db/DbTest.db",
        "test"
        )
    assert(db_create.execute == true)
  }

  test("The execute method from Insert_Book should return true"){
    val db_insert = new Insert_Book(
        "src/test/scala/files/books/test.txt",
        "src/test/scala/components/db/DbTest.db",
        "test"
        )
    assert(db_insert.execute == true)
  }

  test("The register documents methods should return true"){
    val db_register = new Register_Documents(
      "src/test/scala/files/books/test.txt",
      "src/test/scala/components/db/DbTest.db",
      "test"
    )

    assert(db_register.register == true)
    assert(db_register.count_words == true)
    assert(db_register.count_chars == true)
    assert(db_register.avg_char_word == true)
    assert(db_register.longest_word == true)
  }

  test("The Export_to_CSV methods should return true"){
    val db_export = new Export_to_CSV(
        "src/test/scala/files/books/test.txt",
        "src/test/scala/components/db/DbTest.db",
        "test",
        "src/test/scala/files/spreadsheets-db/"
      )
      assert(db_export.export_words == true)
      assert(db_export.export_characters == true)
      assert(db_export.export_data == true)
    }
  }

