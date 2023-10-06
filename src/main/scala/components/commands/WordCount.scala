package components.commands
import components.db.{Register_Documents, CreateTables, Insert_Book, Select_Most_Frequent, Export_to_CSV}

class WordCount extends Interaction{
    def create: Unit = {
        val db_create = new CreateTables(txt_file,db_file,book_name)
        print_success("\nSuccessfully created the Database")
    }


    def register_doc: Unit = {
        val db_register = new Register_Documents(txt_file,db_file,book_name)
        db_register.register
        print_success("\nSucessfully registered into Documents")
    }

    def contar: Unit = {
        val db_insert = new Insert_Book(txt_file,db_file, book_name)
        print_success("\nSuccessfully into Database")
    }

    def register_updates: Unit = {
        val db_register = new Register_Documents(txt_file,db_file,book_name)
        db_register.count_words
        db_register.longest_word
        db_register.count_chars
        db_register.avg_char_word
        db_register.length_25
    }
    def selecionar: Unit = {
        val db_select_most_frequent = new Select_Most_Frequent(txt_file,db_file, book_name)
        print_success("\n25 Most frequent words")
        db_select_most_frequent.words
        print_success("\n25 Most frequent characters")
        db_select_most_frequent.characters

    }

    def print_success(string: String): Unit = {
        val greenColor = "\u001B[32m"
        val resetColor = "\u001B[0m"
        println(greenColor + string + resetColor)
    }

    def check_existence: Boolean = {
        val db_register = new Register_Documents(txt_file,db_file,book_name)
        return db_register.check_register 
    }

    def export_csv: Unit = {
        if(export_message=="y"){
            val db_export_Csv = new Export_to_CSV(txt_file, db_file, book_name)
            print_success("Successfully into CSV File")
        }
        else{
            return
        }
    }

    def execute: Unit = {
        create
        if(check_existence){
            print_success("\nThe book is already in the database. Showing results:")
            selecionar
            export_csv
        }
        else{
            register_doc
            contar
            register_updates
            selecionar
            export_csv
        }
    }
}