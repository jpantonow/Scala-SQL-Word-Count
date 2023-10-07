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

//Classe para exportar words e characters em arquivos csv
class Export_to_CSV(
    path_to_text: String,
    path_to_database: String,
    book_name: String
) extends Initialize(path_to_text: String, path_to_database: String) {
  export_words
  export_characters
  export_data

  def export_words: Unit = {
    var Export: PrintWriter = null
    var conn: Connection = null
    var rt: PreparedStatement = null

    try {
      Export = new PrintWriter(
        new File(
          "src/main/scala/files/spreadsheets/" + book_name + "-words.csv"
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

  def export_characters: Unit = {
    var Export: PrintWriter = null
    var conn: Connection = null
    var rt: PreparedStatement = null

    try {
      Export = new PrintWriter(
        new File(
          "src/main/scala/files/spreadsheets/" + book_name + "-characters.csv"
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

  def export_data: Unit = {
    var rt: PreparedStatement = null
    var conn: Connection = null
    var Export: PrintWriter = null

    try {
      Export = new PrintWriter(
        new File("src/main/scala/files/spreadsheets/documents.csv")
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
    } catch {
      case e: SQLException => {
        print_error("Error while exporting 'documents' to CSV")
        e.printStackTrace()
        if (rt != null) rt.close()
        if (Export != null) Export.close()
        if (conn != null) conn.close()
        sys.exit(1)
      }
    }
  }
}
