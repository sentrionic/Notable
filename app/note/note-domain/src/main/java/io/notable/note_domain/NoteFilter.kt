package io.notable.note_domain

import io.notable.core.domain.FilterOrder

sealed class NoteFilter(val uiValue: String) {

    data class Title(
        val order: FilterOrder = FilterOrder.Descending
    ) : NoteFilter("Title")

    data class UpdatedAt(
        val order: FilterOrder = FilterOrder.Descending
    ) : NoteFilter("Last Updated")

}