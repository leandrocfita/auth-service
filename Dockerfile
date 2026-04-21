# --- 1. Etapa de Build ---
# Usa uma imagem com Maven ja instalado para compilar o projeto
FROM maven:3.9.9-amazoncorretto-21-alpine as build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN apk add --no-cache openssl
# Compila o projeto e cria o JAR.
RUN mvn package -DskipTests

# --- 2. Etapa Final ---
# Usa uma imagem JRE (Java Runtime Environment) muito menor para executar a aplicação
FROM amazoncorretto:21.0.3-alpine3.19

# 1. Instala openssl e cria o diretório para as chaves (como root)
#RUN apk add --no-cache openssl && \
#    mkdir /keys

# 2. Cria um grupo e um usuário de sistema dedicados para rodar a aplicação
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

RUN apk add --no-cache dos2unix
# Define o diretório de trabalho
WORKDIR /app

# 3. Copia o JAR da etapa de build e o script de inicialização
COPY --from=build /app/target/*.jar app.jar

RUN mkdir /keys
COPY init-keys.sh /app/init-keys.sh

# 4. Define as permissões corretas para o usuário da aplicação
RUN chown -R appuser:appgroup /app
RUN chown -R appuser:appgroup /keys
RUN chmod +x /app/init-keys.sh
RUN dos2unix /app/init-keys.sh && chmod +x /app/init-keys.sh

USER appuser

# 6. Define o entrypoint para o nosso script, que então iniciará a aplicação Java
ENTRYPOINT ["/app/init-keys.sh"]
CMD ["java", "-jar", "/app/app.jar"]
