package io.notable.shared.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import coil.ImageLoader
import io.notable.shared.components.plugins.BlockQuoteEditHandler
import io.notable.shared.components.plugins.CodeEditHandler
import io.notable.shared.components.plugins.HeadingEditHandler
import io.notable.shared.components.plugins.StrikethroughEditHandler
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import io.noties.markwon.editor.handler.EmphasisEditHandler
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import java.util.concurrent.Executors

@Composable
fun MarkdownEditor(
    handleChange: (String) -> Unit,
    value: String?,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    hintText: String? = "",
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    @FontRes fontResource: Int? = null,
    style: TextStyle = LocalTextStyle.current,
    @IdRes viewId: Int? = null
) {
    val defaultColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    val context: Context = LocalContext.current
    val markdownRender: Markwon = remember { createMarkdownRender(context) }
    val editor: MarkwonEditor = remember {
        MarkwonEditor
            .builder(markdownRender)
            .useEditHandler(EmphasisEditHandler())
            .useEditHandler(StrongEmphasisEditHandler())
            .useEditHandler(HeadingEditHandler())
            .useEditHandler(BlockQuoteEditHandler())
            .useEditHandler(CodeEditHandler())
            .useEditHandler(StrikethroughEditHandler())
            .build()
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            createEditTextView(
                value = value,
                context = ctx,
                color = color,
                defaultColor = defaultColor,
                fontSize = fontSize,
                fontResource = fontResource,
                maxLines = maxLines,
                style = style,
                textAlign = textAlign,
                hintText = hintText,
                viewId = viewId
            )
        },
        update = { editText ->
            editText.addTextChangedListener(
                MarkwonEditorTextWatcher.withPreRender(
                    editor, Executors.newCachedThreadPool(),
                    editText
                )
            )

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    handleChange(editText.text.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }
    )
}

private const val IMAGE_MEMORY_PERCENTAGE = 0.5

private fun createEditTextView(
    value: String?,
    context: Context,
    color: Color = Color.Unspecified,
    defaultColor: Color,
    fontSize: TextUnit = TextUnit.Unspecified,
    hintText: String? = "",
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    @FontRes fontResource: Int? = null,
    style: TextStyle,
    @IdRes viewId: Int? = null
): EditText {

    val textColor = color.takeOrElse { style.color.takeOrElse { defaultColor } }
    val mergedStyle = style.merge(
        TextStyle(
            color = textColor,
            fontSize = fontSize,
            textAlign = textAlign,
        )
    )
    return EditText(context).apply {

        setText(value)
        hint = hintText
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        setTextColor(textColor.toArgb())
        setMaxLines(maxLines)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, mergedStyle.fontSize.value)

        viewId?.let { id = viewId }
        textAlign?.let { align ->
            textAlignment = when (align) {
                TextAlign.Left, TextAlign.Start -> View.TEXT_ALIGNMENT_TEXT_START
                TextAlign.Right, TextAlign.End -> View.TEXT_ALIGNMENT_TEXT_END
                TextAlign.Center -> View.TEXT_ALIGNMENT_CENTER
                else -> View.TEXT_ALIGNMENT_TEXT_START
            }
        }

        fontResource?.let { font ->
            typeface = ResourcesCompat.getFont(context, font)
        }
    }
}

private fun createMarkdownRender(context: Context): Markwon {
    val imageLoader = ImageLoader.Builder(context)
        .apply {
            availableMemoryPercentage(IMAGE_MEMORY_PERCENTAGE)
            bitmapPoolPercentage(IMAGE_MEMORY_PERCENTAGE)
            crossfade(true)
        }.build()

    return Markwon.builder(context)
        .usePlugin(SoftBreakAddsNewLinePlugin.create())
        .usePlugin(TaskListPlugin.create(context))
        .usePlugin(CoilImagesPlugin.create(context, imageLoader))
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(TablePlugin.create(context))
        .build()
}