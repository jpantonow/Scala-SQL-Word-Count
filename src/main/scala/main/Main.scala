package main
import sqlite3._
object Main extends App {
    val db_create = new CreateTables()
    val db_insert = new Insert_Words()
    val greenColor = "\u001B[32m"
    val resetColor = "\u001B[0m"
    println(greenColor + "Successful" + resetColor)
}