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

class Initialize(path_to_text: String, path_to_database: String) {
  val url = s"jdbc:sqLite:$path_to_database"
  val book = scala.io.Source.fromFile(path_to_text)
  val stoptxt = scala.io.Source.fromFile("src/main/scala/files/stop-words.txt")
  val lines: List[String] = book.getLines().toList
  val stopwords: List[String] = stoptxt.getLines().toList
  var stop = stopwords.toString().split(" ")
  var text = lines.toString().split(" ")
  text = text.map(_.replace("List(", ""))
  text = text.map(_.filter(_.isLetter))
  text = text.map(_.toLowerCase())
  text = text.sorted
  text = text.filter(s => s.size >= 2)
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

  def execute: Boolean = {

    // Criando um PreparedStatement
    var rt: PreparedStatement = null
    var conn: Connection = null
    try {

      // Estabelendo a conexão com o JDBC
      val conn = DriverManager.getConnection(url)

      // Convertendo a lista de linhas do livro em strings
      // separadas por espaço
      var update: String = ""
      var command: String = ""
      // Separando apenas as letras da string e convertendo em letra minúscula
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
    book_name: String,
    limit: Int
) extends Initialize(path_to_text: String, path_to_database: String) {

  // Seleciona as 25 palavras mais frequentes
  def get_words: List[(String, Int)] = {
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
      var break = 0

      // Pega todos os resultados da Query
      var wordFrequency: List[(String, Int)] = List()
      while (rs.next() && (break != limit)) {
        var name = rs.getString("name")
        var frequency = rs.getInt("frequency")
        wordFrequency = wordFrequency :+ (name, frequency)
        break += 1
      }

      // Fecha a conexão com o banco de dados
      select.close()
      conn.close()

      wordFrequency
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
  def get_characters: List[(String, Int)] = {
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
      var characterFrequency: List[(String, Int)] = List()
      while (rs.next() && (break != limit)) {
        var char = rs.getString("char")
        var frequency = rs.getInt("frequency")
        characterFrequency = characterFrequency :+ (char, frequency)
        break += 1
      }

      select.close()
      conn.close()

      characterFrequency
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

  def get_longest: String = {
    var select: Statement = null
    var conn: Connection = null
    try {
      conn = DriverManager.getConnection(url)

      // Criando um statement SQL
      select = conn.createStatement()

      // Comando para ordenar as palavras por ordem de frequência
      var command =
        s"SELECT longest_word FROM documents as d WHERE d.book = '${book_name}' "

      // Coloca para executar a query
      val rs = select.executeQuery(command)
      var break: Int = 0

      // Pega todos os resultados da Query
      var longest = rs.getString("longest_word")

      select.close()
      conn.close()

      longest
    } catch {
      case e: SQLException => {
        print_error("Error while selecting the longest word")
        e.printStackTrace()
        if (select != null) select.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }
  }
}


class Register_Documents(
    path_to_text: String,
    path_to_database: String,
    book_name: String
) extends Initialize(path_to_text: String, path_to_database: String) {

  def register: Boolean = {

    var rt: PreparedStatement = null
    var conn: Connection = null

    try {
      conn = DriverManager.getConnection(url)

      // Desativando o autocommit
      conn.setAutoCommit(false)
      var register: String = ""
      register = "INSERT OR IGNORE INTO documents(book) VALUES("
      register += "'" + book_name + "');"
      rt = conn.prepareStatement(register)
      rt.execute()
      conn.commit()
      rt.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while registering into documents table")
        e.printStackTrace()
        if (rt != null) rt.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }
  }

  def check_register: Boolean = {
    var select: Statement = null
    var conn: Connection = null

    try {
      conn = DriverManager.getConnection(url)

      // Criando um statement SQL
      select = conn.createStatement()

      // Comando para ordenar as palavras por ordem de frequência
      var command =
        s"SELECT COUNT(*) as count FROM documents WHERE book = '${book_name}';"

      // Coloca para executar a query
      val rs = select.executeQuery(command)
      var exists: Int = rs.getInt("count")
      if (exists != 0) {
        select.close()
        conn.close()
        return true
      }
      // Fecha a conexão com o banco de dados
      select.close()
      conn.close()
      return false
    } catch {
      case e: SQLException => {
        print_error("Error while checking register")
        e.printStackTrace()
        if (select != null) select.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }

  }

  def count_words: Boolean = {
    var conn: Connection = null
    var rt: PreparedStatement = null
    var select_all: Statement = null
    try {
      val conn = DriverManager.getConnection(url)
      conn.setAutoCommit(false)
      val select_all = conn.createStatement()

      var command =
        s"SELECT COUNT(name) FROM words WHERE book = '${book_name}';"
      val rs = select_all.executeQuery(command)

      val count: Integer = rs.getInt(1)
      command =
        s"UPDATE OR IGNORE documents SET num_words = ${count} WHERE book = '${book_name}';"

      rt = conn.prepareStatement(command)
      rt.execute()
      conn.commit()
      rt.close()
      select_all.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while counting words")
        e.printStackTrace()
        if (select_all != null) select_all.close()
        if (conn != null) conn.close()
        if (rt != null) rt.close()
        sys.exit(1)
      }
    }

  }

  def count_chars: Boolean = {
    var rt: PreparedStatement = null
    var conn: Connection = null
    var select_all: Statement = null

    try {
      conn = DriverManager.getConnection(url)
      conn.setAutoCommit(false)
      val select_all = conn.createStatement()
      var command =
        s"SELECT COUNT(char) from characters WHERE book = '${book_name}';"
      val rs = select_all.executeQuery(command)
      // rs.next()
      val count: Integer = rs.getInt(1)
      command =
        s"UPDATE OR IGNORE documents SET num_char = ${count} WHERE book = '${book_name}';"
      rt = conn.prepareStatement(command)
      rt.execute()
      rt.close()
      conn.commit()
      select_all.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while counting characters")
        e.printStackTrace()
        if (select_all != null) select_all.close()
        if (conn != null) conn.close()
        if (rt != null) rt.close()
        sys.exit(1)
      }
    }

  }

  def avg_char_word: Boolean = {
    var rt: PreparedStatement = null
    var conn: Connection = null

    try {
      conn = DriverManager.getConnection(url)
      conn.setAutoCommit(false)
      var command =
        s"UPDATE OR IGNORE documents SET avg_char_word = num_char/num_words where book='${book_name}';"
      rt = conn.prepareStatement(command)
      rt.execute()
      conn.commit()
      rt.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while getting average characters per word")
        e.printStackTrace()
        if (rt != null) rt.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }

  }

  def longest_word: Boolean = {
    var rt: PreparedStatement = null
    var conn: Connection = null
    var select_longest: Statement = null

    try {
      conn = DriverManager.getConnection(url)
      conn.setAutoCommit(false)
      select_longest = conn.createStatement()
      var command =
        s"SELECT name FROM words WHERE book = '${book_name}' ORDER BY length(name) DESC;"
      val rs = select_longest.executeQuery(command)
      val longest_word: String = rs.getString(1)
      command =
        s"UPDATE OR IGNORE documents SET longest_word = '${longest_word}' WHERE book = '${book_name}';"
      rt = conn.prepareStatement(command)
      rt.execute()
      rt.close()
      conn.commit()
      select_longest.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while getting longest word")
        e.printStackTrace()
        if (rt != null) rt.close()
        if (select_longest != null) select_longest.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }
  }

  def length_25: Boolean = {
    var rt: PreparedStatement = null
    var conn: Connection = null

    try {
      conn = DriverManager.getConnection(url)
      conn.setAutoCommit(false)
      var command =
        s"UPDATE OR IGNORE documents SET length_25 = (SELECT sum(len) " +
          s"as total_length from (select length(name) as len from words where book = '${book_name}' limit 25)) " +
          s"where book = '${book_name}';"
      rt = conn.prepareStatement(command)
      rt.execute()
      conn.commit()
      rt.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while selecting length of 25 most frequent words")
        e.printStackTrace()
        if (rt != null) rt.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }
  }
}


//Classe para exportar words e characters em arquivos csv
class Export_to_CSV(
    path_to_text: String,
    path_to_database: String,
    book_name: String,
    export_folder: String 
) extends Initialize(path_to_text: String, path_to_database: String) {

  def export_words: Boolean = {
    var Export: PrintWriter = null
    var conn: Connection = null
    var rt: PreparedStatement = null
    try {
      Export = new PrintWriter(
        new File(
          export_folder + book_name + "-words.csv"
        )
      )
      var sb: StringBuilder = new StringBuilder()

      // Estabelendo a conexão com o JDBC
      val conn = DriverManager.getConnection(url)
      // Desativando o autocommit
      conn.setAutoCommit(false)

      var query: String = "SELECT *, COUNT(*) as frequency "
      query += s"FROM words where book = '${book_name}' GROUP BY name ORDER BY CAST(frequency AS int) DESC"
      rt = conn.prepareStatement(query)
      var rs = rt.executeQuery()
      sb.append("name" + "," + "frequency" + "\r\n")
      while (rs.next()) {
        sb.append(rs.getString("name"))
        sb.append(",")
        sb.append(rs.getString("frequency"))
        sb.append("\r\n")
      }

      Export.write(sb.toString())
      Export.close()
      conn.close()
      rt.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while exporting words to CSV")
        e.printStackTrace()
        if (rt != null) rt.close()
        if (Export != null) Export.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }

  }

  def export_characters: Boolean = {
    var Export: PrintWriter = null
    var conn: Connection = null
    var rt: PreparedStatement = null

    try {
      Export = new PrintWriter(
        new File(
          export_folder + book_name + "-characters.csv"
        )
      )
      var sb: StringBuilder = new StringBuilder()

      // Estabelendo a conexão com o JDBC
      conn = DriverManager.getConnection(url)
      // Desativando o autocommit
      conn.setAutoCommit(false)

      var query: String = "SELECT *, COUNT(*) as frequency "
      query += s"FROM characters where book = '${book_name}' GROUP BY char ORDER BY CAST(frequency AS int) DESC"
      rt = conn.prepareStatement(query)
      var rs = rt.executeQuery()
      sb.append("character" + "," + "frequency" + "\r\n")
      while (rs.next()) {
        sb.append(rs.getString("char"))
        sb.append(",")
        sb.append(rs.getString("frequency"))
        sb.append("\r\n")
      }

      Export.write(sb.toString())
      Export.close()
      rt.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while exporting characters to CSV")
        e.printStackTrace()
        if (rt != null) rt.close()
        if (Export != null) Export.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }
  }

  def export_data: Boolean = {
    var rt: PreparedStatement = null
    var conn: Connection = null
    var Export: PrintWriter = null

    try {
      Export = new PrintWriter(
        new File(export_folder + "/documents.csv")
      )
      var sb: StringBuilder = new StringBuilder()

      // Estabelendo a conexão com o JDBC
      conn = DriverManager.getConnection(url)
      // Desativando o autocommit
      conn.setAutoCommit(false)

      var query: String = "SELECT * "
      query += s"FROM documents"
      rt = conn.prepareStatement(query)
      var rs = rt.executeQuery()
      sb.append(
        "book, num_words, num_char, avg_char_word, longest_word, length_25\r\n"
      )
      while (rs.next()) {
        sb.append(rs.getString("book"))
        sb.append(",")
        sb.append(rs.getString("num_words"))
        sb.append(",")
        sb.append(rs.getString("num_char"))
        sb.append(",")
        sb.append(rs.getString("avg_char_word"))
        sb.append(",")
        sb.append(rs.getString("longest_word"))
        sb.append(",")
        sb.append(rs.getString("length_25"))
        sb.append("\r\n")
      }

      Export.write(sb.toString())
      Export.close()
      rt.close()
      conn.close()
      true
    } catch {
      case e: SQLException => {
        print_error("Error while exporting 'documents' to CSV")
        e.printStackTrace()
        if (rt != null) rt.close()
        if (Export != null) Export.close()
        if (conn != null) conn.close()
        sys.exit(1)
        false
      }
    }
  }
}
