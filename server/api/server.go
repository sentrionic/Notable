package api

import (
	"fmt"
	"github.com/gin-gonic/gin"
	cors "github.com/rs/cors/wrapper/gin"
	db "github.com/sentrionic/notable/db/sqlc"
	"github.com/sentrionic/notable/token"
	"github.com/sentrionic/notable/util"
)

// Server serves HTTP requests for our banking service.
type Server struct {
	config     util.Config
	store      db.Store
	tokenMaker token.Maker
	router     *gin.Engine
}

// NewServer creates a new HTTP server and set up routing.
func NewServer(config util.Config, store db.Store) (*Server, error) {
	tokenMaker, err := token.NewPasetoMaker(config.TokenSymmetricKey)
	if err != nil {
		return nil, fmt.Errorf("cannot create token maker: %w", err)
	}

	server := &Server{
		config:     config,
		store:      store,
		tokenMaker: tokenMaker,
	}

	server.setupRouter()
	return server, nil
}

func (server *Server) setupRouter() {
	router := gin.Default()

	// set cors settings
	c := cors.New(cors.Options{
		AllowedOrigins:   []string{"*"},
		AllowCredentials: true,
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE"},
		AllowedHeaders:   []string{"*"},
	})
	router.Use(c)

	router.POST("/accounts", server.register)
	router.POST("/accounts/login", server.login)

	authRoutes := router.Group("/").Use(authMiddleware(server.tokenMaker))

	authRoutes.GET("/notes", server.listNotes)
	authRoutes.POST("/notes", server.createNote)
	authRoutes.PUT("/notes/:id", server.editNote)
	authRoutes.DELETE("/notes/:id", server.deleteNote)
	authRoutes.DELETE("/notes", server.deleteNotes)

	authRoutes.GET("/notes/deleted", server.getDeletedNotes)

	server.router = router
}

// Start runs the HTTP server on a specific address.
func (server *Server) Start(address string) error {
	return server.router.Run(address)
}

func errorResponse(err error) gin.H {
	return gin.H{"error": err.Error()}
}
