# Dev : démarre tout (appli + MariaDB) en une commande
./mvnw spring-boot:run

# Prod intégré
./mvnw -DskipTests clean package
java -jar target/maadictionary-1.0-SNAPSHOT.jar