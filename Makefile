dev:
	docker compose -f compose.yaml up -d --build --remove-orphans
stop:
	docker compose -f compose.yaml down
log:
	docker logs -ft list-manager-db
