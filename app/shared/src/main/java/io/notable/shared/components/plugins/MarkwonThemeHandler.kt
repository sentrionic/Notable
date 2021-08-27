package io.notable.shared.components.plugins

import android.graphics.Color
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.core.MarkwonTheme

class MarkwonThemeHandler : AbstractMarkwonPlugin() {
    override fun configureTheme(builder: MarkwonTheme.Builder) {
        builder.linkColor(Color.parseColor("#0366d6"))
    }
}