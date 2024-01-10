package top.yukonga.update.logic.utils.miuiStringToast.data

import kotlinx.serialization.Serializable

@Serializable
class StringToastBean(
    var left: Left? = null,
    var right: Right? = null
) {
    fun getStringToastBundle(): StringToastBundle {
        return StringToastBundle()
    }

    override fun toString(): String {
        return "StringToastBean{left=$left, right=$right}"
    }
}
