FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/auth-service.jar app.jar

# instalar openssl dentro do container
RUN apt-get update && apt-get install -y openssl

COPY docker/init-keys.sh /init-keys.sh

RUN chmod +x /init-keys.sh

ENTRYPOINT ["/init-keys.sh"]

CMD ["java", "-jar", "app.jar"]