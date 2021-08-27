-- name: CreateNote :one
INSERT INTO notes (title, body, user_id)
VALUES ($1, $2, $3)
RETURNING *;

-- name: GetNote :one
SELECT *
FROM notes
WHERE id = $1;

-- name: UpdateNote :one
UPDATE notes
SET title      = $2,
    body       = $3,
    updated_at = now()
WHERE id = $1
RETURNING *;

-- name: ListNotes :many
SELECT *
FROM notes
WHERE user_id = @user_id
  AND deleted_at IS NULL
  AND (title LIKE @search
    OR body LIKE @search)
ORDER BY CASE WHEN @created_asc::bool THEN created_at END,
         CASE WHEN @updated_asc::bool THEN updated_at END,
         CASE WHEN @updated_desc::bool THEN updated_at END DESC,
         CASE WHEN TRUE THEN created_at END DESC;


-- name: DeleteNote :one
UPDATE notes
SET deleted_at = now()
WHERE id = $1
RETURNING *;

-- name: DeleteNotes :exec
UPDATE notes
SET deleted_at = now()
WHERE id = ANY ($1::uuid[]);

-- name: GetNotesByIds :many
SELECT *
FROM notes
WHERE id = ANY (@ids::uuid[]);

-- name: GetDeletedNotes :many
SELECT *
FROM notes
WHERE deleted_at IS NOT NULL
  AND user_id = $1;

-- name: PermanentlyDelete :exec
DELETE
FROM notes
WHERE deleted_at < now() - interval '30 days';