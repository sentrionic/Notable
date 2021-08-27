package io.notable.note_datasource_test.network

sealed class NoteServiceResponseTypeSingle {

    object GoodData : NoteServiceResponseTypeSingle()

    object GoodUpdatedData : NoteServiceResponseTypeSingle()

    object Http404 : NoteServiceResponseTypeSingle()

    object Unauthorized : NoteServiceResponseTypeSingle()

    object ServerError : NoteServiceResponseTypeSingle()
}