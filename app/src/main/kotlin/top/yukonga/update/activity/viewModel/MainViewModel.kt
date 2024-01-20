package top.yukonga.update.activity.viewModel

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var device: String? = null
    var version: String? = null
    var codebase: String? = null
    var branch: String? = null
    var filename: String? = null
    var filesize: String? = null
    var bigversion: String? = null
    var officialDownload: String? = null
    var officialText: String? = null
    var cdn1Download: String? = null
    var cdn2Download: String? = null
    var cdn3Download: String? = null
    var changelog: String? = null
}
