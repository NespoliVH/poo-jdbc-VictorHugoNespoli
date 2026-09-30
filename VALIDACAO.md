# Registro de validação

Data: 30/09/2026.

## Ambiente utilizado

- OpenJDK 17.0.20.
- Apache Maven 3.9.9.
- Driver pgJDBC 42.7.13, o mesmo declarado no `pom.xml`.
- PGlite 0.5.8, reportando PostgreSQL 18.3 em WebAssembly.
- Conexão JDBC por TCP local através de `@electric-sql/pglite-socket` 0.2.11.

## Resultados

| Verificação | Resultado |
|---|---|
| Compilação das seis classes Java pelo Maven | Aprovada — `BUILD SUCCESS`. |
| Execução do DDL das tabelas, incluindo chaves e restrições | Aprovada. |
| Inserção de desenvolvedora e dois jogos com recuperação de IDs | Aprovada. |
| Listagem e busca por ID em ambas as entidades | Aprovadas. |
| `INNER JOIN` com reconstrução completa da desenvolvedora | Aprovado. |
| Atualização e nova leitura das duas entidades | Aprovadas. |
| Exclusão do pai com filhos vinculados | Bloqueada; SQLState observado: `23001`. |
| Exclusão dos filhos seguida da exclusão do pai | Aprovada. |
| Consultas depois da exclusão | Retornaram ausência dos registros. |
| Preservação de registros anteriores à demonstração | Aprovada. |
| País e ano opcionais, incluindo `NULL` no banco | Aprovados. |
| Texto com apóstrofo e conteúdo semelhante a SQL | Armazenado e recuperado literalmente, sem executar o texto. |
| Valores zero e limites máximos das colunas `NUMERIC` | Aprovados. |
| Mudança do jogo para outra desenvolvedora existente | Aprovada. |
| Inserção com ID de desenvolvedora inexistente | Bloqueada com SQLState `23503`. |
| Entradas negativas, não finitas, fora do limite ou com mais de duas casas | Rejeitadas. |
| Atualização e exclusão de IDs já removidos | Retornaram `false`, conforme documentado. |

A demonstração `app.Main` terminou com código de saída 0. Um programa auxiliar
temporário executou 25 verificações adicionais de persistência e validação, todas
aprovadas. A consulta final confirmou que apenas os registros de controle,
anteriores ao teste, continuavam presentes.

## Limites desta validação

Não havia um servidor PostgreSQL convencional instalado no ambiente. Os testes
de banco foram realizados com PGlite; não foram testes de autenticação, instalação
do serviço, rede externa ou concorrência de um servidor PostgreSQL convencional.
O script `CREATE DATABASE poo_exercicios` não foi executado nesse ambiente: o
banco de teste utilizou a instância temporária disponibilizada pelo PGlite.

PGlite e Node.js foram utilizados somente para a verificação durante a preparação.
Não são dependências deste projeto. Para executar a entrega, use Java, Maven e
seu PostgreSQL, conforme o README.

Antes de enviar o link ao professor, execute a demonstração no seu PostgreSQL
com os scripts e as credenciais locais configurados. A criação do repositório
GitHub e o envio ao professor não foram realizados neste ambiente.

## Referências da verificação

- [PGlite Socket](https://pglite.dev/docs/pglite-socket)
- [Códigos SQLState do PostgreSQL](https://www.postgresql.org/docs/18/errcodes-appendix.html)
