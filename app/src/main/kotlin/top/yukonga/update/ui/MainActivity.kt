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
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
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
import top.yukonga.update.logic.adapter.CustomArrayAdapter
import top.yukonga.update.logic.data.DeviceInfoHelper
import top.yukonga.update.logic.data.RecoveryRomInfoHelper
import top.yukonga.update.logic.fadInAnimation
import top.yukonga.update.logic.fadOutAnimation
import top.yukonga.update.logic.setTextAnimation
import top.yukonga.update.logic.utils.AppUtils.dp
import top.yukonga.update.logic.utils.AppUtils.hapticConfirm
import top.yukonga.update.logic.utils.AppUtils.hapticReject
import top.yukonga.update.logic.utils.FileUtils
import top.yukonga.update.logic.utils.FileUtils.downloadRomFile
import top.yukonga.update.logic.utils.InfoUtils
import top.yukonga.update.logic.utils.JsonUtils.parseJSON
import top.yukonga.update.logic.utils.LoginUtils
import top.yukonga.update.logic.utils.miuiStringToast.MiuiStringToast
import top.yukonga.update.logic.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {

    // Start ViewBinding.
    private lateinit var _activityMainBinding: ActivityMainBinding
    private val activityMainBinding get() = _activityMainBinding
    private val mainContentBinding: MainContentBinding get() = activityMainBinding.mainContent

    private lateinit var prefs: SharedPreferences

    private lateinit var codeNameWatcher: TextWatcher
    private lateinit var deviceNameWatcher: TextWatcher

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get SharedPreferences.
        prefs = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)

        // Get ViewModel.
        mainViewModel = ViewModelProvider(this@MainActivity)[MainViewModel::class.java]

        // Enable edge to edge.
        setupEdgeToEdge()

        // Setup Cutout mode.
        setupCutoutMode()

        // Inflate view.
        inflateView()

        // Setup main information.
        setupMainInformation()

        // Setup TopAppBar.
        setupTopAppBar()

        // Check if logged in.
        checkIfLoggedIn()
    }

    override fun onResume() {
        super.onResume()

        mainContentBinding.apply {

            // Setup Fab OnClickListener.
            activityMainBinding.implement.setOnClickListener {

                hapticConfirm(activityMainBinding.implement)

                CoroutineScope(Dispatchers.Default).launch {

                    try {
                        val deviceRegion = deviceRegion.editText?.text.toString()
                        val codeName = codeName.editText?.text.toString()
                        val deviceName = deviceName.editText?.text.toString()

                        val regionCode = DeviceInfoHelper.regionCode(deviceRegion)
                        val regionNameExt = DeviceInfoHelper.regionNameExt(deviceRegion)
                        val codeNameExt = codeName + regionNameExt

                        val androidVersion = androidVersion.editText?.text.toString()
                        val systemVersion = systemVersion.editText?.text.toString()

                        val deviceCode = DeviceInfoHelper.deviceCode(androidVersion, codeName, regionCode)
                        val systemVersionTextExt = systemVersion.replace("OS1", "V816").replace("AUTO", deviceCode)

                        // Acquire ROM info.
                        val recoveryRomInfo = InfoUtils.getRecoveryRomInfo(
                            this@MainActivity, codeNameExt, regionCode, systemVersionTextExt, androidVersion
                        ).parseJSON<RecoveryRomInfoHelper.RomInfo>()

                        prefs.edit().putString("deviceName", deviceName).putString("codeName", codeName).putString("deviceRegion", deviceRegion)
                            .putString("systemVersion", systemVersion).putString("androidVersion", androidVersion).apply()

                        withContext(Dispatchers.Main) {

                            // Hide all cardViews & Show a toast if we didn't get anything from request.
                            if (recoveryRomInfo.currentRom?.branch == null) {
                                mainViewModel.apply {
                                    device = null
                                    filename = null
                                }
                                setupCardViews()
                                MiuiStringToast.showStringToast(this@MainActivity, getString(R.string.toast_no_info), 0)
                                throw NoSuchFieldException()
                            }

                            // Show a toast if we detect that the login has expired.
                            if (FileUtils.isCookiesFileExists(this@MainActivity)) {
                                val cookiesFile = FileUtils.readCookiesFile(this@MainActivity)
                                val cookies = Gson().fromJson(cookiesFile, MutableMap::class.java) as MutableMap<String, String>
                                val description = cookies["description"].toString()
                                val authResult = cookies["authResult"].toString()
                                if (description.isNotEmpty() && recoveryRomInfo.authResult != 1 && authResult != "-1") {
                                    cookies["authResult"] = "-1"
                                    FileUtils.saveCookiesFile(this@MainActivity, Gson().toJson(cookies))
                                    MiuiStringToast.showStringToast(this@MainActivity, getString(R.string.login_expired_dialog), 0)
                                }
                            }

                            mainViewModel.apply {
                                device = recoveryRomInfo.currentRom.device
                                version = recoveryRomInfo.currentRom.version
                                codebase = recoveryRomInfo.currentRom.codebase
                                branch = recoveryRomInfo.currentRom.branch
                            }

                            if (recoveryRomInfo.currentRom.md5 != null) {
                                val log = StringBuilder()
                                recoveryRomInfo.currentRom.changelog!!.forEach {
                                    log.append(it.key).append("\n- ").append(it.value.txt.joinToString("\n- ")).append("\n\n")
                                }

                                mainViewModel.apply {
                                    filename = recoveryRomInfo.currentRom.filename
                                    filesize = recoveryRomInfo.currentRom.filesize
                                    bigversion = if (recoveryRomInfo.currentRom.bigversion?.contains("816") == true) {
                                        recoveryRomInfo.currentRom.bigversion.replace("816", "HyperOS 1.0")
                                    } else {
                                        "MIUI ${recoveryRomInfo.currentRom.bigversion}"
                                    }
                                    officialDownload = if (recoveryRomInfo.currentRom.md5 == recoveryRomInfo.latestRom?.md5) getString(
                                        R.string.official1_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.latestRom.filename
                                    ) else getString(R.string.official2_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.currentRom.filename)
                                    officialText = if (recoveryRomInfo.currentRom.md5 == recoveryRomInfo.latestRom?.md5) getString(R.string.official, "ultimateota")
                                    else getString(R.string.official, "bigota")
                                    cdnDownload = if (recoveryRomInfo.currentRom.md5 == recoveryRomInfo.latestRom?.md5) getString(
                                        R.string.cdn_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.latestRom.filename
                                    ) else getString(R.string.cdn_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.currentRom.filename)
                                    changelog = log.toString().trimEnd()
                                }
                            } else {
                                mainViewModel.filename = null
                            }
                            setupCardViews()

                        } // Main context
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } // CoroutineScope

            } // Fab operation

        } // Main content

        // Hide all card views if we didn't get anything from request.
        setupCardViews()

    } // OnResume

    override fun onDestroy() {
        super.onDestroy()
        activityMainBinding
    }

    private fun showLoginDialog() {
        val view = createDialogView()
        val switch = createSwitchForLogin()
        val inputAccountLayout = createTextInputLayout(getString(R.string.account))
        val inputAccount = createTextInputEditText()
        inputAccountLayout.addView(inputAccount)
        val inputPasswordLayout = createTextInputLayout(getString(R.string.password), TextInputLayout.END_ICON_PASSWORD_TOGGLE)
        val inputPassword = createTextInputEditText(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        inputPasswordLayout.addView(inputPassword)
        view.apply {
            addView(switch)
            addView(inputAccountLayout)
            addView(inputPasswordLayout)
        }
        MaterialAlertDialogBuilder(this@MainActivity).setTitle(getString(R.string.login)).setView(view).setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            hapticReject(activityMainBinding.topAppBar)
            dialog.dismiss()
        }.setPositiveButton(getString(R.string.login)) { _, _ ->
            hapticConfirm(activityMainBinding.topAppBar)
            val global = prefs.getString("global", "") ?: "0"
            val mInputAccount = inputAccount.text.toString()
            val mInputPassword = inputPassword.text.toString()
            CoroutineScope(Dispatchers.Default).launch {
                val isValid = LoginUtils().login(this@MainActivity, mInputAccount, mInputPassword, global)
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
        MaterialAlertDialogBuilder(this@MainActivity).setTitle(getString(R.string.login)).setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_desc)).setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                hapticReject(activityMainBinding.topAppBar)
                dialog.dismiss()
            }.setPositiveButton(getString(R.string.confirm)) { _, _ ->
                hapticConfirm(activityMainBinding.topAppBar)
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
        val view = createDialogView()
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
        MaterialAlertDialogBuilder(this@MainActivity).setTitle(getString(R.string.app_name)).setIcon(R.drawable.ic_launcher).setView(view).show()
    }

    private fun setupEdgeToEdge() {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
    }

    private fun setupCutoutMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val layoutParam = window.attributes
            layoutParam.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.setAttributes(layoutParam)
        }
    }

    private fun inflateView() {
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
    }

    private fun setupMainInformation() {
        mainContentBinding.apply {

            // Hide input method when focus is on dropdown.
            deviceRegionDropdown.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) hideSoftInput(view)
            }
            androidVersionDropdown.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) hideSoftInput(view)
            }

            // Setup default device information.
            deviceName.editText!!.setText(prefs.getString("deviceName", ""))
            codeName.editText!!.setText(prefs.getString("codeName", ""))
            deviceRegion.editText!!.setText(prefs.getString("deviceRegion", ""))
            systemVersion.editText!!.setText(prefs.getString("systemVersion", ""))
            androidVersion.editText!!.setText(prefs.getString("androidVersion", ""))

            // Setup DropDownList.
            val deviceNamesAdapter = CustomArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, DeviceInfoHelper.deviceNames)
            val codeNamesAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, DeviceInfoHelper.codeNames)
            val deviceRegionAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, DeviceInfoHelper.regionNames)
            val androidVersionAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, DeviceInfoHelper.androidVersions)
            (deviceName.editText as? MaterialAutoCompleteTextView)?.setAdapter(deviceNamesAdapter)
            (codeName.editText as? MaterialAutoCompleteTextView)?.setAdapter(codeNamesAdapter)
            (deviceRegion.editText as? MaterialAutoCompleteTextView)?.setAdapter(deviceRegionAdapter)
            (androidVersion.editText as? MaterialAutoCompleteTextView)?.setAdapter(androidVersionAdapter)

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
                    }
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
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable) {
                    codeName.editText!!.addTextChangedListener(codeNameWatcher)
                }
            }
            codeName.editText!!.addTextChangedListener(codeNameWatcher)
            deviceName.editText!!.addTextChangedListener(deviceNameWatcher)
        }
    }

    private fun setupCardViews() {
        mainContentBinding.apply {
            val firstViewArray = arrayOf(firstInfo, secondInfo)
            val secondViewArray = arrayOf(secondInfo, downloadInfo, bigVersion, bigVersionInfo)

            CoroutineScope(Dispatchers.Default).launch {
                withContext(Dispatchers.Main) {
                    if (mainViewModel.device != null) {
                        firstViewArray.forEach {
                            if (!it.isVisible) it.fadInAnimation()
                        }
                        activityMainBinding.implement.shrink()
                        codenameInfo.setTextAnimation(mainViewModel.device)
                        systemInfo.setTextAnimation(mainViewModel.version)
                        codebaseInfo.setTextAnimation(mainViewModel.codebase)
                        branchInfo.setTextAnimation(mainViewModel.branch)
                    } else {
                        activityMainBinding.implement.extend()
                        firstViewArray.forEach {
                            if (it.isVisible) it.fadOutAnimation()
                        }
                    }
                    if (mainViewModel.filename != null) {
                        secondViewArray.forEach {
                            if (!it.isVisible) it.fadInAnimation()
                        }
                        bigVersionInfo.setTextAnimation(mainViewModel.bigversion)
                        filenameInfo.setTextAnimation(mainViewModel.filename)
                        filesizeInfo.setTextAnimation(mainViewModel.filesize)
                        changelogInfo.setTextAnimation(mainViewModel.changelog)
                        changelogInfo.setCopyClickListener(mainViewModel.changelog)
                        officialDownload.setDownloadClickListener(mainViewModel.filename, mainViewModel.officialDownload!!)
                        official.text = mainViewModel.officialText
                        officialCopy.setCopyClickListener(mainViewModel.officialDownload)
                        cdnDownload.setDownloadClickListener(mainViewModel.filename, mainViewModel.cdnDownload!!)
                        cdnCopy.setCopyClickListener(mainViewModel.cdnDownload)
                    } else {
                        secondViewArray.forEach {
                            if (it.isVisible) it.fadOutAnimation()
                        }
                    }
                }
            }
        }
    }

    private fun setupTopAppBar() {
        activityMainBinding.topAppBar.apply {
            setNavigationOnClickListener {
                hapticConfirm(this)
                showAboutDialog()
            }
            setOnMenuItemClickListener { menuItem ->
                hapticConfirm(this)
                when (menuItem.itemId) {
                    R.id.login -> showLoginDialog()
                    R.id.logout -> showLogoutDialog()
                }
                false
            }
        }
    }

    private fun checkIfLoggedIn() {
        if (FileUtils.isCookiesFileExists(this@MainActivity)) {
            val cookiesFile = FileUtils.readCookiesFile(this@MainActivity)
            val cookies = Gson().fromJson(cookiesFile, MutableMap::class.java)
            val description = cookies["description"].toString()
            val authResult = cookies["authResult"].toString()
            if (description.isNotEmpty() && authResult == "-1") {
                mainContentBinding.apply {
                    loginIcon.setImageResource(R.drawable.ic_error)
                    loginTitle.text = getString(R.string.login_expired)
                    loginDesc.text = getString(R.string.login_expired_desc)
                }
            } else if (description.isNotEmpty()) {
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

    private fun createDialogView(): LinearLayout {
        return LinearLayout(this@MainActivity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
        }
    }

    private fun createSwitchForLogin(): MaterialSwitch {
        return MaterialSwitch(this@MainActivity).apply {
            text = getString(R.string.global)
            isChecked = prefs.getString("global", "") == "1"
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(28.dp, 8.dp, 28.dp, 0.dp)
            }
            setOnCheckedChangeListener { _, isChecked ->
                prefs.edit().putString("global", if (isChecked) "1" else "0").apply()
            }
        }
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
            filename?.let {
                hapticConfirm(this)
                downloadRomFile(this@MainActivity, link, it)
            }
        }
    }

    private fun MaterialButton.setCopyClickListener(link: CharSequence?) {
        setOnClickListener {
            hapticConfirm(this)
            copyText(link)
            MiuiStringToast.showStringToast(this@MainActivity, getString(R.string.toast_copied_to_pasteboard), 1)
        }
    }

    private fun MaterialTextView.setCopyClickListener(text: CharSequence?) {
        setOnClickListener {
            hapticConfirm(this)
            copyText(text)
            MiuiStringToast.showStringToast(this@MainActivity, getString(R.string.toast_copied_to_pasteboard), 1)
        }
    }

    private fun copyText(text: CharSequence?) {
        val cm: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(packageName, text))
    }

    private fun hideSoftInput(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
