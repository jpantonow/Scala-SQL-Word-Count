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
class CreateTables(path_to_text: String, path_to_database: String, book_name: String) extends Initialize(path_to_text: String, path_to_database: String){
    
    //Estabelendo a conexão com o JDBC
    val conn = DriverManager.getConnection(url)

    //Desativando o autocommit
    conn.setAutoCommit(false)

    //Criando um PreparedStatement SQL

    var rt: PreparedStatement = null

    //Array que contém os comandos SQL de criação das tabelas
    val criar = Array(
        "CREATE TABLE IF NOT EXISTS documents (book TEXT PRIMARY KEY, num_words INTEGER, " +
        "num_char INTEGER, avg_char_word INTEGER, longest_word TEXT, lenght_25 INTEGER);",
        "CREATE TABLE IF NOT EXISTS words(book TEXT , name TEXT, frequency INTEGER, FOREIGN KEY (book)" +
        " REFERENCES documents(book), PRIMARY KEY(book, name));",
        "CREATE TABLE IF NOT EXISTS characters(book TEXT , char TEXT, frequency INTEGER, FOREIGN KEY (book)" +
        " REFERENCES documents(book), PRIMARY KEY(book, char));"
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

class Insert_Words(path_to_text: String, path_to_database: String, book_name: String) extends Initialize(path_to_text: String, path_to_database: String){

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
                    update += "'" + text(n)(i).toString() + "' and "
                    update += "book = '" + book_name + "';"
                    rt = conn.prepareStatement(update)
                    rt.execute()
                    command = "INSERT OR IGNORE INTO characters(book, char, frequency) VALUES ("
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
                
                command = "INSERT OR IGNORE INTO words(book, name, frequency) VALUES ("
                command += "'" + book_name + "', "
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
class Select_Most_Frequent(path_to_text: String, path_to_database: String, book_name: String) extends Initialize(path_to_text: String, path_to_database: String){
    
    def words: Unit = {
        val conn = DriverManager.getConnection(url)

        //Criando um statement SQL
        val select = conn.createStatement()

        //Comando para ordenar as palavras por ordem de frequência
        var command = "SELECT words.name as name,words.frequency as frequency, COUNT(*) as frequency "
        command += s"FROM words as words INNER JOIN documents as documents ON documents.book = words.book WHERE words.book as words = '${book_name}' "
        command += "GROUP BY words.name ORDER BY CAST(words.frequency AS int) DESC"
        println(command)
        //Coloca para executar a query
        val rs = select.executeQuery(command)
        var break: Int = 0
        //Pega todos os resultados da Query
        while(rs.next() && (break!=25)){
            var name = rs.getString("words.name")
            var frequency = rs.getInt("words.frequency")
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
        var command = "SELECT characters.char as char,characters.frequency as frequency, COUNT(*) as frequency "
        command += s"FROM characters INNER JOIN documents on documents.book = characters.book WHERE characters.book = '${book_name}' "
        command += "GROUP BY char ORDER BY CAST(frequency AS int) DESC"
        
        //Coloca para executar a query
        val rs = select.executeQuery(command)
        var break: Int = 0
        //Pega todos os resultados da Query
        while(rs.next() && (break!=25)){
            var char = rs.getString("characters.char")
            var frequency = rs.getInt("characters.frequency")
            println(s"$char has appeared $frequency times.")
            break += 1
        }

        //Fecha a conexão com o banco de dados     
        select.close()
        conn.close()
        }
}

class Register_Documents(path_to_text: String, path_to_database: String, book_name: String) extends Initialize(path_to_text: String, path_to_database: String){
    register
    count_words

    def register: Unit = {
        val conn = DriverManager.getConnection(url)
        //Criando um PreparedStatement
        var rt: PreparedStatement = null
        //Desativando o autocommit
        conn.setAutoCommit(false)
        var register: String = ""
        register = "INSERT OR IGNORE INTO documents(book) VALUES("
        register += "'" + book_name + "');"
        rt = conn.prepareStatement(register)
        rt.execute()   
        rt.close()
        conn.commit()
        conn.close()
    }
    def count_words: Unit = {
        var rt: PreparedStatement = null
        val conn = DriverManager.getConnection(url)
        conn.setAutoCommit(false)
        val select_all = conn.createStatement()
        var command = "SELECT COUNT(name) from words;"
        val rs = select_all.executeQuery(command)
        //rs.next()
        val count: Integer = rs.getInt(1)
        command = s"UPDATE OR IGNORE documents SET num_words = ${count} WHERE book = '${book_name}';"
        rt = conn.prepareStatement(command)
        rt.execute()
        rt.close()
        conn.commit()
        select_all.close()
        conn.close()
    }
}
//Classe para exportar words e characters em arquivos csv
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
