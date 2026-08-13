# Roteiro de Testes da API — Sistema de Gestão de Salão

## 1. Objetivo

### Este documento organiza um roteiro prático para testar, no Postman ou no Insomnia, o fluxo principal e o CRUD da aplicação web desenvolvida com **Spring Boot**, **Java** e **PostgreSQL**.

### A API gerencia clientes, profissionais, serviços, agendamentos, lançamentos financeiros e estoque. Os testes devem ser executados, preferencialmente, na sequência apresentada, pois alguns módulos dependem de registros criados anteriormente.

> **Base URL:** `http://localhost:8080`
>
> **Formato dos dados:** JSON (`Content-Type: application/json`)
>
> **Observação:** Os identificadores utilizados nos exemplos, como `1` e `2`, são ilustrativos. Substitua-os pelos IDs realmente retornados pela aplicação.

## 2. Pré-requisitos

Antes de iniciar os testes, confirme que:

- A aplicação Spring Boot está em execução na porta `8080`.
- O banco de dados PostgreSQL está disponível e configurado.
- As tabelas e possíveis triggers do banco foram criadas corretamente.
- O Postman ou o Insomnia está instalado.
- As requisições que possuem corpo utilizam o formato `raw JSON`.
- Os IDs usados nas requisições relacionadas existem no banco de dados.

## 3. Sequência recomendada

Execute os testes nesta ordem:

1. Criar e consultar um usuário.
2. Criar um profissional vinculado a um usuário.
3. Criar e consultar um serviço.
4. Criar um agendamento utilizando cliente, profissional e serviço existentes.
5. Atualizar o status do agendamento.
6. Registrar uma entrada financeira vinculada ao agendamento.
7. Consultar o fechamento diário e o balanço mensal.
8. Criar um produto no estoque.
9. Consumir, repor e consultar o estoque.
10. Executar os testes de atualização e exclusão, preferencialmente com dados de teste.

## 4. Módulo de usuários

Responsável pelo cadastro dos clientes e pela manutenção da pontuação de fidelidade.

### 4.1 Criar cliente

**Método:** `POST`  
**URL:** `http://localhost:8080/api/usuarios`

```json
{
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "telefone": "84999999999",
  "senha": "123"
}
```

**Resultado esperado:** o cliente deve ser criado e a resposta deve retornar os dados persistidos, incluindo o ID gerado.

### 4.2 Consultar clientes

- **Listar todos:** `GET /api/usuarios`
- **Buscar por ID:** `GET /api/usuarios/{id}`

Exemplo:

```text
GET http://localhost:8080/api/usuarios/1
```

### 4.3 Atualizar cliente

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/usuarios/1`

```json
{
  "nome": "Maria Silva de Souza",
  "senha": "nova_senha_segura"
}
```

> Confirme no controller e no service se o `PUT` exige todos os campos ou se aceita atualização parcial. Caso aceite apenas atualização parcial, o comportamento se aproxima de um `PATCH`.

### 4.4 Excluir cliente

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/usuarios/1`

A requisição não possui corpo.

### 4.5 Regra de fidelidade

O campo `pontos_fidelidade` não precisa ser enviado no cadastro. De acordo com a regra descrita, sua atualização ocorre automaticamente quando um pagamento é registrado no módulo financeiro. Depois de registrar uma entrada financeira, consulte novamente o cliente para verificar a alteração da pontuação.

## 5. Módulo de profissionais

Responsável pelo cadastro da equipe e das porcentagens individuais de comissão.

### 5.1 Criar profissional

**Método:** `POST`  
**URL:** `http://localhost:8080/api/profissionais`

```json
{
  "idUsuario": 2,
  "funcao": "Cabeleireira Senior",
  "porcentagemComissao": 50.00
}
```

O valor de `idUsuario` deve corresponder a um usuário existente.

### 5.2 Consultar profissionais

- **Listar todos:** `GET /api/profissionais`
- **Buscar por ID:** `GET /api/profissionais/{id}`

Exemplo:

```text
GET http://localhost:8080/api/profissionais/1
```

### 5.3 Atualizar profissional

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/profissionais/1`

```json
{
  "idUsuario": 2,
  "funcao": "Especialista em Colorimetria",
  "porcentagemComissao": 60.00
}
```

### 5.4 Excluir profissional

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/profissionais/1`

## 6. Módulo de serviços

Representa o catálogo de procedimentos oferecidos pelo salão.

### 6.1 Criar serviço

**Método:** `POST`  
**URL:** `http://localhost:8080/api/servicos`

```json
{
  "nomeServico": "Corte Feminino",
  "descricao": "Corte em camadas e finalização",
  "preco": 120.00,
  "duracaoMinutos": 60
}
```

### 6.2 Consultar serviços

- **Listar todos:** `GET /api/servicos`
- **Buscar por ID:** `GET /api/servicos/{id}`

Exemplo:

```text
GET http://localhost:8080/api/servicos/1
```

### 6.3 Atualizar serviço

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/servicos/1`

```json
{
  "nomeServico": "Corte Feminino - Repicado",
  "descricao": "Corte em camadas com lavagem e escova inclusa",
  "preco": 150.00,
  "duracaoMinutos": 90
}
```

### 6.4 Excluir serviço

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/servicos/1`

## 7. Módulo de agendamentos

Controla a agenda, os atendimentos e seus respectivos status.

### 7.1 Criar agendamento

**Método:** `POST`  
**URL:** `http://localhost:8080/api/agendamentos`

```json
{
  "idCliente": 1,
  "idProfissional": 1,
  "idServico": 1,
  "dataAtendimento": "2026-08-10",
  "horaInicio": "14:00:00"
}
```

O campo utilizado para a data é `dataAtendimento`.

### 7.2 Consultar agendamentos

- **Listar todos:** `GET /api/agendamentos`
- **Buscar por ID:** `GET /api/agendamentos/{id}`

### 7.3 Atualizar o status

**Método:** `PATCH`  
**URL:** `http://localhost:8080/api/agendamentos/1/status?novoStatus=CONCLUÍDO`

Valores informados como aceitos:

- `CANCELADO`
- `AUSENTE`
- `CONFIRMADO`
- `CONCLUÍDO`

> Verifique se a aplicação utiliza acentos no valor `CONCLUÍDO` ou se o enum espera uma forma sem acento, como `CONCLUIDO`.

### 7.4 Reagendar o atendimento

Caso exista uma rota `PUT` no controller para atualização completa:

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/agendamentos/1`

```json
{
  "idCliente": 1,
  "idProfissional": 1,
  "idServico": 1,
  "dataAtendimento": "2026-08-15",
  "horaInicio": "16:30:00",
  "status": "CONFIRMADO"
}
```

> O exemplo original utilizava `REAGENDADO`, mas esse valor não aparece na lista de status aceitos. Use-o somente se ele estiver definido no enum e for aceito pelo backend.

### 7.5 Excluir agendamento

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/agendamentos/1`

## 8. Módulo financeiro

Controla entradas, despesas, comissões, fechamento diário e balanço mensal.

### 8.1 Registrar receita ou despesa

**Método:** `POST`  
**URL:** `http://localhost:8080/api/financeiro`

Exemplo de entrada:

```json
{
  "tipo": "ENTRADA",
  "valor": 120.00,
  "formaPagamento": "PIX",
  "descricao": "Pagamento de Corte",
  "idProfissional": 1,
  "idAgendamento": 1
}
```

De acordo com a regra descrita, o registro de um pagamento pode atualizar automaticamente os pontos de fidelidade do cliente relacionado.

### 8.2 Consultar lançamentos

**Método:** `GET`  
**URL:** `http://localhost:8080/api/financeiro`

Essa rota deve ser utilizada para auditoria do livro caixa.

### 8.3 Fechamento diário

**Método:** `GET`  
**URL:** `http://localhost:8080/api/financeiro/fechamento-diario?data=2026-08-10`

A resposta deve apresentar os valores do dia e, conforme a regra descrita, calcular a comissão somente quando aplicável a um dia útil.

### 8.4 Balanço mensal

**Método:** `GET`  
**URL:** `http://localhost:8080/api/financeiro/balanco-mensal?mes=8&ano=2026`

A resposta deve apresentar entradas, saídas e a situação final, como `LUCRO` ou `PREJUÍZO`.

### 8.5 Excluir ou estornar lançamento

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/financeiro/1`

O material original não apresenta uma rota `PUT` para edição financeira. Em um fluxo de auditoria, um lançamento incorreto pode ser excluído e recriado ou compensado por um estorno, conforme as regras implementadas no sistema.

## 9. Módulo de estoque

Controla os insumos do salão e os níveis mínimos de quantidade. O módulo também pode utilizar uma trigger do PostgreSQL para atualizar automaticamente a data da última compra.

### 9.1 Cadastrar produto

**Método:** `POST`  
**URL:** `http://localhost:8080/api/estoque`

```json
{
  "nomeProduto": "Shampoo L'Oréal 1L",
  "quantidade": 15,
  "nivelMinimo": 5,
  "fornecedor": "Distribuidora ABC"
}
```

### 9.2 Consultar inventário

**Método:** `GET`  
**URL:** `http://localhost:8080/api/estoque`

A resposta deve listar os produtos e indicar a situação do estoque, como `OK` ou `ALERTA: ESTOQUE BAIXO`.

### 9.3 Consumir produto

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/estoque/1/consumir`

```json
{
  "quantidadeConsumida": 2
}
```

Após a operação, consulte o inventário para confirmar a redução da quantidade.

### 9.4 Repor produto

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/estoque/1/repor?quantidade=10`

A quantidade de reposição é enviada como parâmetro na URL. Conforme a regra descrita, a trigger do banco atualiza `data_ultima_compra` automaticamente.

### 9.5 Atualizar dados cadastrais

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/estoque/1`

```json
{
  "nomeProduto": "Shampoo L'Oréal 1L - Profissional",
  "quantidade": 15,
  "nivelMinimo": 10,
  "fornecedor": "Nova Distribuidora Premium"
}
```

### 9.6 Consultar alertas

**Método:** `GET`  
**URL:** `http://localhost:8080/api/estoque/alertas`

A resposta deve retornar exclusivamente os produtos cuja quantidade seja menor ou igual ao nível mínimo configurado.

### 9.7 Excluir produto

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/estoque/1`

## 10. Checklist de validação

Para cada requisição, registre:

- Método HTTP utilizado.
- URL completa.
- Corpo JSON enviado, quando aplicável.
- Código HTTP retornado.
- Mensagem ou corpo da resposta.
- Alterações observadas no banco de dados.
- Tratamento de erros para IDs inexistentes ou dados inválidos.

### Fluxo ideal de demonstração

1. Criar um cliente e guardar o ID retornado.
2. Criar um profissional vinculado ao usuário existente.
3. Criar um serviço e guardar seu ID.
4. Criar um agendamento com os três IDs relacionados.
5. Alterar o status para `CONFIRMADO` ou `CONCLUÍDO`.
6. Registrar o pagamento do atendimento.
7. Consultar o cliente e verificar a pontuação de fidelidade.
8. Consultar o fechamento diário e o balanço mensal.
9. Criar um produto, consumir parte do estoque e verificar seu status.
10. Repor o produto e confirmar a atualização da data da última compra.

## 11. Observações finais

Este roteiro consolida as descrições duplicadas em um único documento e separa os testes de criação, leitura, atualização e exclusão por módulo.
