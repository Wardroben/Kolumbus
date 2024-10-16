package ru.smalljinn.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
fun TransparentTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    readOnly: Boolean,
    style: TextStyle,
    hintText: String? = null,
    shouldShowHint: Boolean,
    imeAction: ImeAction = ImeAction.Default
) {
    val contentColor = LocalContentColor.current
    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = onTextChanged,
            readOnly = readOnly,
            textStyle = style.copy(color = contentColor),
            cursorBrush = SolidColor(contentColor),
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction,
                capitalization = KeyboardCapitalization.Sentences
            ),
            modifier = Modifier
                .fillMaxWidth()

        )
        hintText?.let {
            if (shouldShowHint) {
                Text(hintText, style = style.copy(color = contentColor.copy(alpha = 0.6f)))
            }
        }
    }
}