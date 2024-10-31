package ru.smalljinn.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

private const val MAX_EDITABLE_DESCRIPTION_TEXT_FIELD_LINES = 4

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
    var maxLines by rememberSaveable { mutableIntStateOf(maxLines(readOnly)) }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        TextField(
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            value = text,
            onValueChange = onTextChanged,
            readOnly = readOnly,
            textStyle = style.copy(color = contentColor),
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction,
                capitalization = KeyboardCapitalization.Sentences
            ),
            maxLines = maxLines,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .onGloballyPositioned { maxLines = maxLines(readOnly) }
                .fillMaxWidth()
                .animateContentSize()
        )
        hintText?.let {
            if (shouldShowHint) {
                Text(hintText, style = style.copy(color = contentColor.copy(alpha = 0.6f)))
            }
        }
    }
}

private fun maxLines(readOnly: Boolean) = if (!readOnly) {
    MAX_EDITABLE_DESCRIPTION_TEXT_FIELD_LINES
} else {
    Int.MAX_VALUE
}