package top.yukonga.update.logic.utils.miuiStringToast.data

import kotlinx.serialization.Serializable

@Serializable
data class TextParams(
    var text: String? = null,
    var textColor: Int = 0
) {
    override fun toString(): String {
        return "TextParams{text='$text', textColor=$textColor}"
    }
}
