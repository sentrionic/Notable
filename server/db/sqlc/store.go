package db

import "database/sql"

type Store interface {
	Querier
}

// SQLStore provides all functions to execute db queries and transaction
type SQLStore struct {
	db *sql.DB
	*Queries
}

// NewStore creates a new store
func NewStore(db *sql.DB) Store {
	return &SQLStore{
		db:      db,
		Queries: New(db),
	}
}
