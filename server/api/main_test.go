package api

import (
	"github.com/gin-gonic/gin"
	db "github.com/sentrionic/notable/db/sqlc"
	"github.com/sentrionic/notable/util"
	"github.com/stretchr/testify/require"
	"os"
	"testing"
)

func newTestServer(t *testing.T, store db.Store) *Server {
	config := util.Config{
		TokenSymmetricKey: util.RandomString(32),
	}

	server, err := NewServer(config, store)
	require.NoError(t, err)

	return server
}

func TestMain(m *testing.M) {
	gin.SetMode(gin.TestMode)

	os.Exit(m.Run())
}
