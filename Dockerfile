# Estágio 1: Build da aplicação com Maven e JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia as definições do Maven e descarrega as dependências em cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila gerando o JAR sem rodar testes novamente
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Ambiente de Execução (Runtime leve com JRE 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria um utilizador sem privilégios de root para segurança da execução
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copia apenas o binário final gerado no estágio anterior
COPY --from=build /app/target/antifraude-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta padrão do servidor HTTP
EXPOSE 8080

# Inicia a aplicação com configurações otimizadas para containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]