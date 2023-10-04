package main
import components._
import java.io.File
object Main extends App {
    val WordCount = new WordCount()
    WordCount.create
    WordCount.register_doc
    WordCount.contar
    WordCount.register_updates
    WordCount.selecionar
    WordCount.export_csv
}