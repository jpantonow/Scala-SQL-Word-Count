package components.db
import org.scalatest.funsuite.AnyFunSuite

class DbCommandsTest extends AnyFunSuite {
    test("The execute method should return true") {
        val db_create = new CreateTables(
        "src/test/scala/files/books/test.txt",
        "src/test/scala/components/db/databasetest.db",
        "test.txt"
        )
    assert(db_create.execute == true)
  }

}
