package top.yukonga.update.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.update.R
import top.yukonga.update.data.RomInfo
import top.yukonga.update.utils.JsonUtils.parseJSON
import top.yukonga.update.utils.Utils

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val codename = findViewById<TextInputLayout>(R.id.code_name)
        codename.editText?.setText("houji")

        val system = findViewById<TextInputLayout>(R.id.system_version)
        system.editText?.setText("OS1.0.25.0.UNCCNXM")

        val android = findViewById<TextInputLayout>(R.id.android_version)
        android.editText?.setText("14")
    }

    override fun onResume() {
        super.onResume()
        val codename = findViewById<TextInputLayout>(R.id.code_name)
        var codenameText = codename.editText?.text.toString()
        codename.editText?.doAfterTextChanged {
            codenameText = it.toString()
        }

        val system = findViewById<TextInputLayout>(R.id.system_version)
        var systemText = system.editText?.text.toString()
        system.editText?.doAfterTextChanged {
            systemText = it.toString()
        }

        val android = findViewById<TextInputLayout>(R.id.android_version)
        var androidText = android.editText?.text.toString()
        android.editText?.doAfterTextChanged {
            androidText = it.toString()
        }

        val implement = findViewById<ExtendedFloatingActionButton>(R.id.implement)
        implement.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                val romInfo = Utils.getRomInfo(codenameText, systemText, androidText).parseJSON<RomInfo>()
                withContext(Dispatchers.Main) {
                    val romDevice = romInfo.currentRom.device
                    val romVersion = romInfo.currentRom.version
                    val romBigVersion = romInfo.currentRom.bigversion.replace("816", "HyperOS 1.0")
                    val codebase = romInfo.currentRom.codebase
                    val romBranch = romInfo.currentRom.branch
                    val romFileName = romInfo.currentRom.filename
                    val romFileSize = romInfo.currentRom.filesize
                    val romMd5 = romInfo.currentRom.md5
                    val latestRomMd5 = romInfo.LatestRom.md5
                    val latestRomFileName = romInfo.LatestRom.filename

                    val romChangelog = romInfo.currentRom.changelog.toString()

                    val codeNameV = findViewById<MaterialTextView>(R.id.codename)
                    codeNameV.visibility = View.VISIBLE
                    val codeNameInfo = findViewById<MaterialTextView>(R.id.codename_info)
                    codeNameInfo.text = romDevice
                    textViewScrolling(codeNameInfo)
                    val systemV = findViewById<MaterialTextView>(R.id.system)
                    systemV.visibility = View.VISIBLE
                    val systemInfo = findViewById<MaterialTextView>(R.id.system_info)
                    systemInfo.text = romVersion
                    textViewScrolling(systemInfo)
                    val bigVersionV = findViewById<MaterialTextView>(R.id.big_version)
                    bigVersionV.visibility = View.VISIBLE
                    val bigVersionInfo = findViewById<MaterialTextView>(R.id.big_version_info)
                    bigVersionInfo.text = romBigVersion
                    textViewScrolling(bigVersionInfo)
                    val codeVaseV = findViewById<MaterialTextView>(R.id.codebase)
                    codeVaseV.visibility = View.VISIBLE
                    val codeBaseInfo = findViewById<MaterialTextView>(R.id.codebase_info)
                    codeBaseInfo.text = codebase
                    textViewScrolling(codeBaseInfo)
                    val branchV = findViewById<MaterialTextView>(R.id.branch)
                    branchV.visibility = View.VISIBLE
                    val branchInfo = findViewById<MaterialTextView>(R.id.branch_info)
                    branchInfo.text = romBranch
                    textViewScrolling(branchInfo)
                    val fileNameV = findViewById<MaterialTextView>(R.id.filename)
                    fileNameV.visibility = View.VISIBLE
                    val fileNameInfo = findViewById<MaterialTextView>(R.id.filename_info)
                    fileNameInfo.text = romFileName
                    val fileSizeV = findViewById<MaterialTextView>(R.id.filesize)
                    fileSizeV.visibility = View.VISIBLE
                    val fileSizeInfo = findViewById<MaterialTextView>(R.id.filesize_info)
                    fileSizeInfo.text = romFileSize
                    textViewScrolling(fileSizeInfo)
                    val downloadV = findViewById<MaterialTextView>(R.id.download)
                    downloadV.visibility = View.VISIBLE
                    val downloadInfo = findViewById<MaterialTextView>(R.id.download_info)
                    downloadInfo.text = if (romMd5 == latestRomMd5) "https://ultimateota.d.miui.com/${romVersion}/${latestRomFileName}" else "https://bigota.d.miui.com/${romVersion}/${romFileName}"
                    val changelogV = findViewById<MaterialTextView>(R.id.changelog)
                    changelogV.visibility = View.VISIBLE
                    val changelogInfo = findViewById<MaterialTextView>(R.id.changelog_info)
                    changelogInfo.text = romChangelog
                }
            }
        }
    }
}

private fun textViewScrolling(textView: MaterialTextView) {
    textView.ellipsize = TextUtils.TruncateAt.MARQUEE
    textView.isHorizontalFadingEdgeEnabled = true
    textView.setSingleLine()
    textView.marqueeRepeatLimit = -1
    textView.isSelected = true
    textView.setHorizontallyScrolling(true)
}