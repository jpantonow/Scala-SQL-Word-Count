package components.commands
import java.io.File

//Trait para interagir com o usuario
//Mostra os livros disponiveis, e pergunta ao
//usuario quantas palavras e caracteres mais
//frequentes ele quer visualizar
trait Interaction {
  var txt_file: String = ""
  var book_name: String = ""
  var db_file: String = ""
  var csv_folder: String = ""
  var limit: Integer = 0

  def set_pathing(
      txt_path: String,
      db_path: String,
      name: String,
      csv: String
  ): Unit = {
    txt_file = txt_path
    db_file = db_path
    book_name = name
    csv_folder = csv
  }

  def display_texts: Unit = {
    println("\nWelcome, user! Which File would you like to read?\n")

    val directory_path = new File("src/main/scala/files/books")

    val contents = directory_path.list()
    for (i <- 0 until contents.size) {
      println(i + "-" + contents(i))
    }

    var chosen = scala.io.StdIn.readInt()
    book_name = contents(chosen).replace(".txt", "")
    txt_file = directory_path.getPath() + "/" + contents(chosen)
    db_file = "src/main/scala/components/db/database.db"
    csv_folder = "src/main/scala/files/spreadsheets/"
    // return (file, book, database)
  }

  def export_message: String = {
    println("\nWould you like to export your database into CSV file?(y/n)")
    val choice = scala.io.StdIn.readLine()
    return choice
  }

  def print_success(string: String): Unit = {
    val greenColor = "\u001B[32m"
    val resetColor = "\u001B[0m"
    println(greenColor + string + resetColor)
  }

  def set_limit(int: Integer): Unit = {
    limit = int
  }

  def user_limit: Unit = {
    println(
      "\nHow many words and characters ordered by frequency would you like to display?(Int)"
    )
    val number = scala.io.StdIn.readInt()
    limit = number
  }
}
