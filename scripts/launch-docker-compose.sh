# 1. première construction
docker compose up --build          # compile, lance, initialise la DB

# 2. développement Java uniquement
docker compose up api --build      # recompiles backend seul

# 3. stopper et nettoyer (containers seulement)
docker compose down