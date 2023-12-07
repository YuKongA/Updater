package top.yukonga.update.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.update.R
import top.yukonga.update.databinding.ActivityMainBinding
import top.yukonga.update.databinding.MainContentBinding
import top.yukonga.update.logic.data.InfoHelper
import top.yukonga.update.logic.fadInAnimation
import top.yukonga.update.logic.fadOutAnimation
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
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.dropdown_list_item, dropDownList)

        // Setup default device information.
        mainContentBinding.apply {
            codeName.editText?.setText(getString(R.string.default_device))
            systemVersion.editText?.setText(getString(R.string.default_system_version))
            androidVersion.editText?.setText(getString(R.string.default_android_version))
            (androidVersion.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }
    }

    override fun onResume() {
        super.onResume()
        mainContentBinding.apply {
            activityMainBinding.implement.setOnClickListener {

                val firstViewTitleArray = arrayOf(
                    codename, system, codebase, branch
                )

                val secondViewTitleArray = arrayOf(
                    bigVersion, filename, filesize, download, changelog
                )

                val firstViewContentArray = arrayOf(
                    codenameInfo, systemInfo, codebaseInfo, branchInfo
                )
                
                val secondViewContentArray = arrayOf(
                    bigVersionInfo, filenameInfo, filesizeInfo, downloadInfo, changelogInfo
                )

                CoroutineScope(Dispatchers.Default).launch {

                    try {
                        // Acquire ROM info.
                        val romInfo = Utils.getRomInfo(
                            codeName.editText?.text.toString(),
                            systemVersion.editText?.text.toString(),
                            androidVersion.editText?.text.toString()
                        ).parseJSON<InfoHelper.RomInfo>()

                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        withContext(Dispatchers.Main) {

                            // Show a toast if we didn't get anything from request
                            if (romInfo.currentRom?.branch == null) {
                                Toast.makeText(this@MainActivity, getString(R.string.toast_no_info), Toast.LENGTH_SHORT)
                                    .show()
                                throw NoSuchFieldException()
                            }

                            firstViewTitleArray.forEach {
                                if (!it.isVisible) it.fadInAnimation()
                            }

                            // Setup TextViews
                            codenameInfo.setTextAnimation(romInfo.currentRom.device)

                            systemInfo.setTextAnimation(romInfo.currentRom.version)

                            codebaseInfo.setTextAnimation(romInfo.currentRom.codebase)

                            branchInfo.setTextAnimation(romInfo.currentRom.branch)


                            if (romInfo.currentRom.filename != null) {
                                secondViewTitleArray.forEach {
                                    if (!it.isVisible) it.fadInAnimation()
                                }
                            } else {
                                secondViewTitleArray.forEach {
                                    if (it.isVisible) it.fadOutAnimation()
                                }
                                secondViewContentArray.forEach {
                                    if (it.isVisible) it.fadOutAnimation()
                                }
                            }

                            if (romInfo.currentRom.md5 != null) {
                                bigVersionInfo.setTextAnimation(
                                    romInfo.currentRom.bigversion?.replace("816", "HyperOS 1.0")
                                )

                                filenameInfo.setTextAnimation(romInfo.currentRom.filename)

                                filesizeInfo.setTextAnimation(romInfo.currentRom.filesize)

                                downloadInfo.setTextAnimation(
                                    if (romInfo.currentRom.md5 == romInfo.latestRom?.md5) getString(
                                        R.string.https_ultimateota_d_miui_com,
                                        romInfo.currentRom.version,
                                        romInfo.latestRom.filename
                                    )
                                    else getString(
                                        R.string.https_bigota_d_miui_com,
                                        romInfo.currentRom.version,
                                        romInfo.currentRom.filename
                                    )
                                )

                                val log = StringBuilder()
                                romInfo.currentRom.changelog!!.forEach {
                                    log.append(it.key).append("\n").append(it.value.txt.joinToString("\n")).append("\n")
                                }

                                changelogInfo.setTextAnimation(
                                    log.toString()
                                )

                                changelogInfo.setOnClickListener {
                                    val clip = ClipData.newPlainText("label", changelogInfo.text)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(
                                        this@MainActivity,
                                        getString(R.string.toast_copied_to_pasteboard),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                downloadInfo.setOnClickListener {
                                    val clip = ClipData.newPlainText(
                                        "label", downloadInfo.text
                                    )
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(
                                        this@MainActivity,
                                        getString(R.string.toast_copied_to_pasteboard),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } // Main context
                    } catch (e: Exception) {
                        e.printStackTrace()

                        firstViewTitleArray.forEach {
                            if (it.isVisible) it.fadOutAnimation()
                        }

                        secondViewTitleArray.forEach {
                            if (it.isVisible) it.fadOutAnimation()
                        }

                        firstViewContentArray.forEach {
                            if (it.isVisible) it.fadOutAnimation()
                        }

                        secondViewContentArray.forEach {
                            if (it.isVisible) it.fadOutAnimation()
                        }

                    }

                } // CoroutineScope

            } // Fab operation

        } // Main content

    } // OnResume

    override fun onDestroy() {
        super.onDestroy()
        _activityMainBinding = null
    }
}