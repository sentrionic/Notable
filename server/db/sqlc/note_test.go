package db

import (
	"context"
	"github.com/google/uuid"
	"github.com/sentrionic/notable/util"
	"testing"
	"time"

	"github.com/stretchr/testify/require"
)

func createRandomNote(t *testing.T, userId uuid.UUID) Note {

	arg := CreateNoteParams{
		Title:  util.RandomString(6),
		Body:   util.RandomString(100),
		UserID: userId,
	}

	note, err := testQueries.CreateNote(context.Background(), arg)

	require.NoError(t, err)
	require.NotEmpty(t, note)

	require.Equal(t, arg.Title, note.Title)
	require.Equal(t, arg.Body, note.Body)
	require.Equal(t, arg.UserID, note.UserID)

	require.NotZero(t, note.ID)
	require.NotZero(t, note.CreatedAt)
	require.NotZero(t, note.UpdatedAt)

	return note
}

func TestQueries_CreateNote(t *testing.T) {
	user := createRandomUser(t)
	createRandomNote(t, user.ID)
}

func TestQueries_UpdateNote(t *testing.T) {
	user := createRandomUser(t)
	note1 := createRandomNote(t, user.ID)

	arg := UpdateNoteParams{
		ID:    note1.ID,
		Title: util.RandomString(8),
		Body:  util.RandomString(100),
	}

	note2, err := testQueries.UpdateNote(context.Background(), arg)
	require.NoError(t, err)
	require.NotEmpty(t, note2)

	require.Equal(t, note1.ID, note2.ID)
	require.Equal(t, note1.UserID, note2.UserID)
	require.Equal(t, arg.Title, note2.Title)
	require.Equal(t, arg.Body, note2.Body)
	require.WithinDuration(t, note1.CreatedAt, note2.CreatedAt, time.Second)
}

func TestQueries_DeleteNote(t *testing.T) {
	user := createRandomUser(t)
	note1 := createRandomNote(t, user.ID)
	deletedNote, err := testQueries.DeleteNote(context.Background(), note1.ID)
	require.NoError(t, err)
	require.NotEmpty(t, deletedNote)
	require.Equal(t, note1.ID, deletedNote.ID)

	note2, err := testQueries.GetNote(context.Background(), note1.ID)
	require.NoError(t, err)
	require.NotEmpty(t, note2)

	require.NotNil(t, note2.DeletedAt)
	require.Equal(t, note2.ID, deletedNote.ID)
}

func TestQueries_ListNotes(t *testing.T) {
	user := createRandomUser(t)

	for i := 0; i < 10; i++ {
		createRandomNote(t, user.ID)
	}

	args := ListNotesParams{
		UserID: user.ID,
		Search: "%%",
	}

	notes, err := testQueries.ListNotes(context.Background(), args)
	require.NoError(t, err)
	require.Len(t, notes, 10)

	for _, note := range notes {
		require.NotEmpty(t, note)
	}
}

func TestQueries_DeleteNotes(t *testing.T) {
	user := createRandomUser(t)
	ids := make([]uuid.UUID, 0)

	for i := 0; i < 10; i++ {
		note := createRandomNote(t, user.ID)
		ids = append(ids, note.ID)
	}

	err := testQueries.DeleteNotes(context.Background(), ids)
	require.NoError(t, err)

	args := ListNotesParams{
		UserID: user.ID,
		Search: "%%",
	}

	notes, err := testQueries.ListNotes(context.Background(), args)
	require.NoError(t, err)
	require.Len(t, notes, 0)
}

func TestQueries_GetNotesByIds(t *testing.T) {
	user := createRandomUser(t)
	ids := make([]uuid.UUID, 0)

	for i := 0; i < 10; i++ {
		note := createRandomNote(t, user.ID)
		ids = append(ids, note.ID)
	}

	notes, err := testQueries.GetNotesByIds(context.Background(), ids)
	require.NoError(t, err)
	require.Len(t, notes, 10)
}

func TestQueries_GetDeletedNotes(t *testing.T) {
	user := createRandomUser(t)
	notes := make([]Note, 0)

	for i := 0; i < 10; i++ {
		note := createRandomNote(t, user.ID)
		notes = append(notes, note)

		_, err := testQueries.DeleteNote(context.Background(), note.ID)
		require.NoError(t, err)
	}

	deletedNotes, err := testQueries.GetDeletedNotes(context.Background(), user.ID)
	require.NoError(t, err)
	require.Len(t, notes, 10)

	valid := false
	for _, deletedNote := range deletedNotes {
		require.NotEmpty(t, deletedNote)
		for _, note := range notes {
			if note.ID == deletedNote.ID {
				valid = true
				break
			}
			valid = false
		}
	}

	require.True(t, valid)
}

