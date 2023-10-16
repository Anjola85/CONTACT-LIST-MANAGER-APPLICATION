dev:
	docker compose -f compose.yaml up -d --build --remove-orphans

stop:
	docker compose -f compose.yaml down

log-db:
	docker logs -ft list-manager-db

log-pgadmin:
	docker logs -ft list-manager-pgadmin

shell-db:
	docker exec -it list-manager-db bash

shell-pgadmin:
	docker exec -it list-manager-pgadmin bash
