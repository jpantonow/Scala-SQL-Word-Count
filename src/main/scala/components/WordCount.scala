package components
import sqlite3.{Select_Words, CreateTables, Insert_Words, Select_Characters}
import sqlite3.Export_to_CSV

class WordCount {
    def contar: Unit = {
        val db_create = new CreateTables()
        val db_insert = new Insert_Words()
        print_success("\nSuccessfully into Database")
    }

    def selecionar: Unit = {
        print_success("\n100 Most frequent words")
        val db_select_words = new Select_Words()
        print_success("\n100 Most frequent characters")
        val db_select_char = new Select_Characters()

    }
    
    def print_success(string: String): Unit = {
        val greenColor = "\u001B[32m"
        val resetColor = "\u001B[0m"
        println(greenColor + string + resetColor)
    }

    def export_csv: Unit = {
        val db_export_Csv = new Export_to_CSV()
        print_success("Successfully into CSV File")
    }
}