// Code generated by sqlc. DO NOT EDIT.

package db

import (
	"context"

	"github.com/google/uuid"
)

type Querier interface {
	CreateNote(ctx context.Context, arg CreateNoteParams) (Note, error)
	CreateUser(ctx context.Context, arg CreateUserParams) (User, error)
	DeleteNote(ctx context.Context, id uuid.UUID) (Note, error)
	DeleteNotes(ctx context.Context, dollar_1 []uuid.UUID) error
	GetDeletedNotes(ctx context.Context, userID uuid.UUID) ([]Note, error)
	GetNote(ctx context.Context, id uuid.UUID) (Note, error)
	GetNotesByIds(ctx context.Context, ids []uuid.UUID) ([]Note, error)
	GetUserByEmail(ctx context.Context, email string) (User, error)
	GetUserById(ctx context.Context, id uuid.UUID) (User, error)
	ListNotes(ctx context.Context, arg ListNotesParams) ([]Note, error)
	PermanentlyDelete(ctx context.Context) error
	UpdateNote(ctx context.Context, arg UpdateNoteParams) (Note, error)
}

var _ Querier = (*Queries)(nil)