package top.yukonga.update.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
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
import top.yukonga.update.logic.utils.AppUtils.deviceCodeList
import top.yukonga.update.logic.utils.AppUtils.dp
import top.yukonga.update.logic.utils.AppUtils.dropDownList
import top.yukonga.update.logic.utils.FileUtils
import top.yukonga.update.logic.utils.FileUtils.downloadFile
import top.yukonga.update.logic.utils.InfoUtils
import top.yukonga.update.logic.utils.JsonUtils.parseJSON
import top.yukonga.update.logic.utils.LoginUtils

class MainActivity : AppCompatActivity() {

    // Start ViewBinding.
    private lateinit var _activityMainBinding: ActivityMainBinding
    private val activityMainBinding get() = _activityMainBinding
    private val mainContentBinding: MainContentBinding get() = _activityMainBinding.mainContent

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Enable edge to edge.
        enableEdgeToEdge()

        // Inflate view.
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Setup default device information.
        mainContentBinding.apply {
            (codeName.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(deviceCodeList)
            (androidVersion.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(dropDownList)
        }

        // Setup TopAppBar.
        setSupportActionBar(_activityMainBinding.topAppBar)
    }

    override fun onResume() {
        super.onResume()
        mainContentBinding.apply {

            codeName.editText!!.setText(prefs.getString("codeName", ""))
            systemVersion.editText!!.setText(prefs.getString("systemVersion", ""))
            androidVersion.editText!!.setText(prefs.getString("androidVersion", ""))

            val cookiesFile = FileUtils.readFile(this@MainActivity, "cookies.json")
            if (cookiesFile.isNotEmpty()) {
                val cookies = Gson().fromJson(cookiesFile, Map::class.java)
                val description = if (cookies["description"] != null) cookies["description"].toString() else ""
                if (description == "成功") {
                    loginIcon.setImageResource(R.drawable.ic_check_circle)
                    loginTitle.text = getString(R.string.logged_in)
                    loginDesc.text = getString(R.string.using_v2)
                }
            }

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
                    bigVersionInfo, filenameInfo, filesizeInfo, changelogInfo
                )

                CoroutineScope(Dispatchers.Default).launch {

                    try {
                        // Acquire ROM info.
                        val romInfo = InfoUtils.getRomInfo(
                            this@MainActivity,
                            codeName.editText?.text.toString(),
                            systemVersion.editText?.text.toString(),
                            androidVersion.editText?.text.toString()
                        ).parseJSON<InfoHelper.RomInfo>()

                        prefs.edit().putString("codeName", codeName.editText?.text.toString())
                            .putString("systemVersion", systemVersion.editText?.text.toString())
                            .putString("androidVersion", androidVersion.editText?.text.toString()).apply()

                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        withContext(Dispatchers.Main) {

                            // Show a toast if we didn't get anything from request
                            if (romInfo.currentRom?.branch == null) {
                                Toast.makeText(this@MainActivity, getString(R.string.toast_no_info), Toast.LENGTH_SHORT).show()
                                throw NoSuchFieldException()
                            }

                            firstViewTitleArray.forEach {
                                if (!it.isVisible) it.fadInAnimation()
                            }

                            firstInfo.fadInAnimation()

                            // Setup TextViews
                            codenameInfo.setTextAnimation(romInfo.currentRom.device)

                            systemInfo.setTextAnimation(romInfo.currentRom.version)

                            codebaseInfo.setTextAnimation(romInfo.currentRom.codebase)

                            branchInfo.setTextAnimation(romInfo.currentRom.branch)


                            if (romInfo.currentRom.filename != null) {
                                secondViewTitleArray.forEach {
                                    if (!it.isVisible) it.fadInAnimation()
                                }
                                secondInfo.fadInAnimation()
                            } else {
                                secondViewTitleArray.forEach {
                                    if (it.isVisible) it.fadOutAnimation()
                                }
                                secondViewContentArray.forEach {
                                    if (it.isVisible) it.fadOutAnimation()
                                }
                                secondInfo.fadOutAnimation()
                            }

                            if (romInfo.currentRom.md5 != null) {
                                bigVersionInfo.setTextAnimation(
                                    romInfo.currentRom.bigversion?.replace("816", "HyperOS 1.0")
                                )

                                filenameInfo.setTextAnimation(romInfo.currentRom.filename)

                                filesizeInfo.setTextAnimation(romInfo.currentRom.filesize)

                                val officialLink = if (romInfo.currentRom.md5 == romInfo.latestRom?.md5) getString(
                                    R.string.official1_link, romInfo.currentRom.version, romInfo.latestRom.filename
                                ) else getString(R.string.official2_link, romInfo.currentRom.version, romInfo.currentRom.filename)

                                officialCopy.setOnClickListener {
                                    val clip = ClipData.newPlainText("label", officialLink)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(
                                        this@MainActivity, getString(R.string.toast_copied_to_pasteboard), Toast.LENGTH_SHORT
                                    ).show()
                                }

                                officialDownload.setOnClickListener {
                                    romInfo.currentRom.filename?.let { downloadFile(this@MainActivity, officialLink, it) }
                                }

                                val cdn1Link = if (romInfo.currentRom.md5 == romInfo.latestRom?.md5) getString(
                                    R.string.cdn1_link, romInfo.currentRom.version, romInfo.latestRom.filename
                                ) else getString(R.string.cdn2_link, romInfo.currentRom.version, romInfo.currentRom.filename)

                                cdn1Copy.setOnClickListener {
                                    val clip = ClipData.newPlainText("label", cdn1Link)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(
                                        this@MainActivity, getString(R.string.toast_copied_to_pasteboard), Toast.LENGTH_SHORT
                                    ).show()
                                }

                                cdn1Download.setOnClickListener {
                                    romInfo.currentRom.filename?.let { downloadFile(this@MainActivity, cdn1Link, it) }
                                }

                                val cdn2Link = if (romInfo.currentRom.md5 == romInfo.latestRom?.md5) getString(
                                    R.string.cdn2_link, romInfo.currentRom.version, romInfo.latestRom.filename
                                ) else getString(R.string.cdn2_link, romInfo.currentRom.version, romInfo.currentRom.filename)

                                cdn2Copy.setOnClickListener {
                                    val clip = ClipData.newPlainText("label", cdn2Link)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(
                                        this@MainActivity, getString(R.string.toast_copied_to_pasteboard), Toast.LENGTH_SHORT
                                    ).show()
                                }

                                cdn2Download.setOnClickListener {
                                    romInfo.currentRom.filename?.let { downloadFile(this@MainActivity, cdn2Link, it) }
                                }

                                val log = StringBuilder()
                                romInfo.currentRom.changelog!!.forEach {
                                    log.append(it.key).append("\n- ").append(it.value.txt.joinToString("\n- ")).append("\n\n")
                                }

                                changelogInfo.setTextAnimation(
                                    log.toString().trimEnd()
                                )

                                changelogInfo.setOnClickListener {
                                    val clip = ClipData.newPlainText("label", changelogInfo.text)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(
                                        this@MainActivity, getString(R.string.toast_copied_to_pasteboard), Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } // Main context
                    } catch (e: Exception) {
                        e.printStackTrace()

                        withContext(Dispatchers.Main) {
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
                            firstInfo.fadOutAnimation()
                            secondInfo.fadOutAnimation()
                        }

                    }

                } // CoroutineScope

            } // Fab operation

        } // Main content

    } // OnResume

    override fun onDestroy() {
        super.onDestroy()
        _activityMainBinding
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login -> {
                showDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDialog() {
        val view = LinearLayout(this@MainActivity).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
        }
        val inputAccountLayout = TextInputLayout(this@MainActivity).apply {
            hint = getString(R.string.account)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(180.dp, 50.dp, 180.dp, 0.dp)
            }
        }
        val inputAccount = TextInputEditText(this@MainActivity)
        inputAccountLayout.addView(inputAccount)
        val inputPasswordLayout = TextInputLayout(this@MainActivity).apply {
            endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            hint = getString(R.string.password)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(180.dp, 50.dp, 180.dp, 0.dp)
            }
        }
        val inputPassword = TextInputEditText(this@MainActivity).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        inputPasswordLayout.addView(inputPassword)
        view.addView(inputAccountLayout)
        view.addView(inputPasswordLayout)
        val builder = MaterialAlertDialogBuilder(this@MainActivity)
        builder.setTitle(getString(R.string.login)).setView(view).setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        builder.setPositiveButton(getString(R.string.login)) { _, _ ->
            val mInputAccount = inputAccount.text.toString()
            val mInputPassword = inputPassword.text.toString()
            CoroutineScope(Dispatchers.Default).launch {
                val isValid = LoginUtils().login(this@MainActivity, mInputAccount, mInputPassword)
                if (isValid) {
                    withContext(Dispatchers.Main) {
                        findViewById<ImageView>(R.id.login_icon).setImageResource(R.drawable.ic_check_circle)
                        findViewById<TextView>(R.id.login_title).text = getString(R.string.logged_in)
                        findViewById<TextView>(R.id.login_desc).text = getString(R.string.using_v2)
                    }
                }
            }
        }
        builder.show()
    }
}