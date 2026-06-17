🌵 Raízes do Nordeste — API Back-end

Aluno: Mateus Bonfanti (RU 4873600)


Disciplina: Projeto Multidisciplinar: Trilha Back-end

🚀 Tecnologias Utilizadas --------------------
Java 17 / 21
Spring Boot 3
Maven
Banco de Dados: H2 Database (Arquivo local)
Segurança: Spring Security & JWT (Stateless)
Documentação: Swagger / SpringDoc OpenAPI

⚙️ Como Executar a Aplicação --------------------

Certifique-se de ter o Java JDK e o Maven instalados na sua máquina.
Clone este repositório e abra o terminal na pasta raiz do projeto.
Execute o comando abaixo para iniciar o servidor:

No Windows:
mvnw.cmd spring-boot:run   

No Linux / Mac:
./mvnw spring-boot:run   

Acessos Importantes (com a API rodando)
Interface do Swagger (Testes): http://localhost:8080/swagger-ui/index.html
Console do Banco H2: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:file:./data/raizesdb
Username: db4873600
Password: 123456

🧪 Instruções para Avaliação e Testes --------------------

Para facilitar a validação dos requisitos e a correção do trabalho, 
preparei duas abordagens de testes no repositório: automáticos e manuais.

1. Testes Automatizados (Script PowerShell)

Para testar a API de forma rápida e completa, deixei um script na raiz do projeto chamado api_tests.ps1. 
Ele executa requisições HTTP cobrindo cenários positivos e negativos, regras de negócio e bloqueios de segurança.  

Como rodar:
Com o servidor Spring Boot rodando normalmente no terminal, abra uma janela do PowerShell na pasta raiz do projeto.
Execute o comando:
.\api_tests.ps1
O script criará os usuários (Admin/Cliente), pegará os tokens JWT necessários e disparará as simulações, 
exibindo no terminal o que PASSOU (verde) e o que FALHOU (vermelho).  

2. Testes Manuais via Swagger (Roteiro e JSONs)
Caso o avaliador prefira testar a API manualmente, interagindo diretamente com a interface visual do Swagger, criei um roteiro detalhado.

Onde encontrar:
Acesse o arquivo Evidencias Testes/Json testes.md.  

Como utilizar:
O arquivo contém o passo a passo exato para simular fluxos através de três perfis de acesso diferentes: GERENTE, CLIENTE e ATENDENTE.

Utilize os códigos JSON fornecidos no documento para criar unidades, 
popular o estoque e submeter pedidos sem precisar adivinhar o formato da requisição.  

Lembrete de Autenticação: Ao usar a rota /auth/login, copie o token JWT devolvido e cole-o no botão verde "Authorize" (cadeado)
no topo da página do Swagger para desbloquear os endpoints restritos. 

Lembre-se de fazer Logout sempre que for trocar de perfil.
