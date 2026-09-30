# Loja de Jogos Digitais e Produtoras

**Aluno:** Victor Hugo Nespoli Ferreira  
**Disciplina:** Programação Orientada a Objetos em Java  
**Tema:** Desenvolvedora (1) e Jogo (N)

Projeto didático de console com Java, JDBC e PostgreSQL. Uma desenvolvedora pode
ter vários jogos, e cada jogo pertence a uma única desenvolvedora.

## 1. Tecnologias e requisitos

- JDK 17 ou superior, com `java` e `javac` disponíveis.
- Apache Maven 3.9 ou superior, com `mvn` disponível.
- PostgreSQL instalado e em execução; pode ser administrado pelo pgAdmin ou pelo `psql`.
- Driver JDBC PostgreSQL 42.7.13, baixado automaticamente pelo Maven.
- Acesso à internet na primeira compilação para baixar as dependências do Maven.

Confira as instalações:

```bash
java -version
javac -version
mvn -version
```

O projeto usa JDBC diretamente:

## 2. Organização

| Arquivo | Responsabilidade |
|---|---|
| `src/main/java/model/Desenvolvedora.java` | Entidade do lado 1; encapsulamento e validações. |
| `src/main/java/model/Jogo.java` | Entidade do lado N; possui `Desenvolvedora desenvolvedora`. |
| `src/main/java/dao/DesenvolvedoraDAO.java` | CRUD de desenvolvedoras. |
| `src/main/java/dao/JogoDAO.java` | CRUD de jogos e consultas com `INNER JOIN`. |
| `src/main/java/util/ConnectionFactory.java` | Carregamento do driver e abertura das conexões. |
| `src/main/java/app/Main.java` | Demonstração e verificações funcionais no console. |
| `sql/01_criar_banco.sql` | Criação do banco `poo_exercicios`. |
| `sql/02_criar_tabelas.sql` | Tabelas, restrições e índice da chave estrangeira. |
| `database.properties.example` | Modelo de configuração sem credenciais reais. |
| `pom.xml` | Configuração Maven e dependência JDBC. |
| `.gitignore` | Exclusão de compilados, arquivos de IDEs e credenciais locais. |

Os pacotes `model`, `dao`, `util` e `app` estão separados. As classes de modelo
não executam SQL; os DAOs não imprimem mensagens para o usuário.

## 3. Criar o banco e as tabelas

### Opção A — pgAdmin

1. Conecte-se ao servidor PostgreSQL.
2. Abra o Query Tool conectado ao banco `postgres`, com autocommit habilitado.
3. Execute `sql/01_criar_banco.sql` uma única vez. `CREATE DATABASE` deve ser
   executado fora de uma transação.
4. Atualize a lista de bancos e abra **outro Query Tool conectado a `poo_exercicios`**.
5. Execute `sql/02_criar_tabelas.sql` uma única vez nesse banco.

Se o banco já existir, pule sua criação. Se as tabelas já existirem, confira sua
estrutura antes de executar o DDL. Os scripts não apagam nem recriam tabelas existentes.

### Opção B — terminal com psql

Na pasta do projeto, execute os comandos abaixo. O PostgreSQL solicitará a senha
quando necessário. Substitua `postgres` se você usa outro usuário com permissão
para criar bancos e tabelas.

```bash
psql -h localhost -p 5432 -U postgres -d postgres -v ON_ERROR_STOP=1 -f sql/01_criar_banco.sql
psql -h localhost -p 5432 -U postgres -d poo_exercicios -v ON_ERROR_STOP=1 -f sql/02_criar_tabelas.sql
```

## 4. Configurar a conexão

Na pasta que contém `pom.xml`, copie `database.properties.example` para
`database.properties`.

No Windows PowerShell:

```powershell
Copy-Item database.properties.example database.properties
```

No Linux/macOS:

```bash
cp database.properties.example database.properties
```

Edite **somente a cópia `database.properties`**:

```properties
db.url=jdbc:postgresql://localhost:5432/poo_exercicios
db.user=postgres
db.password=SUA_SENHA_DO_POSTGRESQL
```

Substitua `SUA_SENHA_DO_POSTGRESQL` pela senha configurada no seu servidor.
O arquivo real já está no `.gitignore`. O arquivo `.example` deve permanecer
sem credenciais reais, pois faz parte do repositório.

Também é possível configurar `DB_URL`, `DB_USER` e `DB_PASSWORD` como variáveis de
ambiente. Cada variável definida tem prioridade sobre o campo correspondente no
arquivo. A URL e o usuário têm os valores padrão mostrados acima; a senha precisa
ser configurada. O arquivo é procurado no diretório em que o programa é executado.

## 5. Compilar e executar

Abra o terminal **na pasta que contém `pom.xml`**:

```bash
mvn clean compile
mvn exec:java
```

Ou compile e execute em um comando:

```bash
mvn clean compile exec:java
```

Em uma IDE, importe a pasta como projeto Maven, selecione o JDK 17 ou superior
e execute `app.Main`. Configure a raiz do projeto como diretório de trabalho
para que `database.properties` seja encontrado.

O programa executa uma demonstração automática; não exige entrada pelo teclado.
Ela realiza:

1. Inserção de uma desenvolvedora e recuperação do ID gerado.
2. Inserção de dois jogos associados à mesma desenvolvedora.
3. Listagem de desenvolvedoras e de jogos com todos os dados da desenvolvedora.
4. Busca por ID em ambas as entidades.
5. Atualização das duas entidades e nova consulta para conferir os valores gravados.
6. Tentativa de excluir a desenvolvedora antes dos jogos, conferindo o erro de chave estrangeira.
7. Exclusão dos dois jogos e, depois, da desenvolvedora; novas consultas confirmam a remoção.

A mensagem de bloqueio por `ON DELETE RESTRICT` é um resultado esperado. A
demonstração só considera esse passo aprovado se o SQLState for `23503`
(`foreign_key_violation`) ou `23001` (`restrict_violation`), conforme a versão do servidor.
Outras falhas são propagadas e fazem o programa terminar com código de saída 1.

Ao concluir com sucesso, a demonstração remove apenas os registros que criou,
identificados pelos IDs gerados naquela execução. Dados anteriores permanecem no
banco. Os números das sequências `SERIAL` não voltam ao valor anterior; portanto,
os IDs mudam nas próximas execuções.

As operações da demonstração são independentes e usam autocommit. Se houver falha
ou interrupção no meio da execução, registros já inseridos podem permanecer no banco.

## 6. Modelagem e decisões de implementação

### Associação 1:N

`Jogo` possui o atributo privado `Desenvolvedora desenvolvedora`. Dois ou mais
objetos `Jogo` podem apontar para a mesma desenvolvedora. No banco, cada jogo guarda
o ID do pai em `desenvolvedora_id`.

A referência no lado N atende à alternativa prevista no enunciado. Não é necessário
manter uma lista duplicada de jogos dentro de `Desenvolvedora`. As duas classes têm
construtor padrão, construtores com parâmetros, getters, setters e `toString()`.

### Persistência e integridade

- Todos os valores variáveis nas consultas são parâmetros `?` de `PreparedStatement`.
- As inserções usam `Statement.RETURN_GENERATED_KEYS` e atualizam o ID do objeto.
- `Connection`, `PreparedStatement` e `ResultSet` são fechados com `try-with-resources`.
- Os DAOs propagam `SQLException`; o `Main` faz o tratamento com `try-catch`.
- `buscarPorId()` retorna `Optional.empty()` quando o ID não existe.
- `atualizar()` e `deletar()` retornam `false` quando nenhum registro corresponde ao ID.
- `salvar()` recebe um objeto novo com ID 0; atualização e remoção exigem ID positivo.
- O banco impede jogos sem desenvolvedora e referências a IDs inexistentes.
- `ON DELETE RESTRICT` exige a remoção ou reassociação dos jogos antes de excluir seu pai.

`JogoDAO.listarTodos()` e `JogoDAO.buscarPorId()` usam `INNER JOIN` e reconstroem
os dois objetos associados. `DesenvolvedoraDAO.listarTodos()` consulta a tabela pai
diretamente para incluir também desenvolvedoras sem jogos.

### Tipos e validações

Os atributos `precoVenda` e `tamanhoGB` permanecem `double`, como solicitado.
Os valores são convertidos com `BigDecimal.valueOf()` para envio ao PostgreSQL,
que os armazena como `NUMERIC`. Os setters rejeitam valores negativos, não finitos,
acima do limite da coluna ou com mais de duas casas decimais. Essa conversão não
torna cálculos com `double` exatos; o projeto não realiza cálculos financeiros.

O preço máximo é `99.999.999,99`, correspondente a `NUMERIC(10,2)`; o tamanho máximo
é `9.999,99 GB`, correspondente a `NUMERIC(6,2)`. Preço e tamanho zero são aceitos.

`paisOrigem` e `anoCriacao` são opcionais no DDL original. Como o atributo Java
`anoCriacao` é `int`, o valor 0 representa ano não informado e é convertido para
`NULL` no banco; a leitura realiza a conversão inversa. Anos informados vão de
1 a 9999. País vazio é convertido para `NULL`.

Além das chaves e dos tipos pedidos, o SQL inclui `CHECK` para campos obrigatórios
não vazios, valores não negativos e ano válido. A chave estrangeira tem um índice.
O trecho SQL recebido no enunciado terminava em `RESTRICT`; o fechamento `);`
está completo nos arquivos deste projeto.

## 7. Script SQL das tabelas

Primeiro, execute a criação do banco separadamente:

```sql
CREATE DATABASE poo_exercicios;
```

Depois, conectado ao banco `poo_exercicios`, execute:

```sql
BEGIN;

CREATE TABLE desenvolvedora (
    id SERIAL PRIMARY KEY,
    razao_social VARCHAR(120) NOT NULL,
    pais_origem VARCHAR(60),
    ano_criacao INT,
    CONSTRAINT ck_desenvolvedora_razao_social
        CHECK (LENGTH(TRIM(razao_social)) > 0),
    CONSTRAINT ck_desenvolvedora_ano_criacao
        CHECK (ano_criacao BETWEEN 1 AND 9999)
);

CREATE TABLE jogo (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    preco_venda NUMERIC(10,2) NOT NULL,
    tamanho_gb NUMERIC(6,2) NOT NULL,
    desenvolvedora_id INT NOT NULL,
    CONSTRAINT fk_jogo_desenvolvedora
        FOREIGN KEY (desenvolvedora_id)
        REFERENCES desenvolvedora(id) ON DELETE RESTRICT,
    CONSTRAINT ck_jogo_titulo CHECK (LENGTH(TRIM(titulo)) > 0),
    CONSTRAINT ck_jogo_preco CHECK (preco_venda >= 0),
    CONSTRAINT ck_jogo_tamanho CHECK (tamanho_gb >= 0)
);

CREATE INDEX idx_jogo_desenvolvedora_id ON jogo(desenvolvedora_id);

COMMIT;
```

Exemplo da consulta relacional usada no DAO:

```sql
SELECT j.id AS jogo_id, j.titulo, j.preco_venda, j.tamanho_gb,
       d.id AS desenvolvedora_id, d.razao_social, d.pais_origem, d.ano_criacao
FROM jogo j
INNER JOIN desenvolvedora d ON d.id = j.desenvolvedora_id
ORDER BY j.id;
```
