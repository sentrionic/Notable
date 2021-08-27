package main

import (
	"database/sql"
	"github.com/gin-gonic/gin"
	"github.com/sentrionic/notable/api"
	"github.com/sentrionic/notable/util"
	"log"

	_ "github.com/lib/pq"
	db "github.com/sentrionic/notable/db/sqlc"
)

func main() {
	gin.SetMode(gin.ReleaseMode)

	config, err := util.LoadConfig(".")
	if err != nil {
		log.Fatal("cannot load config:", err)
	}

	conn, err := sql.Open(config.DBDriver, config.DBSource)

	if err != nil {
		log.Fatal("cannot connect to db:", err)
	}

	store := db.NewStore(conn)
	server, err := api.NewServer(config, store)
	if err != nil {
		log.Fatal("cannot create server:", err)
	}

	err = server.Start(config.ServerAddress)
	if err != nil {
		log.Fatal("cannot start server:", err)
	}
}
