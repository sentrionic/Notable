package io.notable.core.domain

sealed class UIComponent {

    data class Dialog(
        val title: String,
        val description: String,
    ) : UIComponent()

    data class SnackBar(
        val message: String,
    ) : UIComponent()

    class AreYouSureDialog(
        val message: String,
        val description: String,
        val callback: AreYouSureCallback
    ) : UIComponent()

    data class None(
        val message: String,
    ) : UIComponent()
}

interface AreYouSureCallback {

    fun proceed()

    fun cancel()
}
