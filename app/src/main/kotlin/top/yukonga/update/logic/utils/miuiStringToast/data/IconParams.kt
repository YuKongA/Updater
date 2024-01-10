package top.yukonga.update.logic.utils.miuiStringToast.data

import kotlinx.serialization.Serializable

@Serializable
class IconParams(
    var category: String? = null,
    var iconFormat: String? = null,
    var iconResName: String? = null,
    var iconType: Int = 0
) {
    override fun toString(): String {
        return "IconParams{category='$category', iconFormat='$iconFormat', iconResName='$iconResName', iconType=$iconType}"
    }
}