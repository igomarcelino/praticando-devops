# 🚀 Exercício Prático – DevOps com Spring Boot

Este projeto visa aplicar na pratica como seria um processo de deploy e integração continua utilizando o github actions, para esse projeto foi utilizado uma vm alocada no Google Cloud Platform.
Eu separei o projeto em tópicos para facilitar a aplicação do conceito.

## 📑 Checklist de Etapas

- [ ]  Criar um projeto simples no Spring Initializr que utilize Web, JPA, Postgres
- [ ]  Criar um repositório para o projeto no GitHub
- [ ]  Criar o `Dockerfile`
- [ ]  Definir `docker-compose` (desenvolvimento)
- [ ]  Definir `docker-compose` (produção)
- [ ]  Definir `nginx.conf`
- [ ]  Preparar o servidor
    - [ ]  Instalar Docker
    - [ ]  Instalar Docker Compose
    - [ ]  Criar diretório do projeto
    - [ ]  Provisionar arquivos (`docker-compose.yml`, `nginx.conf`) no diretório
    - [ ]  Adicionar chaves SSH para conexão
- [ ]  Preparar o GitHub
    - [ ]  Configurar o Action do repositório
    - [ ]  Dar permissão para o workflow
    - [ ]  Definir as Secrets no Action
- [ ]  Subir o projeto
- [ ]  Verificar os testes de build e deploy no GitHub Actions

---

## 1️⃣ Criando o Projeto no Spring Initializr

Para seguir nesse projeto vamos criar uma api simples e utilizar o github para versionar o nosso projeto, você pode criar a aplicação de sua preferência

Projeto do GitHub:

👉 [igomarcelino/praticando-devops](https://github.com/igomarcelino/praticando-devops)

O projeto está separado por **branches**.

---

## 2️⃣ Criando o `Dockerfile`

Iremos criar o nosso Dockerfile, ele será responsável por criar um build de nossa aplicação, disponibilisando assim uma imagem docker para utilização

```docker
# ---- Estágio 1: Build ----
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Estágio 2: Runtime ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]


```
Para poder utilizar o ENTRYPOINT com o nome app.jar, podemos adicionar ao nosso pom.xml a seguinte configuração

```xml
    <build>
		<finalName>app</finalName>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
```

---

## 3️⃣ Definindo Docker Compose

Nesse projeto iremos ter dois docker-compose, um para desenvolvimento e outro para produção, dessa forma podemos subir o projeto tranquilamente tanto em ambiente de desenvolvimento quanto em produção
Quando formos subir o nosso projeto em desenvolvimento podemos simplesmente utilizar o comando
```text
    docker compose -f docker-compose.yml up -d --build
```
Dessa forma ele ira buildar a imagem utilizando nosso Dockerfile.

### Desenvolvimento → `docker-compose.override.yml`

```yaml
services:
  app:
    build: .
    container_name: app_praticando_devops
    restart: always
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/mensagens
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_JPA_HIBERNATE_DDL_AUTO: update

  db:
    image: postgres:15
    ports:
      - "5433:5432"

```

---

### Produção → `docker-compose.yml`

```yaml

services:
  nginx:
    image: nginx:latest
    container_name: nginx_proxy
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf:ro
    depends_on:
      - app

  app:
    # 👉 Agora usa a imagem publicada no GHCR
    image: ghcr.io/igomarcelino/praticando-devops:latest
    container_name: app_praticando_devops
    restart: always
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/mensagens
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate

  db:
    image: postgres:15
    container_name: postgres_db
    restart: always
    environment:
      POSTGRES_DB: mensagens
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:

```
Em produção nao precisamos nos preocupar pois quem fará todo esse processo de build sera o Github Actions
---

## 4️⃣ Definindo o `nginx.conf`

```
server {
    listen 80;

    location / {
        proxy_pass http://app:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

```
Essa será a configuração de nosso nginx.conf, ele será responsável por cuidar de nosso proxy reverso, sendo assim ele irá direcionar o fluxo que chegar pela porta 80 para a porta do spring :8080
---


## CI/CD – Workflow no GitHub Actions Podemos então adicionar na raiz do nosso projeto o seguinte .yml, ele será responsável pelo processo de automatização ( workflow ) no Github Actions, iremos adicionar esse arquivo em .github/workflows

```yaml
name: CI/CD Pipeline (Skip Tests)

on:
  pull_request:
    branches: ["main"]

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: mvn clean install -DskipTests
      - uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ghcr.io/${{ github.repository }}:latest

  deploy:
    runs-on: ubuntu-latest
    needs: build-and-push
    steps:
      - uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            cd /home/geslucian1/estudos-devops
            echo "${{ secrets.GHCR_PAT }}" | docker login ghcr.io -u ${{ github.actor }} --password-stdin
            docker pull ghcr.io/${{ github.repository }}:latest
            docker compose -f docker-compose.yml down
            docker compose -f docker-compose.yml pull
            docker compose -f docker-compose.yml up -d

```



## 5️⃣ Preparando o Servidor
Nessa etapa precisamos realizar algumas configurações em nosso servidor, será necessário instalar o docker e o plugin do docker-compose

- Instalar Docker

```bash
sudo apt update
sudo apt install docker.io -y

```

- Instalar Docker Compose

```bash
sudo curl -L https://github.com/docker/compose/releases/download/1.25.3/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
docker-compose version

```

- Criar diretório e copiar arquivos (`docker-compose.yml`, `nginx.conf`)
- Adicionar chave SSH para conexão com GitHub Actions

---

## 6️⃣ Preparando o GitHub

Com nosso projeto criado, servidor configurado podemos então seguir para a etapada de automatizar o nosso processo de deploy, então vamos seguir para a configuração de nosso GitHub Actions.

- Criar workflow `.github/workflows/ci-cd.yml`
- Permitir **workflow write permissions** no repositório

  ### Settings → Actions →General → Workflow permissions

  📸 Exemplo :

  ![image.png](https://github.com/igomarcelino/praticando-devops/blob/main/Screenshot%20from%202025-08-30%2012-53-06.png?raw=true)

- Definir Secrets:
    - `GHCR_PAT` → Token pessoal
    - `SERVER_HOST` → IP do servidor
    - `SERVER_USER` → Usuário SSH
    - `SSH_PRIVATE_KEY` → Chave SSH privada

📸 Exemplo das secrets:

![image.png](https://github.com/igomarcelino/praticando-devops/blob/main/Screenshot%20from%202025-08-29%2018-15-39.png?raw=true)

---

### Processo realizado podemos seguir para os testes do nosso novo fluxo de trabalho

---

## 7️⃣ Subindo e Testando

- Fazer push para a branch `main`
- Acompanhar workflow no GitHub Actions

📸 Exemplo:

![image.png](https://github.com/igomarcelino/praticando-devops/blob/main/Screenshot%20from%202025-08-30%2001-19-28.png?raw=true)
