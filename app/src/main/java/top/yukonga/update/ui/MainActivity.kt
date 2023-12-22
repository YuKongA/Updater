package top.yukonga.update.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.update.BuildConfig
import top.yukonga.update.R
import top.yukonga.update.databinding.ActivityMainBinding
import top.yukonga.update.databinding.MainContentBinding
import top.yukonga.update.logic.adapter.CustomAdapter
import top.yukonga.update.logic.data.DeviceInfoHelper
import top.yukonga.update.logic.data.RecoveryRomInfoHelper
import top.yukonga.update.logic.fadInAnimation
import top.yukonga.update.logic.fadOutAnimation
import top.yukonga.update.logic.setTextAnimation
import top.yukonga.update.logic.utils.AppUtils.androidDropDownList
import top.yukonga.update.logic.utils.AppUtils.dp
import top.yukonga.update.logic.utils.AppUtils.regionsDropDownList
import top.yukonga.update.logic.utils.FileUtils
import top.yukonga.update.logic.utils.FileUtils.downloadFile
import top.yukonga.update.logic.utils.InfoUtils
import top.yukonga.update.logic.utils.JsonUtils.parseJSON
import top.yukonga.update.logic.utils.LoginUtils
import top.yukonga.update.logic.utils.miuiStringToast.MiuiStringToast

class MainActivity : AppCompatActivity() {

    // Start ViewBinding.
    private lateinit var _activityMainBinding: ActivityMainBinding
    private val activityMainBinding get() = _activityMainBinding
    private val mainContentBinding: MainContentBinding get() = _activityMainBinding.mainContent

    private lateinit var prefs: SharedPreferences

    lateinit var codeNameWatcher: TextWatcher
    lateinit var deviceNameWatcher: TextWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Enable edge to edge.
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        // Setup Cutout mode.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val layoutParam = window.attributes
            layoutParam.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.setAttributes(layoutParam)
        }

        // Inflate view.
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Setup main information.
        mainContentBinding.apply {
            // Setup default device information.
            deviceName.editText!!.setText(prefs.getString("deviceName", ""))
            codeName.editText!!.setText(prefs.getString("codeName", ""))
            deviceRegions.editText!!.setText(prefs.getString("regions", ""))
            systemVersion.editText!!.setText(prefs.getString("systemVersion", ""))
            androidVersion.editText!!.setText(prefs.getString("androidVersion", ""))

            // Setup DropDownList.
            val deviceNamesAdapter = CustomAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, ArrayList(DeviceInfoHelper.deviceNames))
            val codeNamesAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, DeviceInfoHelper.codeNames)
            (deviceName.editText as? MaterialAutoCompleteTextView)?.setAdapter(deviceNamesAdapter)
            (codeName.editText as? MaterialAutoCompleteTextView)?.setAdapter(codeNamesAdapter)
            (deviceRegions.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(regionsDropDownList)
            (androidVersion.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(androidDropDownList)

            // Setup TextChangedListener.
            codeNameWatcher = object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    deviceName.editText!!.removeTextChangedListener(deviceNameWatcher)
                    val text = try {
                        DeviceInfoHelper.deviceName(s.toString())
                    } catch (ex: Exception) {
                        null
                    }
                    if (text != null) {
                        deviceName.editText!!.setText(text)
                        val deviceRegionsAdapter = ArrayAdapter(
                            this@MainActivity, android.R.layout.simple_dropdown_item_1line, DeviceInfoHelper.existRegions(DeviceInfoHelper.codeName(text)!!)
                        )
                        (deviceRegions.editText as? MaterialAutoCompleteTextView)?.setAdapter(deviceRegionsAdapter)
                    } else (deviceRegions.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(regionsDropDownList)
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable) {
                    deviceName.editText!!.addTextChangedListener(deviceNameWatcher)
                }
            }
            deviceNameWatcher = object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    codeName.editText!!.removeTextChangedListener(codeNameWatcher)
                    val text = try {
                        DeviceInfoHelper.codeName(s.toString())
                    } catch (ex: Exception) {
                        null
                    }
                    if (text != null) {
                        codeName.editText!!.setText(text)
                        val deviceRegionsAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, DeviceInfoHelper.existRegions(text))
                        (deviceRegions.editText as? MaterialAutoCompleteTextView)?.setAdapter(deviceRegionsAdapter)
                    } else (deviceRegions.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(regionsDropDownList)
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable) {
                    codeName.editText!!.addTextChangedListener(codeNameWatcher)
                }
            }
            codeName.editText!!.addTextChangedListener(codeNameWatcher)
            deviceName.editText!!.addTextChangedListener(deviceNameWatcher)
        }

        // Setup TopAppBar.
        activityMainBinding.topAppBar.apply {
            setNavigationOnClickListener {
                showAboutDialog()
            }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.login -> showLoginDialog()
                    R.id.logout -> showLogoutDialog()
                }
                false
            }
        }

        // Check if logged in.
        val cookiesFile = FileUtils.readFile(this@MainActivity, "cookies.json")
        if (cookiesFile.isNotEmpty()) {
            val cookies = Gson().fromJson(cookiesFile, Map::class.java)
            val description = if (cookies["description"] != null) cookies["description"].toString() else ""
            if (description == "成功") {
                mainContentBinding.apply {
                    loginIcon.setImageResource(R.drawable.ic_check_circle)
                    loginTitle.text = getString(R.string.logged_in)
                    loginDesc.text = getString(R.string.using_v2)
                }
                activityMainBinding.apply {
                    topAppBar.menu.findItem(R.id.login).isVisible = false
                    topAppBar.menu.findItem(R.id.logout).isVisible = true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        mainContentBinding.apply {

            // Hide input method when focus is on dropdown.
            deviceRegionsDropdown.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            androidVersionDropdown.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }

            // Setup Fab OnClickListener.
            activityMainBinding.implement.setOnClickListener {

                val firstViewTitleArray = arrayOf(
                    codename, system, codebase, branch, firstInfo
                )

                val secondViewTitleArray = arrayOf(
                    bigVersion, filename, filesize, filemd5, download, changelog, secondInfo
                )

                val firstViewContentArray = arrayOf(
                    codenameInfo, systemInfo, codebaseInfo, branchInfo
                )

                val secondViewContentArray = arrayOf(
                    bigVersionInfo, filenameInfo, filesizeInfo, filemd5Info, changelogInfo
                )

                CoroutineScope(Dispatchers.Default).launch {

                    try {

                        val regionsText = deviceRegions.editText?.text.toString()
                        val codeNameText = codeName.editText?.text.toString()
                        val deviceNameText = deviceName.editText?.text.toString()

                        val codeNameTextExtR = codeNameText + DeviceInfoHelper.regions(codeNameText, regionsText)
                        val androidVersionText = androidVersion.editText?.text.toString()
                        val systemVersionText = systemVersion.editText?.text.toString()
                        val systemVersionTextExt = systemVersionText
                            .replace("OS1", "V816")
                            .replace("AUTO", DeviceInfoHelper.deviceCode(androidVersionText, codeNameText, regionsText))

                        // Acquire ROM info.
                        val recoveryRomInfo = InfoUtils.getRecoveryRomInfo(
                            this@MainActivity,
                            codeNameTextExtR,
                            systemVersionTextExt,
                            androidVersionText
                        ).parseJSON<RecoveryRomInfoHelper.RomInfo>()

                        // val fastbootRomInfo = InfoUtils.getFastbootRomInfo(codeNameTextExtR).parseJSON<FastbootRomInfoHelper.RomInfo>()

                        prefs.edit()
                            .putString("deviceName", deviceNameText)
                            .putString("codeName", codeNameText)
                            .putString("regions", regionsText)
                            .putString("systemVersion", systemVersionText)
                            .putString("androidVersion", androidVersionText)
                            .apply()

                        withContext(Dispatchers.Main) {

                            // Show a toast if we didn't get anything from request
                            if (recoveryRomInfo.currentRom?.branch == null) {
                                activityMainBinding.implement.extend()
                                MiuiStringToast.showStringToast(this@MainActivity, getString(R.string.toast_no_info), 0)
                                throw NoSuchFieldException()
                            } else {
                                activityMainBinding.implement.shrink()
                            }

                            // Show a toast if we detect that the login has expired
                            val cookiesFile = FileUtils.readFile(this@MainActivity, "cookies.json")
                            if (cookiesFile.isNotEmpty()) {
                                val cookies = Gson().fromJson(cookiesFile, Map::class.java)
                                val description = if (cookies["description"] != null) cookies["description"].toString() else ""
                                if (description == "成功" && recoveryRomInfo.authResult != 1) {
                                    MiuiStringToast.showStringToast(this@MainActivity, getString(R.string.login_expired), 0)
                                }
                            }

                            firstViewTitleArray.forEach {
                                if (!it.isVisible) it.fadInAnimation()
                            }

                            // Setup TextViews
                            codenameInfo.setTextAnimation(recoveryRomInfo.currentRom.device)
                            systemInfo.setTextAnimation(recoveryRomInfo.currentRom.version)
                            codebaseInfo.setTextAnimation(recoveryRomInfo.currentRom.codebase)
                            branchInfo.setTextAnimation(recoveryRomInfo.currentRom.branch)

                            if (recoveryRomInfo.currentRom.filename != null) {
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

                            if (recoveryRomInfo.currentRom.md5 != null) {
                                bigVersionInfo.setTextAnimation(
                                    if (recoveryRomInfo.currentRom.bigversion?.contains("816") == true) {
                                        recoveryRomInfo.currentRom.bigversion.replace("816", "HyperOS 1.0")
                                    } else {
                                        "MIUI ${recoveryRomInfo.currentRom.bigversion}"
                                    }
                                )

                                filenameInfo.setTextAnimation(recoveryRomInfo.currentRom.filename)
                                filesizeInfo.setTextAnimation(recoveryRomInfo.currentRom.filesize)
                                filemd5Info.setTextAnimation(recoveryRomInfo.currentRom.md5)

                                val officialLink = if (recoveryRomInfo.currentRom.md5 == recoveryRomInfo.latestRom?.md5) getString(
                                    R.string.official1_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.latestRom.filename
                                ) else getString(R.string.official2_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.currentRom.filename)
                                val cdn1Link = if (recoveryRomInfo.currentRom.md5 == recoveryRomInfo.latestRom?.md5) getString(
                                    R.string.cdn1_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.latestRom.filename
                                ) else getString(R.string.cdn2_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.currentRom.filename)
                                val cdn2Link = if (recoveryRomInfo.currentRom.md5 == recoveryRomInfo.latestRom?.md5) getString(
                                    R.string.cdn2_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.latestRom.filename
                                ) else getString(R.string.cdn2_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.currentRom.filename)

                                officialDownload.setDownloadClickListener(recoveryRomInfo.currentRom.filename, officialLink)
                                cdn1Download.setDownloadClickListener(recoveryRomInfo.currentRom.filename, cdn1Link)
                                cdn2Download.setDownloadClickListener(recoveryRomInfo.currentRom.filename, cdn2Link)
                                officialCopy.setCopyClickListener(officialLink)
                                cdn1Copy.setCopyClickListener(cdn1Link)
                                cdn2Copy.setCopyClickListener(cdn2Link)

                                val log = StringBuilder()
                                recoveryRomInfo.currentRom.changelog!!.forEach {
                                    log.append(it.key).append("\n- ").append(it.value.txt.joinToString("\n- ")).append("\n\n")
                                }

                                changelogInfo.setTextAnimation(log.toString().trimEnd())
                                changelogInfo.setCopyClickListener(log.toString().trimEnd())
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

    private fun showLoginDialog() {
        val view = LinearLayout(this@MainActivity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
        }
        val inputAccountLayout = createTextInputLayout(getString(R.string.account))
        val inputAccount = createTextInputEditText()
        inputAccountLayout.addView(inputAccount)
        val inputPasswordLayout = createTextInputLayout(getString(R.string.password), TextInputLayout.END_ICON_PASSWORD_TOGGLE)
        val inputPassword = createTextInputEditText(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
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
                        mainContentBinding.apply {
                            loginIcon.setImageResource(R.drawable.ic_check_circle)
                            loginTitle.text = getString(R.string.logged_in)
                            loginDesc.text = getString(R.string.using_v2)
                        }
                        activityMainBinding.apply {
                            topAppBar.menu.findItem(R.id.login).isVisible = false
                            topAppBar.menu.findItem(R.id.logout).isVisible = true
                        }
                    }
                }
            }
        }.show()
    }

    private fun showLogoutDialog() {
        val builder = MaterialAlertDialogBuilder(this@MainActivity)
        builder.setTitle(getString(R.string.login)).setTitle(getString(R.string.logout)).setMessage(getString(R.string.logout_desc))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        builder.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            CoroutineScope(Dispatchers.Default).launch {
                LoginUtils().logout(this@MainActivity)
                withContext(Dispatchers.Main) {
                    mainContentBinding.apply {
                        loginIcon.setImageResource(R.drawable.ic_cancel)
                        loginTitle.text = getString(R.string.no_account)
                        loginDesc.text = getString(R.string.login_desc)
                    }
                    activityMainBinding.apply {
                        topAppBar.menu.findItem(R.id.login).isVisible = true
                        topAppBar.menu.findItem(R.id.logout).isVisible = false
                    }
                }
            }
        }.show()
    }

    private fun showAboutDialog() {
        val view = LinearLayout(this@MainActivity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
        }
        val appSummary = createTextView(getString(R.string.app_summary), 14f, 25.dp, 10.dp, 25.dp, 20.dp)
        val appVersion =
            createTextView(getString(R.string.app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString()), 14f, 25.dp, 0.dp, 25.dp, 0.dp)
        val appBuild = createTextView(BuildConfig.BUILD_TYPE, 14f, 25.dp, 0.dp, 25.dp, 20.dp)
        val appGithub = createTextView(Html.fromHtml(getString(R.string.app_github), Html.FROM_HTML_MODE_COMPACT), 12f, 25.dp, 0.dp, 25.dp, 25.dp).apply {
            movementMethod = LinkMovementMethod.getInstance()
        }
        view.apply {
            addView(appSummary)
            addView(appVersion)
            addView(appBuild)
            addView(appGithub)
        }
        val builder = MaterialAlertDialogBuilder(this@MainActivity)
        builder.setTitle(getString(R.string.app_name)).setIcon(R.drawable.ic_launcher).setView(view).show()
    }

    private fun createTextInputLayout(hint: String, endIconMode: Int = TextInputLayout.END_ICON_NONE): TextInputLayout {
        return TextInputLayout(this@MainActivity).apply {
            this.hint = hint
            this.endIconMode = endIconMode
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(25.dp, 8.dp, 25.dp, 0.dp)
            }
        }
    }

    private fun createTextInputEditText(inputType: Int = InputType.TYPE_CLASS_TEXT): TextInputEditText {
        return TextInputEditText(this@MainActivity).apply {
            this.inputType = inputType
        }
    }

    private fun createTextView(text: CharSequence, textSize: Float, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): TextView {
        return TextView(this@MainActivity).apply {
            this.text = text
            this.textSize = textSize
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
            }
        }
    }

    private fun MaterialButton.setDownloadClickListener(filename: String?, link: String) {
        setOnClickListener {
            filename?.let { downloadFile(this@MainActivity, link, it) }
        }
    }

    private fun MaterialButton.setCopyClickListener(link: CharSequence?) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        setOnClickListener {
            val clip = ClipData.newPlainText("label", link)
            clipboard.setPrimaryClip(clip)
            MiuiStringToast.showStringToast(this@MainActivity, getString(R.string.toast_copied_to_pasteboard), 1)
        }
    }

    private fun MaterialTextView.setCopyClickListener(text: CharSequence?) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        setOnClickListener {
            val clip = ClipData.newPlainText("label", text)
            clipboard.setPrimaryClip(clip)
            MiuiStringToast.showStringToast(this@MainActivity, getString(R.string.toast_copied_to_pasteboard), 1)
        }
    }

}