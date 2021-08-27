package io.notable.shared.session

import io.notable.core.domain.KQueue
import io.notable.core.domain.UIComponent

data class SessionState(
    val isLoading: Boolean = false,
    val authToken: String? = null,
    val didCheckForPreviousAuthUser: Boolean = false,
    val queue: KQueue<UIComponent> = KQueue(mutableListOf()),
)