package top.yukonga.update.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
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

        val dropDownList = arrayOf("12", "13", "14")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.dropdown_list_item, dropDownList)

        val codename = findViewById<TextInputLayout>(R.id.code_name)
        codename.editText?.setText("houji")

        val system = findViewById<TextInputLayout>(R.id.system_version)
        system.editText?.setText("OS1.0.25.0.UNCCNXM")

        val android = findViewById<TextInputLayout>(R.id.android_version)
        android.editText?.setText("14")
        (android.editText as? AutoCompleteTextView)?.setAdapter(adapter)
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
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                withContext(Dispatchers.Main) {
                    val romDevice = romInfo.currentRom?.device
                    val romVersion = romInfo.currentRom?.version
                    val romBigVersion = romInfo.currentRom?.bigversion?.replace("816", "HyperOS 1.0")
                    val codebase = romInfo.currentRom?.codebase
                    val romBranch = romInfo.currentRom?.branch
                    val romFileName = romInfo.currentRom?.filename
                    val romFileSize = romInfo.currentRom?.filesize
                    val romChangelog = romInfo.currentRom?.changelog
                    val romMd5 = romInfo.currentRom?.md5

                    val latestRomFileName = romInfo.latestRom?.filename
                    val latestRomMd5 = romInfo.latestRom?.md5

                    if (romBranch == null) Toast.makeText(this@MainActivity, "未获取到任何信息", Toast.LENGTH_SHORT).show()

                    val codeNameV = findViewById<MaterialTextView>(R.id.codename)
                    codeNameV.visibility = if (romDevice != null) View.VISIBLE else View.GONE
                    val codeNameInfo = findViewById<MaterialTextView>(R.id.codename_info)
                    codeNameInfo.visibility = if (romDevice != null) View.VISIBLE else View.GONE
                    codeNameInfo.text = romDevice
                    val systemV = findViewById<MaterialTextView>(R.id.system)
                    systemV.visibility = if (romVersion != null) View.VISIBLE else View.GONE
                    val systemInfo = findViewById<MaterialTextView>(R.id.system_info)
                    systemInfo.visibility = if (romVersion != null) View.VISIBLE else View.GONE
                    systemInfo.text = romVersion
                    val bigVersionV = findViewById<MaterialTextView>(R.id.big_version)
                    bigVersionV.visibility = if (romBigVersion != null) View.VISIBLE else View.GONE
                    val bigVersionInfo = findViewById<MaterialTextView>(R.id.big_version_info)
                    bigVersionInfo.visibility = if (romBigVersion != null) View.VISIBLE else View.GONE
                    bigVersionInfo.text = romBigVersion
                    val codeVaseV = findViewById<MaterialTextView>(R.id.codebase)
                    codeVaseV.visibility = if (codebase != null) View.VISIBLE else View.GONE
                    val codeBaseInfo = findViewById<MaterialTextView>(R.id.codebase_info)
                    codeBaseInfo.visibility = if (codebase != null) View.VISIBLE else View.GONE
                    codeBaseInfo.text = codebase
                    val branchV = findViewById<MaterialTextView>(R.id.branch)
                    branchV.visibility = if (romBranch != null) View.VISIBLE else View.GONE
                    val branchInfo = findViewById<MaterialTextView>(R.id.branch_info)
                    branchInfo.visibility = if (romBranch != null) View.VISIBLE else View.GONE
                    branchInfo.text = romBranch
                    val fileNameV = findViewById<MaterialTextView>(R.id.filename)
                    fileNameV.visibility = if (romFileName != null) View.VISIBLE else View.GONE
                    val fileNameInfo = findViewById<MaterialTextView>(R.id.filename_info)
                    fileNameInfo.visibility = if (romFileName != null) View.VISIBLE else View.GONE
                    fileNameInfo.text = romFileName
                    val fileSizeV = findViewById<MaterialTextView>(R.id.filesize)
                    fileSizeV.visibility = if (romFileSize != null) View.VISIBLE else View.GONE
                    val fileSizeInfo = findViewById<MaterialTextView>(R.id.filesize_info)
                    fileSizeInfo.visibility = if (romFileSize != null) View.VISIBLE else View.GONE
                    fileSizeInfo.text = romFileSize
                    val downloadV = findViewById<MaterialTextView>(R.id.download)
                    downloadV.visibility = if (romMd5 != null) View.VISIBLE else View.GONE
                    val downloadInfo = findViewById<MaterialTextView>(R.id.download_info)
                    downloadInfo.visibility = if (romMd5 != null) View.VISIBLE else View.GONE
                    downloadInfo.text =
                        if (romMd5 == latestRomMd5) "https://ultimateota.d.miui.com/${romVersion}/${latestRomFileName}" else "https://bigota.d.miui.com/${romVersion}/${romFileName}"
                    downloadInfo.setOnClickListener {
                        val clip = ClipData.newPlainText("label", downloadInfo.text)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(this@MainActivity, "链接已复制到剪贴板", Toast.LENGTH_SHORT).show()
                    }
                    val changelogV = findViewById<MaterialTextView>(R.id.changelog)
                    changelogV.visibility = if (romMd5 != null) View.VISIBLE else View.GONE
                    val changelogInfo = findViewById<MaterialTextView>(R.id.changelog_info)
                    changelogInfo.visibility = if (romMd5 != null) View.VISIBLE else View.GONE
                    changelogInfo.text =
                        romChangelog.toString().replace("{", "").replace("=txt=[", "\n").replace("]", "").replace(",", "\n").replace("}", "").replace(" ", "")
                    changelogInfo.setOnClickListener {
                        val clip = ClipData.newPlainText("label", changelogInfo.text)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(this@MainActivity, "更新日志已复制到剪贴板", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}