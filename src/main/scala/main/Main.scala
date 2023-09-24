package main
import sqlite3._
object Main extends App {
    //Execução das funções do banco de dados
    val db_create_tables = new CreateTables()
    val db_insert_words = new Insert_Words()
    //val db_select_words = new Select_Words()

    //Caso de sucesso
    val greenColor = "\u001B[32m"
    val resetColor = "\u001B[0m"
    println(greenColor + "Successful" + resetColor)
}