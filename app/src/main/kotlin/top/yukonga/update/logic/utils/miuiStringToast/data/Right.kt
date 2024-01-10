package top.yukonga.update.logic.utils.miuiStringToast.data

import kotlinx.serialization.Serializable

@Serializable
class Right(
    var iconParams: IconParams? = null,
    var textParams: TextParams? = null
) {
    override fun toString(): String {
        return "Right{iconParams=$iconParams, textParams=$textParams}"
    }
}
