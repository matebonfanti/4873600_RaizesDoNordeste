# Raízes do Nordeste — API Back-end
# RU 4873600 - Mateus Bonfanti

## Tecnologias
- Java 17+, Spring Boot 3, Maven
- H2 Database (Arquivo local)
- Swagger / SpringDoc OpenAPI


## Como executar

No Windows:
mvnw.cmd spring-boot:run

No linux/mac:
mvn spring-boot:run




## Primeiro acesso — criar usuário administrador

POST http://localhost:8080/usuarios

{
  "nome": "Admin",
  "email": "admin@raizes.com",
  "senha": "123456",
  "tipoUsuario": "GERENTE"
}


Em seguida faça login em POST /auth/login com as mesmas credenciais
para receber o token JWT. Use o token no header:
  Authorization: Bearer <token>

  ## Banco de dados
Persiste automaticamente na pasta ./data/ criada na raiz do projeto.
Console H2: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:file:./data/raizesdb
- Usuário: db4873600 | Senha: 123456