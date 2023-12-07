package top.yukonga.update.ui

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
import top.yukonga.update.databinding.ActivityMainBinding
import top.yukonga.update.databinding.MainContentBinding
import top.yukonga.update.logic.data.InfoHelper
import top.yukonga.update.logic.setTextAnimation
import top.yukonga.update.logic.utils.JsonUtils.parseJSON
import top.yukonga.update.logic.utils.Utils

class MainActivity : AppCompatActivity() {

    // Start ViewBinding.
    private var _activityMainBinding: ActivityMainBinding? = null
    private val activityMainBinding get() = _activityMainBinding!!
    private val mainContentBinding: MainContentBinding get() = _activityMainBinding!!.mainContent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge to edge.
        enableEdgeToEdge()

        // Inflate view.
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Initialize Dropdown List.
        val dropDownList = arrayOf("12", "13", "14")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
            R.layout.dropdown_list_item, dropDownList)

        // Setup default device information.
        mainContentBinding.apply {
            codeName.editText?.setText(
                getString(R.string.default_device)
            )
            systemVersion.editText?.setText(
                getString(R.string.default_system_version)
            )
            androidVersion.editText?.setText(
                getString(R.string.default_android_version)
            )
            (androidVersion.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }
    }

    override fun onResume() {
        super.onResume()
        mainContentBinding.apply {
            activityMainBinding.implement.setOnClickListener {
                CoroutineScope(Dispatchers.Default).launch {

                    // Acquire ROM info.
                    val romInfo = Utils.getRomInfo(
                        codeName.editText?.text.toString(),
                        systemVersion.editText?.text.toString(),
                        androidVersion.editText?.text.toString()
                    ).parseJSON<InfoHelper.RomInfo>()

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

                        // Show a toast if we didn't get anything from request
                        if (romBranch == null) {
                            Toast.makeText(
                                this@MainActivity,
                                "未获取到任何信息",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        codenameInfo.setTextAnimation(romDevice)

                        systemInfo.setTextAnimation(romVersion)

                        bigVersionInfo.setTextAnimation(romBigVersion)

                        codebaseInfo.setTextAnimation(codebase)

                        branchInfo.setTextAnimation(romBranch)

                        filenameInfo.setTextAnimation(romFileName)

                        filesizeInfo.setTextAnimation(romFileSize)

                        downloadInfo.setTextAnimation(
                            if (romMd5 == latestRomMd5)
                                "https://ultimateota.d.miui.com/${romVersion}/${latestRomFileName}"
                            else
                                "https://bigota.d.miui.com/${romVersion}/${romFileName}"
                        )

                        downloadInfo.setOnClickListener {
                            val clip = ClipData.newPlainText("label", downloadInfo.text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(this@MainActivity, "链接已复制到剪贴板", Toast.LENGTH_SHORT).show()
                        }

                        val log = StringBuilder()
                        romChangelog!!.forEach {
                            log.append(it.key).append("\n").append(it.value.txt.joinToString("\n")).append("\n")
                        }

                        changelogInfo.setTextAnimation(
                            log.toString()
                        )

                        changelogInfo.apply {
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