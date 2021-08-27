package io.notable.note_datasource_test.network

sealed class NoteServiceResponseTypeList {

    object EmptyList : NoteServiceResponseTypeList()

    object MalformedData : NoteServiceResponseTypeList()

    object GoodData : NoteServiceResponseTypeList()

    object GoodSingleData : NoteServiceResponseTypeList()

    object Unauthorized : NoteServiceResponseTypeList()

    object ServerError : NoteServiceResponseTypeList()
}