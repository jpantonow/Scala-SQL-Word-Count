package components
import java.io.File

//Classe para interagir com o usuario
class Interaction{
    var (txt_file, book_name, db_file) = display_texts

    def print_message(string: String): Unit = {
        val greenColor = "\u001B[32m"
        val resetColor = "\u001B[0m"
        println(greenColor + string + resetColor)
    }
    
    def display_texts: (String, String, String) = {

        println("\nWelcome, user! Which File would you like to read?\n")
        val directory_path = new File("src/main/scala/texts")
        val contents = directory_path.list()

        for(i <- 0 until contents.size){
            println(i + "-" + contents(i))
        }

        var chosen = scala.io.StdIn.readInt()
        var book = contents(chosen).replace(".txt", "")
        var file = directory_path.getPath() + "/" + contents(chosen)
        val db_path = new File("src/main/scala/sqlite3/databases")
        var database = db_path.getPath() + "/" + book + ".db"

        return (file, book, database)
    }

    def export_message: String = {
        println("\nWould you like to export your database into CSV file?(y/n)")
        val choice = scala.io.StdIn.readLine()
        return choice
    }
}