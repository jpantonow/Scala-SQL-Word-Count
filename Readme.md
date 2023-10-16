# Repositório para Scala Word Count - Persistent Tables - Grupo 03

## Alunos:
### João Pedro Gomes Covaleski Marin Antonow - 221006351
### Willyan Marques de Melo - 221020940
### Vitor Guedes Frade - 221017130
### Saulo Oliveira de Freitas - 211000176
### Thiago Veras Rodrigues Queiroz - 211055370

## Video-aula:
### Link para vídeo-aula: https://bit.ly/tp2-persistent-tables

## Manual:
### - Ter sqlite3 instalado em seu sistema operacional.
### - Ter scala 2.13.6 ou superior instalado em seu sistema operacional.
### - Vscode: Baixar a extensão metals.
### - Terminal: instalar sbt 1.9.6 em seu sistema operacional.
### Observações: 

- O código foi testado com êxito em ambientes linux e windows devidamente configurados.

- Durante o processo de compilação/execução, verifique as versões existentes em seu sistema do sbt e do scala, a fim de que não haja incompatibilidade das mesmas.

## Projeto:
### Package componentes: Serve de apoio aos packets secundários de interação com o usuário e de interação com o banco de dados
#### Components.commands: Packages de interação com o usuário
##### Interaction: Trait para definir mecanismos de interação com as files e com o usuário.
##### WordCount: Classe que herda de Interaction; tem como intuito principal definir métodos que irão executar o algoritmo de Word-Count, que se trata, no presente trabalho, de inserir os registros necessários apenas se eles não estiverem presentes no banco de dados.

#### Components.db: Package de banco de dados
##### Db: Conjunto de classes que definem os métodos de interação com o banco de dados, os quais possuem tratamento de exceção e parâmetros adequados para se relacionarem com os livros e com os arquivos csv necessários.
## User Guide:
### - Coloque manualmente seu livro desejado em formato txt na pasta "src/main/scala/files/books/"
### - Para a aquisição de livros, a sugestão é via livraria virtual presente em https://www.gutenberg.org/
### - Pressione "run" no programa Main se estiver usando Vscode-Metals
### - Via terminal, digite o comando sbt compile run

## Testes: 
### - Primeiramente, sugere-se a limpeza das bases de dados (.db), com posterior limpeza das pastas spreadsheets
### - DbTest - Focado nos funcionamentos simples do banco de dados, com funcionamento de funções básicas, sem se preocupar com o algoritmo de duplicidade de termos.
### - WordCountTest - Focado no funcionamento do WordCount, testagem das multiplicidades de termos e da checagem da existência dos mesmos.
### - Para executar os testes unitários, primeiramente digite sbt compile
### - Posteriormente, digite sbt clean test
=======
