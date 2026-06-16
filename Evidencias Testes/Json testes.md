MANUAL DE TESTES DA API: RAÍZES DO NORDESTE
=================================================================
Este documento apresenta o roteiro passo a passo para testar os fluxos principais da API utilizando o Swagger UI. Os testes estão divididos pelos três perfis de acesso do sistema: Gerente, Cliente e Atendente.


1. TESTES DO PERFIL: GERENTE
=================================================================
Estes testes preparam os dados base do sistema e exigem permissões de administração.

Teste 1: Cadastrar um Gerente
- Rota: POST /usuarios
- Body:
{
  "nome": "Gerente Oficial",
  "email": "gerente@raizes.com",
  "senha": "senha123",
  "tipoUsuario": "GERENTE"
}

Teste 2: Fazer Login
- Rota: POST /auth/login
- Body:
{
  "email": "gerente@raizes.com",
  "senha": "senha123"
}
[!] Após receber a resposta, copie o Token gerado e cole-o no botão "Authorize" (cadeado) no topo da página.

Teste 3: Criar uma Unidade
- Rota: POST /unidades
- Body:
{
  "nome": "Loja 01",
  "cidade": "Cidade 01",
  "descricao": "A primeira loja"
}

Teste 4: Criar um Produto
- Rota: POST /produtos
- Body:
{
  "nome": "Crepioca",
  "descricao": "Crepioca de Queijo",
  "preco": 22.50
}

Teste 5: Adicionar Produto ao Estoque
- Rota: POST /estoques
- Parâmetros na Interface:
  * produtoId: 1
  * unidadeId: 1
  * quantidade: 10


2. TESTES DO PERFIL: CLIENTE
=================================================================
[!] Atenção: Faça Logout no cadeado "Authorize" antes de iniciar.

Teste 6: Cadastrar um Cliente
- Rota: POST /usuarios
- Body:
{
  "nome": "Mateus Bonfanti",
  "email": "mateus@cliente.com",
  "senha": "4873600",
  "tipoUsuario": "CLIENTE"
}

Teste 7: Fazer Login do Cliente
- Rota: POST /auth/login
- Body:
{
  "email": "mateus@cliente.com",
  "senha": "4873600"
}
[!] Copie o novo Token recebido e cole no botão "Authorize".

Teste 8: Completar o Cadastro do Cliente
- Rota: POST /clientes
- Body:
{
  "nome": "Mateus Bonfanti",
  "cpf": "12345678900",
  "email": "mateus@cliente.com",
  "telefone": "54999999999",
  "dataNascimento": "1995-08-10",
  "aceiteLgpd": true
}

Teste 9: Consultar o Cardápio
- Rota: GET /cardapio/unidade/1
- Parâmetros na Interface:
  * id (path): 1

Teste 10: Fazer um Pedido
- Rota: POST /pedidos
- Parâmetros na Interface:
  * unidadeId (query): 1
- Body:
{
  "canalPedido": "APP",
  "itens": [
    {
      "produto": { "id": 1 },
      "quantidade": 2
    }
  ]
}

Teste 11: Simular um Pagamento
- Rota: POST /pagamentos/simular
- Parâmetros na Interface:
  * pedidoId: (inserir o ID do pedido gerado no Teste 10)
  * aprovado: true


3. TESTES DO PERFIL: ATENDENTE
=================================================================
[!] Atenção: Faça Logout no cadeado "Authorize" antes de iniciar.

Teste 12: Cadastrar um Atendente
- Rota: POST /usuarios
- Body:
{
  "nome": "Atendente",
  "email": "atendente@atendente.com",
  "senha": "senha1234",
  "tipoUsuario": "ATENDENTE"
}

Teste 13: Fazer Login do Atendente
- Rota: POST /auth/login
- Body:
{
  "email": "atendente@atendente.com",
  "senha": "senha1234"
}
[!] Copie o Token do Atendente e cole no botão "Authorize".

Teste 14: Listar os Pedidos da Unidade
- Rota: GET /pedidos/unidade/
- Descrição: Basta clicar em executar. Retorna os pedidos ativos.

Teste 15: Finalizar um Pedido
- Rota: PATCH /pedidos/1/status
- Parâmetros na Interface:
  * id: 1
  * novoStatus: ENTREGUE