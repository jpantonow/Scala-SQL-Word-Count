package components.db

import java.sql.Connection
import java.sql.SQLException
import java.sql.DriverManager
import java.sql.Statement
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.io.PrintWriter
import java.io.File
import scala.io.Source
import components.commands.Interaction

//Trait para obter o caminho do banco de dados
//e conectar com o driver jdbc
class Initialize(path_to_text: String, path_to_database: String) {
  val url = s"jdbc:sqLite:$path_to_database"
  val book = scala.io.Source.fromFile(path_to_text)
  val stoptxt = scala.io.Source.fromFile("src/main/scala/files/stop-words.txt")
  val lines: List[String] = book.getLines().toList
  val stopwords: List[String] = stoptxt.getLines().toList
  var stop = stopwords.toString().split(" ")
  stop = stop.map(_.filter(_.isLetter))
  stop = stop.map(_.toLowerCase())
  stop = stop.sorted

  def print_error(error: String): Unit = {
    val redColor = "\u001b[31m"
    val resetColor = "\u001b[0m"
    println(redColor + error + resetColor)
  }
}

//Classe pra inicializar o banco de dados com as tabelas necessárias
class CreateTables(
    path_to_text: String,
    path_to_database: String,
    book_name: String
) extends Initialize(path_to_text: String, path_to_database: String) {

  execute

  def execute: Boolean = {
    var rt: PreparedStatement = null
    var conn: Connection = null
    try {
      // Estabelendo a conexão com o JDBC
      conn = DriverManager.getConnection(url)

      // Desativando o autocommit
      conn.setAutoCommit(false)

      // Array que contém os comandos SQL de criação das tabelas
      val criar = Array(
        "CREATE TABLE IF NOT EXISTS documents (book TEXT PRIMARY KEY, num_words INTEGER, " +
          "num_char INTEGER, avg_char_word INTEGER, longest_word TEXT, length_25 INTEGER);",
        "CREATE TABLE IF NOT EXISTS words(book TEXT , name TEXT, frequency INTEGER, FOREIGN KEY (book)" +
          " REFERENCES documents(book), PRIMARY KEY(book, name));",
        "CREATE TABLE IF NOT EXISTS characters(book TEXT , char TEXT, frequency INTEGER, FOREIGN KEY (book)" +
          " REFERENCES documents(book), PRIMARY KEY(book, char));"
      )

      // Percorre a array e adiciona cada comando ao statement SQL na "Pilha"
      for (command <- criar) {
        rt = conn.prepareStatement(command)
        rt.execute()
      }

      // Fecha a conexão com o banco de dados
      conn.commit()
      rt.close()
      conn.close()
      true
      
    } catch {
      case e: SQLException => {
        print_error("Error while creating tables in database")
        e.printStackTrace()
        if (rt != null) rt.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }
  }
}

class Insert_Book(
    path_to_text: String,
    path_to_database: String,
    book_name: String
) extends Initialize(path_to_text: String, path_to_database: String) {

  execute

  def execute: Boolean = {

    // Criando um PreparedStatement
    var rt: PreparedStatement = null
    var conn: Connection = null
    try {

      // Estabelendo a conexão com o JDBC
      val conn = DriverManager.getConnection(url)

      // Convertendo a lista de linhas do livro em strings
      // separadas por espaço
      var text = lines.toString().split(" ")
      var update: String = ""
      var command: String = ""
      // Separando apenas as letras da string e convertendo em letra minúscula
      text = text.map(_.filter(_.isLetter))
      text = text.map(_.toLowerCase())
      text = text.sorted
      text = text.filter(s => s.size >= 2)

      // Iteração para adicionar palavra por palavra, caractere por caractere
      // Caso haja repeticao, incrementar a frequência

      // Desativando o autocommit
      conn.setAutoCommit(false)
      for (n <- 0 until text.length) {
        if (!(stop.contains(text(n)))) {
          for (i <- 0 until text(n).length) {
            update = "UPDATE OR IGNORE characters "
            update += "SET frequency = frequency + 1 WHERE char = "
            update += "'" + text(n)(i).toString() + "' and "
            update += "book = '" + book_name + "';"
            rt = conn.prepareStatement(update)
            rt.execute()
            command =
              "INSERT OR IGNORE INTO characters(book, char, frequency) VALUES ("
            command += "'" + book_name + "', "
            command += "'" + text(n)(i).toString() + "', "
            command += "'1');"
            rt = conn.prepareStatement(command)
            rt.execute()
          }

          update = "UPDATE OR IGNORE words "
          update += "SET frequency = frequency + 1 WHERE name = "
          update += "'" + text(n).toString() + "' and "
          update += "book = '" + book_name + "';"
          rt = conn.prepareStatement(update)
          rt.execute()

          command =
            "INSERT OR IGNORE INTO words(book, name, frequency) VALUES ("
          command += "'" + book_name + "', "
          command += "'" + text(n).toString() + "', "
          command += "'1');"
          rt = conn.prepareStatement(command)
          rt.execute()
        }
      }

      conn.commit()
      rt.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while inserting the book in the database")
        e.printStackTrace()
        if (rt != null) rt.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }
  }

}

class Select_Most_Frequent(
    path_to_text: String,
    path_to_database: String,
    book_name: String
) extends Initialize(path_to_text: String, path_to_database: String) {

  // Seleciona as 25 palavras mais frequentes
  def words: Boolean = {
    var conn: Connection = null
    var select: Statement = null
    try {
      conn = DriverManager.getConnection(url)

      // Criando um statement SQL
      select = conn.createStatement()

      // Comando para ordenar as palavras por ordem de frequência
      var command = "SELECT w.name, w.frequency, COUNT(*) as frequency "
      command += s"FROM words w INNER JOIN documents d ON d.book = w.book WHERE w.book = '${book_name}' "
      command += "GROUP BY name ORDER BY CAST(frequency AS int) DESC"
      // Coloca para executar a query
      val rs = select.executeQuery(command)
      var break: Int = 0
      // Pega todos os resultados da Query
      while (rs.next() && (break != 25)) {
        var name = rs.getString("name")
        var frequency = rs.getInt("frequency")
        println(s"$name has appeared $frequency times.")
        break += 1
      }

      // Fecha a conexão com o banco de dados
      select.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while selecting words")
        e.printStackTrace()
        if (select != null) select.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }

    }
  }

  // Seleciona os 25 caracteres mais frequentes
  def characters: Boolean = {
    var select: Statement = null
    var conn: Connection = null
    try {
      conn = DriverManager.getConnection(url)

      // Criando um statement SQL
      select = conn.createStatement()

      // Comando para ordenar as palavras por ordem de frequência
      var command = "SELECT c.char,c.frequency, COUNT(*) as frequency "
      command += s"FROM characters as c INNER JOIN documents as d ON d.book = c.book WHERE c.book = '${book_name}' "
      command += "GROUP BY char ORDER BY CAST(frequency AS int) DESC"
      // Coloca para executar a query
      val rs = select.executeQuery(command)
      var break: Int = 0
      // Pega todos os resultados da Query
      while (rs.next() && (break != 25)) {
        var char = rs.getString("char")
        var frequency = rs.getInt("frequency")
        println(s"$char has appeared $frequency times.")
        break += 1
      }

      select.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while selecting characters")
        e.printStackTrace()
        if (select != null) select.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }

  }
}
