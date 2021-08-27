package api

import (
	"database/sql"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	db "github.com/sentrionic/notable/db/sqlc"
	"github.com/sentrionic/notable/token"
	"net/http"
	"time"
)

type createNoteRequest struct {
	Title string `json:"title" binding:"required,lte=50"`
	Body  string `json:"body"`
}

type noteResponse struct {
	ID        string    `json:"id"`
	Title     string    `json:"title"`
	Body      string    `json:"body"`
	CreatedAt time.Time `json:"createdAt"`
	UpdatedAt time.Time `json:"updatedAt"`
	IsDeleted bool      `json:"isDeleted"`
}

func newNoteResponse(note db.Note) noteResponse {
	return noteResponse{
		ID:        note.ID.String(),
		Title:     note.Title,
		Body:      note.Body,
		CreatedAt: note.CreatedAt,
		UpdatedAt: note.UpdatedAt,
		IsDeleted: note.DeletedAt.Valid,
	}
}

func (server *Server) createNote(ctx *gin.Context) {
	var req createNoteRequest

	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	authPayload := ctx.MustGet(authorizationPayloadKey).(*token.Payload)
	uid, err := uuid.Parse(authPayload.UID)

	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	arg := db.CreateNoteParams{
		Title:  req.Title,
		Body:   req.Body,
		UserID: uid,
	}

	note, err := server.store.CreateNote(ctx, arg)

	if err != nil {
		ctx.JSON(http.StatusInternalServerError, errorResponse(err))
		return
	}

	rsp := newNoteResponse(note)
	ctx.JSON(http.StatusOK, rsp)
}

func (server *Server) listNotes(ctx *gin.Context) {
	authPayload := ctx.MustGet(authorizationPayloadKey).(*token.Payload)
	uid, err := uuid.Parse(authPayload.UID)

	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	search := ctx.Query("search")

	args := db.ListNotesParams{
		UserID: uid,
		Search: "%" + search + "%",
	}

	notes, err := server.store.ListNotes(ctx, args)

	if err != nil {
		ctx.JSON(http.StatusInternalServerError, errorResponse(err))
		return
	}

	response := make([]noteResponse, 0)

	if len(notes) > 0 {
		for _, note := range notes {
			rsp := newNoteResponse(note)
			response = append(response, rsp)
		}
	}

	ctx.JSON(http.StatusOK, response)
}

type updateNoteRequest struct {
	Title string `json:"title" binding:"required,lte=50"`
	Body  string `json:"body" binding:"required"`
}

func (server *Server) editNote(ctx *gin.Context) {
	var req updateNoteRequest

	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	authPayload := ctx.MustGet(authorizationPayloadKey).(*token.Payload)
	uid, err := uuid.Parse(authPayload.UID)

	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	noteId := ctx.Param("id")
	nid, err := uuid.Parse(noteId)

	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	note, err := server.store.GetNote(ctx, nid)

	if err != nil {
		if err == sql.ErrNoRows {
			ctx.JSON(http.StatusNotFound, errorResponse(err))
			return
		}

		ctx.JSON(http.StatusInternalServerError, errorResponse(err))
		return
	}

	if note.UserID != uid {
		ctx.JSON(http.StatusForbidden, gin.H{"error": "You are not the owner of the given note"})
		return
	}

	arg := db.UpdateNoteParams{
		ID:    note.ID,
		Title: req.Title,
		Body:  req.Body,
	}

	updatedNote, err := server.store.UpdateNote(ctx, arg)

	if err != nil {
		ctx.JSON(http.StatusInternalServerError, errorResponse(err))
		return
	}

	ctx.JSON(http.StatusOK, newNoteResponse(updatedNote))
}

func (server *Server) deleteNote(ctx *gin.Context) {
	authPayload := ctx.MustGet(authorizationPayloadKey).(*token.Payload)
	uid, err := uuid.Parse(authPayload.UID)

	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	noteId := ctx.Param("id")
	nid, err := uuid.Parse(noteId)

	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	note, err := server.store.GetNote(ctx, nid)

	if err != nil {
		if err == sql.ErrNoRows {
			ctx.JSON(http.StatusNotFound, errorResponse(err))
			return
		}

		ctx.JSON(http.StatusInternalServerError, errorResponse(err))
		return
	}

	if note.UserID != uid {
		ctx.JSON(http.StatusForbidden, gin.H{"error": "You are not the owner of the given note"})
		return
	}

	deletedNote, err := server.store.DeleteNote(ctx, note.ID)

	if err != nil {
		ctx.JSON(http.StatusInternalServerError, errorResponse(err))
		return
	}

	ctx.JSON(http.StatusOK, newNoteResponse(deletedNote))
}

type deleteNotesRequest struct {
	IDs []string `json:"ids" binding:"required"`
}

func (server *Server) deleteNotes(ctx *gin.Context) {
	var req deleteNotesRequest

	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	authPayload := ctx.MustGet(authorizationPayloadKey).(*token.Payload)
	uid, err := uuid.Parse(authPayload.UID)

	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	ids := make([]uuid.UUID, 0)

	for _, id := range req.IDs {
		guid, err := uuid.Parse(id)
		if err != nil {
			ctx.JSON(http.StatusBadRequest, errorResponse(err))
			return
		}
		ids = append(ids, guid)
	}

	notes, err := server.store.GetNotesByIds(ctx, ids)

	if err != nil {
		if err == sql.ErrNoRows {
			ctx.JSON(http.StatusNotFound, errorResponse(err))
			return
		}

		ctx.JSON(http.StatusInternalServerError, errorResponse(err))
		return
	}

	for _, note := range notes {
		if note.UserID != uid {
			ctx.JSON(http.StatusForbidden, gin.H{"error": "You are not the owner of one of the given notes"})
			return
		}
	}

	err = server.store.DeleteNotes(ctx, ids)

	if err != nil {
		ctx.JSON(http.StatusInternalServerError, errorResponse(err))
		return
	}

	ctx.JSON(http.StatusOK, gin.H{"message": "Successfully deleted all notes"})
}

func (server *Server) getDeletedNotes(ctx *gin.Context) {
	authPayload := ctx.MustGet(authorizationPayloadKey).(*token.Payload)
	uid, err := uuid.Parse(authPayload.UID)

	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	notes, err := server.store.GetDeletedNotes(ctx, uid)

	if err != nil {
		ctx.JSON(http.StatusInternalServerError, errorResponse(err))
		return
	}

	response := make([]noteResponse, 0)

	if len(notes) > 0 {
		for _, note := range notes {
			rsp := newNoteResponse(note)
			response = append(response, rsp)
		}
	}

	ctx.JSON(http.StatusOK, response)
}
