package sqlite3
import java.sql.Connection
import java.sql.Date
import java.sql.SQLException
import java.sql.DriverManager
import java.sql.Statement
import scala.io.Source
import java.sql.ResultSet

//Trait para obter o caminho do banco de dados
//e conectar com o driver jdbc
trait Conexao{
    val path = "src/main/scala/sqlite3/test.db"
    val url = s"jdbc:sqLite:$path"
}

//Trait para obter o caminho do livro em formato txt
//Lines retorna todas as linhas do livro em uma lista de Strings
trait ReadFile{
    val file = "src/main/scala/texts/short.txt"
    val book = scala.io.Source.fromFile(file)
    val lines: List[String] = book.getLines().toList
}

//Classe pra inicializar o banco de dados com as tabelas necessárias
class CreateTables extends Conexao with ReadFile{
    
    //Estabelendo a conexão com o JDBC
    val conn = DriverManager.getConnection(url)

    //Criando um statement SQL
    val statement = conn.createStatement()

    //Array que contém os comandos SQL de criação das tabelas
    val criar = Array(
        "CREATE TABLE IF NOT EXISTS documents (id INTEGER PRIMARY KEY AUTOINCREMENT,name);",
        "CREATE TABLE IF NOT EXISTS words(name TEXT PRIMARY KEY, frequency INTEGER);",
        "CREATE TABLE IF NOT EXISTS characters(char TEXT PRIMARY KEY, frequency INTEGER);"
    )

    //Percorre a array e adiciona cada comando ao statement SQL na "Pilha"
    for(comando <- criar){
        statement.addBatch(comando)
    }

    //Executa o statement e fecha a conexão com o banco de dados
    statement.executeBatch()
    statement.clearBatch()
    statement.close()
    conn.close()
}

class Insert_Words extends Conexao with ReadFile{

    //Estabelendo a conexão com o JDBC
    val conn = DriverManager.getConnection(url)

    //Criando um statement SQL
    val insert = conn.createStatement()
    val char = conn.createStatement()

    //Convertendo a lista de linhas do livro em strings
    //separadas por espaço
    var text = lines.toString().split(" ")
    var update:String = ""
    var command: String = ""
    
    //Separando apenas as letras da string e convertendo em letra minúscula
    text = text.map(_.filter(_.isLetter))
    text = text.map(_.toLowerCase())
    text = text.sorted
    //Iteração para adicionar palavra por palavra
    //Caso haja palavras repetidas, incrementar a frequência

    for(n<-0 until text.length){
            for(i<-0 until text(n).length){
                update = "UPDATE OR IGNORE characters "
                update += "SET frequency = frequency + 1 WHERE char = "
                update += "'" + text(n)(i).toString() + "';"
                char.addBatch(update)
                command = "INSERT OR IGNORE INTO characters(char, frequency) VALUES ("
                command += "'" + text(n)(i).toString() + "', "
                command += "'1');" 
                char.addBatch(command)
            }
            char.executeBatch()
            char.clearBatch()

            update = "UPDATE OR IGNORE words "
            update += "SET frequency = frequency + 1 WHERE name = "
            update += "'" + text(n).toString() + "';"
            insert.addBatch(update)
            command = "INSERT OR IGNORE INTO words(name, frequency) VALUES ("
            command += "'" + text(n).toString() + "', "
            command += "'1');" 
            insert.addBatch(command)
        }

    //Executa o statement e fecha a conexão com o banco de dados     
    insert.executeBatch()
    char.clearBatch()
    insert.clearBatch()
    insert.close()
    char.close()
    conn.close()
}

class Select_Words extends Conexao with ReadFile{
     //Estabelendo a conexão com o JDBC
    val conn = DriverManager.getConnection(url)

    //Criando um statement SQL
    val select = conn.createStatement()

    //Comando para ordenar as palavras por ordem de frequência
    var command = "SELECT name,frequency, COUNT(*) as frequency "
    command += "FROM words GROUP BY name ORDER BY CAST(frequency AS int) DESC"

    //Coloca para executar a query
    val rs = select.executeQuery(command)
    
    //Pega todos os resultados da Query
    while(rs.next()){
        var name = rs.getString("name")
        var frequency = rs.getInt("frequency")
        println(s"$name has appeared $frequency times.")
    }

    //Fecha a conexão com o banco de dados     
    select.close()
    conn.close()
}