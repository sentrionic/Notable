package io.notable.note_interactors

import io.notable.core.domain.FilterOrder
import io.notable.note_domain.Note
import io.notable.note_domain.NoteFilter

class FilterNotes {

    fun execute(
        current: List<Note>,
        query: String,
        noteFilter: NoteFilter,
    ): List<Note> {
        val filteredList: MutableList<Note> = current.filter {
            it.title.lowercase().contains(query.lowercase())
                    || it.body.lowercase().contains(query.lowercase())
        }.toMutableList()

        when (noteFilter) {
            is NoteFilter.Title -> {
                when (noteFilter.order) {
                    is FilterOrder.Descending -> {
                        filteredList.sortByDescending { it.title.lowercase() }
                    }
                    is FilterOrder.Ascending -> {
                        filteredList.sortBy { it.title.lowercase() }
                    }
                }
            }
            is NoteFilter.UpdatedAt -> {
                when (noteFilter.order) {
                    is FilterOrder.Descending -> {
                        filteredList.sortByDescending { it.updatedAt }
                    }
                    is FilterOrder.Ascending -> {
                        filteredList.sortBy { it.updatedAt }
                    }
                }
            }
        }

        return filteredList
    }
}