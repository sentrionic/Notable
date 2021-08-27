postgres:
	docker run --name postgres12 -p 5432:5432 -e POSTGRES_USER=root -e POSTGRES_PASSWORD=password -d postgres:12-alpine

createdb:
	docker exec -it postgres12 createdb --username=root --owner=root notable

dropdb:
	docker exec -it postgres12 dropdb notable

recreate:
	make dropdb && make createdb

migrateup:
	migrate -path db/migration -database "postgresql://root:password@localhost:5432/notable?sslmode=disable" -verbose up

migratedown:
	migrate -path db/migration -database "postgresql://root:password@localhost:5432/notable?sslmode=disable" -verbose down

sqlc:
	docker run --rm -v %cd%:/src -w /src kjconroy/sqlc generate

test:
	go test -v -cover ./...

server:
	go run main.go

mock:
	mockgen -package mockdb -destination db/mock/store.go github.com/sentrionic/notable/db/sqlc Store

build:
	$Env:GOOS = "linux"; $Env:GOARCH = "arm"; $Env:GOARM = "7"; go build

lint:
	golangci-lint run