package top.yukonga.update.logic.utils.miuiStringToast.data

import kotlinx.serialization.Serializable

@Serializable
class Left(
    var iconParams: IconParams? = null,
    var textParams: TextParams? = null
) {
    override fun toString(): String {
        return "Left{iconParams=$iconParams, textParams=$textParams}"
    }
}
