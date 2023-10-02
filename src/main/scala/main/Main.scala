package main
import components._
object Main extends App {
    val WordCount = new WordCount()
    WordCount.contar
    WordCount.selecionar
    WordCount.export_csv
}