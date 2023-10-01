//Ideias para teste unitário

// import org.scalatest.flatspec.AnyFlatSpec
// import org.scalatest.matchers.should.Matchers

// class WordCountServiceSpec extends AnyFlatSpec with Matchers {
//   val dbPath = "caminho/para/seu/banco-de-dados.db"

//   "WordCountService" should "contar corretamente as palavras" in {
//     val wordCountService = new WordCountService(dbPath)

//     val text = "Olá, mundo! Olá, Scala!"
//     val expectedWordCounts = Map("olá" -> 2, "mundo" -> 1, "scala" -> 1)

//     val actualWordCounts = wordCountService.wordCount(text)

//     actualWordCounts shouldEqual expectedWordCounts
//   }

//   it should "salvar os resultados no banco de dados" in {
//     val wordCountService = new WordCountService(dbPath)
//     val wordCounts = Map("scala" -> 3, "test" -> 2)

//     wordCountService.saveWordCounts(wordCounts)

//     // Verifique se os resultados foram salvos corretamente no banco de dados
//     // Implemente essa verificação de acordo com a sua estrutura de banco de dados
//     // Exemplo: Consultar o banco de dados para verificar se as contagens foram salvas corretamente
//   }

//   it should "fechar a conexão com o banco de dados" in {
//     val wordCountService = new WordCountService(dbPath)

//     // Feche a conexão
//     wordCountService.closeConnection()

//     // Verifique se a conexão foi fechada corretamente
//     // Implemente essa verificação de acordo com a sua estrutura de banco de dados
//     // Exemplo: Verificar se a conexão está fechada (connection.isClosed)
//   }
// }