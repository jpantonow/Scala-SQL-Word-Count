package components.db
import org.scalatest.funsuite.AnyFunSuite

class ExportCSVTest extends AnyFunSuite {
    test("The export_words method should return true"){
        val db_export = new Export_to_CSV(
            "src/test/scala/files/books/test.txt",
            "src/test/scala/components/db/databasetest.db",
            "test.txt"
        )
        assert(db_export.export_words == true)
    }
}
