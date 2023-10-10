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
        s"UPDATE OR IGNORE documents SET avg_char_word = num_words/num_char where book='${book_name}';"
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
