package components.commands
import components.db._
import components.db.{CreateTables, Select_Most_Frequent, Insert_Book}
import components.db.Export_to_CSV

class WordCount extends Interaction {
  def create: Boolean = {
    val db_create = new CreateTables(txt_file, db_file, book_name)
    db_create.execute
    // print_success("\nSuccessfully created the Database")
  }

  def register_doc: Boolean = {
    val db_register = new Register_Documents(txt_file, db_file, book_name)
    db_register.register
    // print_success("\nSucessfully registered into Documents")
  }

  def insert: Boolean = {
    val db_insert = new Insert_Book(txt_file, db_file, book_name)
    db_insert.execute
    // print_success("\nSuccessfully into Database")
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
      new Select_Most_Frequent(txt_file, db_file, book_name)
    db_select_most_frequent
  }

  def print_frequency: Unit = {
    val db_select_most_frequent = get_frequency

    print_success(s"\n$limit Most frequent words")
    for ((name, frequency) <- db_select_most_frequent.words(limit)) {
      println(s"$name has appeared $frequency times.")
    }
    
    print_success(s"\n$limit Most frequent characters")
    for ((name, frequency) <- db_select_most_frequent.characters(limit)) {
      println(s"$name has appeared $frequency times.")
    }
  }

  def check_existence: Boolean = {
    val db_register = new Register_Documents(txt_file, db_file, book_name)
    return db_register.check_register
  }
//"src/main/scala/files/spreadsheets/"
  def export_csv: Boolean = {
    if (export_message == "y") {
      val export_folder: String = csv_folder
      val db_Export_CSV =
        new Export_to_CSV(txt_file, db_file, book_name, export_folder)
      (db_Export_CSV.export_words &&
      db_Export_CSV.export_characters &&
      db_Export_CSV.export_data)
      // print_success("Successfully into CSV File")
    } else {
      return false
    }
  }

  def run: Unit = {
    create
    if (check_existence) {
      get_frequency
      export_csv
    } else {
      register_doc
      insert
      register_updates
      get_frequency
      export_csv
    }
  }

  def execute: Unit = {
    create
    if (check_existence) {
      print_success("\nThe book is already in the database. Showing results:")
      print_frequency
      export_csv
    } else {
      register_doc
      insert
      register_updates
      print_frequency
      export_csv
    }
  }

}
