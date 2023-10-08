package components.db
import org.scalatest.funsuite.AnyFunSuite

class DbCommandsTest extends AnyFunSuite {
    test("The execute method from CreateTables should return true") {
        val db_create = new CreateTables(
        "src/test/scala/files/books/test.txt",
        "src/test/scala/components/db/databasetest.db",
        "test.txt"
        )
    assert(db_create.execute == true)
  }

  test("The execute method from Insert_Book should return true"){
    val db_insert = new Insert_Book(
        "src/test/scala/files/books/test.txt",
        "src/test/scala/components/db/databasetest.db",
        "test.txt"
        )
    assert(db_insert.execute == true)
  }

    test("The Export_to_CSV methods should return true"){
        val db_export = new Export_to_CSV(
            "src/test/scala/files/books/test.txt",
            "src/test/scala/components/db/databasetest.db",
            "test.txt",
            "src/test/scala/files/spreadsheets/"
        )
        assert(db_export.export_words == true)
        assert(db_export.export_characters == true)
    }
  }

