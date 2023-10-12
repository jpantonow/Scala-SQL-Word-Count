package components.commands
import components.db._
import components.db.{CreateTables, Select_Most_Frequent, Insert_Book}

//Classe WordCount
//Responsavel pela interacao com o usuario
//Responsavel pela interacao com os comandos do banco de dados
class WordCount extends Interaction {
  def create: Boolean = {
    val db_create = new CreateTables(txt_file, db_file, book_name)
    db_create.execute
  }

  def register_doc: Boolean = {
    val db_register = new Register_Documents(txt_file, db_file, book_name)
    db_register.register
  }

  def insert: Boolean = {
    val db_insert = new Insert_Book(txt_file, db_file, book_name)
    db_insert.execute
  }

  def register_updates: Boolean = {
    val db_register = new Register_Documents(txt_file, db_file, book_name)
    (db_register.count_words &&
    db_register.longest_word &&
    db_register.count_chars &&
    db_register.avg_char_word &&
    db_register.length_25)
  }

  def get_frequency: Select_Most_Frequent = {
    val db_select_most_frequent =
      new Select_Most_Frequent(txt_file, db_file, book_name, limit)
    db_select_most_frequent
  }

  def print_frequency: Unit = {
    val db_select_most_frequent = get_frequency

    print_success(s"\n$limit Most frequent words")
    for ((name, frequency) <- db_select_most_frequent.get_words) {
      println(s"$name has appeared $frequency times.")
    }

    print_success(s"\n$limit Most frequent characters")
    for ((name, frequency) <- db_select_most_frequent.get_characters) {
      println(s"$name has appeared $frequency times.")
    }
  }

  def check_existence: Boolean = {
    val db_register = new Register_Documents(txt_file, db_file, book_name)
    return db_register.check_register
  }

  def export_csv: Boolean = {
    val export_folder: String = csv_folder
    val db_Export_CSV =
      new Export_to_CSV(txt_file, db_file, book_name, export_folder)
    (db_Export_CSV.export_words &&
    db_Export_CSV.export_characters &&
    db_Export_CSV.export_data)
  }

  // Faz a contagem sem interacao com o usuario
  def run: Unit = {
    create
    if (check_existence) {
      export_csv
    } else {
      register_doc
      insert
      register_updates
      export_csv
    }
  }

  //Faz a contagem interagindo com o usuario
  def execute: Unit = {
    if (create) {
      print_success("\nSuccessfully created the Database")
    }

    if (check_existence) {
      print_success("\nThe book is already in the database. Showing results:")
      print_frequency
      if (export_message == "y") {
        export_csv
      }
    } else {
      if (register_doc) {
        print_success("\nSucessfully registered into Documents")
      }
      if (insert) { print_success("\nSuccessfully into Database") }

      register_updates
      print_frequency
      if (export_message == "y") {
        export_csv
      }
    }
  }

}
