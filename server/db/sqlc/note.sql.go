// Code generated by sqlc. DO NOT EDIT.
// source: note.sql

package db

import (
	"context"

	"github.com/google/uuid"
	"github.com/lib/pq"
)

const createNote = `-- name: CreateNote :one
INSERT INTO notes (title, body, user_id)
VALUES ($1, $2, $3)
RETURNING id, title, body, user_id, created_at, updated_at, deleted_at
`

type CreateNoteParams struct {
	Title  string    `json:"title"`
	Body   string    `json:"body"`
	UserID uuid.UUID `json:"user_id"`
}

func (q *Queries) CreateNote(ctx context.Context, arg CreateNoteParams) (Note, error) {
	row := q.db.QueryRowContext(ctx, createNote, arg.Title, arg.Body, arg.UserID)
	var i Note
	err := row.Scan(
		&i.ID,
		&i.Title,
		&i.Body,
		&i.UserID,
		&i.CreatedAt,
		&i.UpdatedAt,
		&i.DeletedAt,
	)
	return i, err
}

const deleteNote = `-- name: DeleteNote :one
UPDATE notes
SET deleted_at = now()
WHERE id = $1
RETURNING id, title, body, user_id, created_at, updated_at, deleted_at
`

func (q *Queries) DeleteNote(ctx context.Context, id uuid.UUID) (Note, error) {
	row := q.db.QueryRowContext(ctx, deleteNote, id)
	var i Note
	err := row.Scan(
		&i.ID,
		&i.Title,
		&i.Body,
		&i.UserID,
		&i.CreatedAt,
		&i.UpdatedAt,
		&i.DeletedAt,
	)
	return i, err
}

const deleteNotes = `-- name: DeleteNotes :exec
UPDATE notes
SET deleted_at = now()
WHERE id = ANY ($1::uuid[])
`

func (q *Queries) DeleteNotes(ctx context.Context, dollar_1 []uuid.UUID) error {
	_, err := q.db.ExecContext(ctx, deleteNotes, pq.Array(dollar_1))
	return err
}

const getDeletedNotes = `-- name: GetDeletedNotes :many
SELECT id, title, body, user_id, created_at, updated_at, deleted_at
FROM notes
WHERE deleted_at IS NOT NULL
  AND user_id = $1
`

func (q *Queries) GetDeletedNotes(ctx context.Context, userID uuid.UUID) ([]Note, error) {
	rows, err := q.db.QueryContext(ctx, getDeletedNotes, userID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var items []Note
	for rows.Next() {
		var i Note
		if err := rows.Scan(
			&i.ID,
			&i.Title,
			&i.Body,
			&i.UserID,
			&i.CreatedAt,
			&i.UpdatedAt,
			&i.DeletedAt,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Close(); err != nil {
		return nil, err
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const getNote = `-- name: GetNote :one
SELECT id, title, body, user_id, created_at, updated_at, deleted_at
FROM notes
WHERE id = $1
`

func (q *Queries) GetNote(ctx context.Context, id uuid.UUID) (Note, error) {
	row := q.db.QueryRowContext(ctx, getNote, id)
	var i Note
	err := row.Scan(
		&i.ID,
		&i.Title,
		&i.Body,
		&i.UserID,
		&i.CreatedAt,
		&i.UpdatedAt,
		&i.DeletedAt,
	)
	return i, err
}

const getNotesByIds = `-- name: GetNotesByIds :many
SELECT id, title, body, user_id, created_at, updated_at, deleted_at
FROM notes
WHERE id = ANY ($1::uuid[])
`

func (q *Queries) GetNotesByIds(ctx context.Context, ids []uuid.UUID) ([]Note, error) {
	rows, err := q.db.QueryContext(ctx, getNotesByIds, pq.Array(ids))
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var items []Note
	for rows.Next() {
		var i Note
		if err := rows.Scan(
			&i.ID,
			&i.Title,
			&i.Body,
			&i.UserID,
			&i.CreatedAt,
			&i.UpdatedAt,
			&i.DeletedAt,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Close(); err != nil {
		return nil, err
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const listNotes = `-- name: ListNotes :many
SELECT id, title, body, user_id, created_at, updated_at, deleted_at
FROM notes
WHERE user_id = $1
  AND deleted_at IS NULL
  AND (title LIKE $2
    OR body LIKE $2)
ORDER BY CASE WHEN $3::bool THEN created_at END,
         CASE WHEN $4::bool THEN updated_at END,
         CASE WHEN $5::bool THEN updated_at END DESC,
         CASE WHEN TRUE THEN created_at END DESC
`

type ListNotesParams struct {
	UserID      uuid.UUID `json:"user_id"`
	Search      string    `json:"search"`
	CreatedAsc  bool      `json:"created_asc"`
	UpdatedAsc  bool      `json:"updated_asc"`
	UpdatedDesc bool      `json:"updated_desc"`
}

func (q *Queries) ListNotes(ctx context.Context, arg ListNotesParams) ([]Note, error) {
	rows, err := q.db.QueryContext(ctx, listNotes,
		arg.UserID,
		arg.Search,
		arg.CreatedAsc,
		arg.UpdatedAsc,
		arg.UpdatedDesc,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var items []Note
	for rows.Next() {
		var i Note
		if err := rows.Scan(
			&i.ID,
			&i.Title,
			&i.Body,
			&i.UserID,
			&i.CreatedAt,
			&i.UpdatedAt,
			&i.DeletedAt,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Close(); err != nil {
		return nil, err
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const permanentlyDelete = `-- name: PermanentlyDelete :exec
DELETE
FROM notes
WHERE deleted_at < now() - interval '30 days'
`

func (q *Queries) PermanentlyDelete(ctx context.Context) error {
	_, err := q.db.ExecContext(ctx, permanentlyDelete)
	return err
}

const updateNote = `-- name: UpdateNote :one
UPDATE notes
SET title      = $2,
    body       = $3,
    updated_at = now()
WHERE id = $1
RETURNING id, title, body, user_id, created_at, updated_at, deleted_at
`

type UpdateNoteParams struct {
	ID    uuid.UUID `json:"id"`
	Title string    `json:"title"`
	Body  string    `json:"body"`
}

func (q *Queries) UpdateNote(ctx context.Context, arg UpdateNoteParams) (Note, error) {
	row := q.db.QueryRowContext(ctx, updateNote, arg.ID, arg.Title, arg.Body)
	var i Note
	err := row.Scan(
		&i.ID,
		&i.Title,
		&i.Body,
		&i.UserID,
		&i.CreatedAt,
		&i.UpdatedAt,
		&i.DeletedAt,
	)
	return i, err
}
