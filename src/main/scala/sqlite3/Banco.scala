package sqlite3

import java.sql.Connection
import java.sql.SQLException
import java.sql.DriverManager
import java.sql.Statement
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.io.PrintWriter
import java.io.File
import scala.io.Source
import components.Interaction

//Trait para obter o caminho do banco de dados
//e conectar com o driver jdbc
class Initialize(path_to_text: String, path_to_database: String){
    val url = s"jdbc:sqLite:$path_to_database"
    val book = scala.io.Source.fromFile(path_to_text)
    val stoptxt = scala.io.Source.fromFile("src/main/scala/files/stop-words.txt")
    val lines: List[String] = book.getLines().toList
    val stopwords: List[String] = stoptxt.getLines().toList
}

//Classe pra inicializar o banco de dados com as tabelas necessárias
class CreateTables(path_to_text: String, path_to_database: String) extends Initialize(path_to_text: String, path_to_database: String){
    
    //Estabelendo a conexão com o JDBC
    val conn = DriverManager.getConnection(url)

    //Desativando o autocommit
    conn.setAutoCommit(false)

    //Criando um PreparedStatement SQL

    var rt: PreparedStatement = null

    //Array que contém os comandos SQL de criação das tabelas
    val criar = Array(
        "CREATE TABLE IF NOT EXISTS documents (id INTEGER PRIMARY KEY AUTOINCREMENT,name);",
        "CREATE TABLE IF NOT EXISTS words(name TEXT PRIMARY KEY, frequency INTEGER);",
        "CREATE TABLE IF NOT EXISTS characters(char TEXT PRIMARY KEY, frequency INTEGER);"
    )

    //Percorre a array e adiciona cada comando ao statement SQL na "Pilha"
    for(command <- criar){
        rt = conn.prepareStatement(command)
        rt.execute()
    }

    //Fecha a conexão com o banco de dados
    rt.close()
    conn.commit()
    conn.close()
}

class Insert_Words(path_to_text: String, path_to_database: String) extends Initialize(path_to_text: String, path_to_database: String){

    //Estabelendo a conexão com o JDBC
    val conn = DriverManager.getConnection(url)

    //Convertendo a lista de linhas do livro em strings
    //separadas por espaço
    var text = lines.toString().split(" ")
    var update:String = ""
    var command: String = ""
    //Separando apenas as letras da string e convertendo em letra minúscula
    text = text.map(_.filter(_.isLetter))
    text = text.map(_.toLowerCase())
    text = text.sorted
    text = text.filter(s => s.size >= 2)

    //Iteração para adicionar palavra por palavra, caractere por caractere
    //Caso haja repeticao, incrementar a frequência

    //Criando um PreparedStatement
    var rt: PreparedStatement = null

    //Desativando o autocommit
    conn.setAutoCommit(false)

    for(n<-0 until text.length){
            if(!stopwords.contains(text(n))){
                for(i<-0 until text(n).length){
                    update = "UPDATE OR IGNORE characters "
                    update += "SET frequency = frequency + 1 WHERE char = "
                    update += "'" + text(n)(i).toString() + "';"
                    rt = conn.prepareStatement(update)
                    rt.execute()
                    command = "INSERT OR IGNORE INTO characters(char, frequency) VALUES ("
                    command += "'" + text(n)(i).toString() + "', "
                    command += "'1');" 
                    rt = conn.prepareStatement(command)
                    rt.execute()
                }
    
                update = "UPDATE OR IGNORE words "
                update += "SET frequency = frequency + 1 WHERE name = "
                update += "'" + text(n).toString() + "';"
                rt = conn.prepareStatement(update)
                rt.execute()
                
                command = "INSERT OR IGNORE INTO words(name, frequency) VALUES ("
                command += "'" + text(n).toString() + "', "
                command += "'1');" 
                rt = conn.prepareStatement(command)
                rt.execute()
            }
        }

    //Executa o statement e fecha a conexão com o banco de dados     
    rt.close()
    conn.commit()
    conn.close()
}
class Select_Most_Frequent(path_to_text: String, path_to_database: String) extends Initialize(path_to_text: String, path_to_database: String){
    
    def words: Unit = {
        val conn = DriverManager.getConnection(url)

        //Criando um statement SQL
        val select = conn.createStatement()

        //Comando para ordenar as palavras por ordem de frequência
        var command = "SELECT name,frequency, COUNT(*) as frequency "
        command += "FROM words GROUP BY name ORDER BY CAST(frequency AS int) DESC"

        //Coloca para executar a query
        val rs = select.executeQuery(command)
        var break: Int = 0
        //Pega todos os resultados da Query
        while(rs.next() && (break!=100)){
            var name = rs.getString("name")
            var frequency = rs.getInt("frequency")
            println(s"$name has appeared $frequency times.")
            break += 1
        }

        //Fecha a conexão com o banco de dados     
        select.close()
        conn.close()
    }

    def characters: Unit = {
        val conn = DriverManager.getConnection(url)

        //Criando um statement SQL
        val select = conn.createStatement()

        //Comando para ordenar as palavras por ordem de frequência
        var command = "SELECT char,frequency, COUNT(*) as frequency "
        command += "FROM characters GROUP BY char ORDER BY CAST(frequency AS int) DESC"

        //Coloca para executar a query
        val rs = select.executeQuery(command)
        var break: Int = 0
        //Pega todos os resultados da Query
        while(rs.next() && (break!=100)){
            var char = rs.getString("char")
            var frequency = rs.getInt("frequency")
            println(s"$char has appeared $frequency times.")
            break += 1
        }

        //Fecha a conexão com o banco de dados     
        select.close()
        conn.close()
        }
}

class Export_to_CSV (path_to_text: String, path_to_database: String, book_name: String) extends Initialize(path_to_text: String, path_to_database: String){
    export_words
    export_characters

    def export_words: Unit = {
        var Export = new PrintWriter(new File("src/main/scala/spreadsheets/" + book_name + "-words.csv"))
        var sb: StringBuilder = new StringBuilder()

        //Estabelendo a conexão com o JDBC
        val conn = DriverManager.getConnection(url)
        //Desativando o autocommit
        conn.setAutoCommit(false)

        var query: String = "SELECT *, COUNT(*) as frequency "
        query += "FROM words GROUP BY name ORDER BY CAST(frequency AS int) DESC"
        var rt: PreparedStatement = conn.prepareStatement(query)
        var rs = rt.executeQuery()
        sb.append("name" + "," + "frequency" + "\r\n")
        while(rs.next()){
            sb.append(rs.getString("name"))
            sb.append(",")
            sb.append(rs.getString("frequency"))
            sb.append("\r\n")
        }

        Export.write(sb.toString())
        Export.close()
    }
    
    def export_characters: Unit = {
        var Export = new PrintWriter(new File("src/main/scala/spreadsheets/" + book_name + "-characters.csv"))
        var sb: StringBuilder = new StringBuilder()

        //Estabelendo a conexão com o JDBC
        val conn = DriverManager.getConnection(url)
        //Desativando o autocommit
        conn.setAutoCommit(false)

        var query: String = "SELECT *, COUNT(*) as frequency "
        query += "FROM characters GROUP BY char ORDER BY CAST(frequency AS int) DESC"
        var rt: PreparedStatement = conn.prepareStatement(query)
        var rs = rt.executeQuery()
        sb.append("character" + "," + "frequency" + "\r\n")
        while(rs.next()){
            sb.append(rs.getString("char"))
            sb.append(",")
            sb.append(rs.getString("frequency"))
            sb.append("\r\n")
        }

        Export.write(sb.toString())
        Export.close()
    }
    
}