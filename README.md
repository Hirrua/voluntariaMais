# Voluntaria+

*Voluntaria+ é uma plataforma digital desenvolvida para facilitar a conexão entre 
voluntários e projetos de organizações não governamentais (ONGs). 
Com um sistema intuitivo e acessível, a plataforma permite que ONGs 
cadastrem os seus projetos, gerenciem atividades e encontrem voluntários 
ideais para cada iniciativa e objetivo.*

## Status do projeto
- [x] Back-end - (01/08/2025)
- [x] Front-end - (20/01/2026)

> O projeto está em constante evolução com novas funcionalidades e tecnologias, acompanhe o desenvolvimento!

## Como rodar o projeto
1. É necessário gerar duas chaves: ``app.key`` e ``app.pub`` que devem ficar armazenadas em `src/main/resource`. Utilize
OpenSSL para gerar.
```shell
  openssl genrsa -out app.key 2048

  openssl rsa -in app.key -pubout -out app.pub
```
2. Crie uma cópia do arquivo `.env-example` e forneça as credenciais necessárias, você pode utilizar o provedor de e-mail
que achar melhor.
3. Suba o `docker-compose.yml` que contém a *aplicação*, *postgresql* e o *pgadmin*, com o seguinte comando: 
```shell
  docker-compose up -d
```

## Módulos do sistema

### 1. **Registro e login de usuários**
- **Funcionalidade para registrar e logar usuários na plataforma**:
    - Para se registrar algumas informações são necessárias como: nome, sobrenome, e-mail, senha, cpf,
    e o endereço. Por padrão todos os usuários que se registrar, são atribuídos como voluntários.
    - O login é realizado com o e-mail e senha cadastrados, a senha é criptografada utilizando BCryptPasswordEncoder e
    gerado um token JWT para realizar o acesso os demais endpoints.

### 2. **Perfil Voluntário**
- **Gerenciar informações do perfil do voluntário**:
  - Criar um perfil com informações como: bio, disponibilidade, data de nascimento
  e telefone de contato, as demais informações já estão contidas ao realizar o registro.
  - Buscar um determinado perfil baseado no id fornecido.
  - Atualizar os dados de um perfil.
  - Deletar o perfil.

### 3. **ONG**
- **Funcionalidades para o controle de ONG's**
  - Criar uma ONG é necessário ter um usuário responsável e fornecer as seguintes informações: nome, cnpj, descrição sobre a ong, e-mail de contato,
  telefone de contato, link do site (opcional), logo da ong (opcional), data da fundação e endereço.
  - É possível buscar uma ONG com base em um id fornecido.
  - Listar uma ONG e os seus projetos vinculados a ela.
  - Buscar todas as ONG's cadastradas no sistema.
  - Atualizar as informações da ONG.
  - Deletar a ONG, tanto o admin da ong pode realizar a exclusão quanto o administrador da plataforma.

### 4. **Projetos**
*Uma ONG pode ter 1 ou mais projetos.*
- **Funcionalidades para o gerenciamento de projetos de uma ONG**
  -  Criar e vincular um projeto especifico a uma ONG com as seguintes informações: nome do projeto, descrição sobre o projeto, objetivo do projeto, publico alvo,
  data de inicio do projeto, data de termino do projeto, endereço do projeto, uma imagem de destaque para o projeto (opcional).
  - Buscar todos os projetos no sistema.
  - Atualizar as informações sobre o projeto, validando que somente o admin (responsável) está realizando a alteração.
  - Exclusão do projeto, com a validação de que usuário está fazendo aquela operação.

### 5. **Atividades**
*Um projeto pode possuir 1 atividade em andamento ou muitas, para que consiga atingir o objetivo principal.*
- **Gerenciamento de atividades**
  - Criar uma atividade com as seguintes informações: nome, descrição, a data e hora de inicio, data e hora de finalização da atividade, o local que será realizada, 
  números de vagas.
  - Buscar todas as atividades disponiveis no sistema, bem como a informação de quantas vagas já foram preenchidas.
  - Atualizar as informações sobre a atividade, validando que somente o admin (responsável) está realizando a alteração e atualizando no banco quando foi realizada a 
  última alteração.
  - Exclusão da atividade, com a validação de que usuário está fazendo aquela operação.

### 6. **Inscrição**
- **Funcionalidade para que um voluntário realize a inscrição em uma atividade**
  - Para realizar a inscrição é necessário fornecer o id da atividade em questão e o id do usuário, é validado se o usuário possui um perfil de voluntário,
  validação se ainda existe vagas disponiveis na atividade com o auxílio do lock otimista ```@Version```  na entidade da atividade. É gerado um token aleatório de 
  confirmação e a data de expiração do mesmo, é realizado o envio do e-mail utilizando ```JavaMailSender``` e do Thymeleaf para montar o HTML do corpo do e-mail.
  - Buscar a lista de inscritos em uma determinada atividade.
  - Endpoint para confirmação da inscrição após o envio do e-mail, é validado se o token existe, se o token não está expirado ou se a inscrição já foi confirmada anteriormente e
  validar se ainda possui vagas para a inscrição.

### 7. **Envio de e-mail**
- **Funcionalidade para que um voluntário receba um e-mail de confirmação de inscrição**
  - Utilizado ```JavaMailSender``` e ```SpringTemplateEngine``` juntamente com o Thymeleaf para criação de um template de e-mail, configuração do MimeMessage para definir o formato UTF-8
  e criação de um botão para confirmar, o método deve receber: destinatário, assunto, template usado, e o conteúdo.

### 8. **Feedback**
- **Funcionalidade para que um voluntário possa dar sua opinião referente ao projeto/atividade que participou**
  - Para escrever um comentário é necessário informar qual a inscrição, o usuário que está tentando deixar um comentário, nota de 0 a 5 e um comentário, é validado se a inscrição
  existe, se o usuário estava matriculado nessa atividade e se já foi concluida a participação.
  - É possível atualizar um comentário.
  - Para deletar um comentário o usuário (voluntário) ou o administrador da ONG também pode decidir realizar a exclusão.
  
## Tecnologias

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Lombok
- Oauth2 JWT
- Docker compose
- Flyway
- Spring Boot Mail (Java Mail Sender)
- Thymeleaf
- MapStruct
