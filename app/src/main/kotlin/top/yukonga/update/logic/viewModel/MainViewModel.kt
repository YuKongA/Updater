package top.yukonga.update.logic.viewModel

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
    var cdnDownload: String? = null
    var changelog: String? = null
}
