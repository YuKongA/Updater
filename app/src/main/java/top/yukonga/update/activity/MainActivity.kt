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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.update.R
import top.yukonga.update.data.RomInfo
import top.yukonga.update.databinding.ActivityMainBinding
import top.yukonga.update.databinding.MainContentBinding
import top.yukonga.update.utils.JsonUtils.parseJSON
import top.yukonga.update.utils.Utils

class MainActivity : AppCompatActivity() {


    private var _activityMainBinding: ActivityMainBinding? = null
    private val activityMainBinding get() = _activityMainBinding!!
    private val mainContentBinding: MainContentBinding get() = _activityMainBinding!!.mainContent

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        val dropDownList = arrayOf("12", "13", "14")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.dropdown_list_item, dropDownList)
        mainContentBinding.apply {
            codeName.editText?.setText("houji")
            systemVersion.editText?.setText("OS1.0.25.0.UNCCNXM")
            androidVersion.editText?.setText("14")
            (androidVersion.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }
    }

    override fun onResume() {
        super.onResume()
        mainContentBinding.apply {
            activityMainBinding.implement.setOnClickListener {
                CoroutineScope(Dispatchers.Default).launch {
                    val codenameText = codeName.editText?.text.toString()
                    val systemText = systemVersion.editText?.text.toString()
                    val androidText = androidVersion.editText?.text.toString()
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
                        codename.visibility = if (romDevice != null) View.VISIBLE else View.GONE
                        codenameInfo.apply {
                            visibility = if (romDevice != null) View.VISIBLE else View.GONE
                            text = romDevice
                        }
                        system.visibility = if (romVersion != null) View.VISIBLE else View.GONE
                        systemInfo.apply {
                            visibility = if (romVersion != null) View.VISIBLE else View.GONE
                            text = romVersion
                        }
                        bigVersion.visibility = if (romBigVersion != null) View.VISIBLE else View.GONE
                        bigVersionInfo.apply {
                            visibility = if (romBigVersion != null) View.VISIBLE else View.GONE
                            text = romBigVersion
                        }
                        mainContentBinding.codebase.visibility = if (codebase != null) View.VISIBLE else View.GONE
                        codebaseInfo.apply {
                            visibility = if (codebase != null) View.VISIBLE else View.GONE
                            text = codebase
                        }
                        branch.visibility = if (romBranch != null) View.VISIBLE else View.GONE
                        branchInfo.apply {
                            visibility = if (romBranch != null) View.VISIBLE else View.GONE
                            text = romBranch
                        }
                        filename.visibility = if (romFileName != null) View.VISIBLE else View.GONE
                        filenameInfo.apply {
                            visibility = if (romFileName != null) View.VISIBLE else View.GONE
                            text = romFileName
                        }
                        filesize.visibility = if (romFileSize != null) View.VISIBLE else View.GONE
                        filesizeInfo.apply {
                            visibility = if (romFileSize != null) View.VISIBLE else View.GONE
                            text = romFileSize
                        }
                        download.visibility = if (romMd5 != null) View.VISIBLE else View.GONE
                        downloadInfo.apply {
                            visibility = if (romMd5 != null) View.VISIBLE else View.GONE
                            text = if (romMd5 == latestRomMd5) "https://ultimateota.d.miui.com/${romVersion}/${latestRomFileName}" else "https://bigota.d.miui.com/${romVersion}/${romFileName}"
                        }
                        downloadInfo.setOnClickListener {
                            val clip = ClipData.newPlainText("label", downloadInfo.text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(this@MainActivity, "链接已复制到剪贴板", Toast.LENGTH_SHORT).show()
                        }
                        changelog.visibility = if (romMd5 != null) View.VISIBLE else View.GONE
                        changelogInfo.apply {
                            visibility = if (romMd5 != null) View.VISIBLE else View.GONE
                            val log = StringBuilder()
                            romChangelog!!.forEach {
                                log.append(it.key).append("\n").append(it.value.txt.joinToString("\n")).append("\n")
                            }
                            text = log.toString()
                            setOnClickListener {
                                val clip = ClipData.newPlainText("label", changelogInfo.text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(this@MainActivity, "更新日志已复制到剪贴板", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityMainBinding = null
    }
}