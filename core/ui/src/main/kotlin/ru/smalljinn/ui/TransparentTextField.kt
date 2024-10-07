package ru.smalljinn.ui


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun TransparentTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    readOnly: Boolean,
    style: TextStyle,
    hintText: String? = null,
    shouldShowHint: Boolean
) {
    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = onTextChanged,
            readOnly = readOnly,
            textStyle = style,
        )
        hintText?.let {
            if (shouldShowHint) {
                Text(hintText, style = style)
            }
        }
    }
}