# lancer tout (build initial)
docker compose up --build

# modifier Java
cd backend-java
./mvnw spring-boot:run         # en local, hot-reload
# ou rebuild rapide
docker compose up --build api

# modifier un .php ou .html
docker compose restart web