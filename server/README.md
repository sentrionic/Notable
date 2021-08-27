# Notable - Server

The server written using Go.

# Stack

- [Gin](https://gin-gonic.com/) for the REST server
- [sqlc](https://sqlc.dev/) to generate type-safe SQL
- PostgreSQL
- [Paseto](https://github.com/paragonie/paseto) Token for authentication

# Installation

(If you are familiar with `make`, take a look at the `Makefile` to quickly setup the following steps)

1. Install Docker and get the Postgresql container (`make postgres`)
2. Start the Postgresql container and create a DB (`make createdb`)
3. Install Golang and get all the dependencies (`go mod tidy`)
4. Apply the DB migration (`make migrateup`)
5. Run `go run github.com/sentrionic/notable` to run the server

# Testing

1. Make sure you are connected to the database
2. Run `go test -v -cover ./...` (`make test`)

# Credits

[Tech School](https://github.com/techschool/simplebank): The server is based on his tutorial series.
