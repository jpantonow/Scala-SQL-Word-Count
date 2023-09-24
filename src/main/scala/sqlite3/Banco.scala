package sqlite3
import java.sql.Connection
import java.sql.Date
import java.sql.SQLException
import java.sql.DriverManager
import java.sql.Statement
import scala.io.Source

//Trait para obter o caminho do banco de dados
//e conectar com o driver jdbc
trait Conexao{
    val path = "src/main/scala/sqlite3/short_test.db"
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
        "CREATE TABLE IF NOT EXISTS characters(name TEXT PRIMARY KEY, frequency INTEGER);"
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

    //Convertendo a lista de linhas do livro em strings
    //separadas por espaço
    var text = lines.toString().split(" ")
    var update:String = ""
    var command: String = ""
    
    //Separando apenas as letras da string e convertendo em letra minúscula
    text = text.map(_.filter(_.isLetter))
    text = text.map(_.toLowerCase())

    //Iteração para adicionar palavra por palavra
    //Caso haja palavras repetidas, incrementar a frequência

    for(n<-0 until text.length){
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
    insert.clearBatch()
    insert.close()
    conn.close()
}
