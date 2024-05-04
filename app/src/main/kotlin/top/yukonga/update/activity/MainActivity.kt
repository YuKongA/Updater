package top.yukonga.update.activity

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
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.yukonga.miuiStringToast.MiuiStringToast.showStringToast
import top.yukonga.update.BuildConfig
import top.yukonga.update.R
import top.yukonga.update.activity.adapter.CustomArrayAdapter
import top.yukonga.update.activity.viewModel.MainViewModel
import top.yukonga.update.databinding.ActivityMainBinding
import top.yukonga.update.databinding.MainContentBinding
import top.yukonga.update.logic.data.DeviceInfoHelper
import top.yukonga.update.logic.data.RomInfoHelper
import top.yukonga.update.logic.utils.AnimUtils.fadInAnimation
import top.yukonga.update.logic.utils.AnimUtils.fadOutAnimation
import top.yukonga.update.logic.utils.AnimUtils.setTextAnimation
import top.yukonga.update.logic.utils.AppUtils
import top.yukonga.update.logic.utils.AppUtils.addInsetsByMargin
import top.yukonga.update.logic.utils.AppUtils.addInsetsByPadding
import top.yukonga.update.logic.utils.AppUtils.dp
import top.yukonga.update.logic.utils.AppUtils.hideKeyBoard
import top.yukonga.update.logic.utils.AppUtils.json
import top.yukonga.update.logic.utils.AppUtils.setCopyClickListener
import top.yukonga.update.logic.utils.AppUtils.setDownloadClickListener
import top.yukonga.update.logic.utils.FileUtils
import top.yukonga.update.logic.utils.HapticUtils.hapticConfirm
import top.yukonga.update.logic.utils.HapticUtils.hapticReject
import top.yukonga.update.logic.utils.InfoUtils.getRecoveryRomInfo
import top.yukonga.update.logic.utils.LoginUtils

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

        // Inflate view.
        inflateView()

        // Enable edge to edge.
        setupEdgeToEdge()

        // Setup Cutout mode.
        setupCutoutMode()

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
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val recoveryRomInfo = json.decodeFromString<RomInfoHelper.RomInfo>(
                            getRecoveryRomInfo(
                                this@MainActivity,
                                codeNameExt,
                                regionCode,
                                systemVersionTextExt,
                                androidVersion
                            )
                        )

                        withContext(Dispatchers.Main) {
                            prefs.edit().putString("deviceName", deviceName).putString("codeName", codeName).putString("deviceRegion", deviceRegion)
                                .putString("systemVersion", systemVersion).putString("androidVersion", androidVersion).apply()

                            // Hide all cardViews & Show a toast if we didn't get anything from request.
                            if (recoveryRomInfo.currentRom?.branch == null) {
                                mainViewModel.apply {
                                    device = null
                                    filename = null
                                }
                                setupCardViews()
                                showStringToast(this@MainActivity, getString(R.string.toast_no_info), 0)
                                throw NoSuchFieldException()
                            }

                            // Show a toast if we detect that the login has expired.
                            if (FileUtils.isCookiesFileExists(this@MainActivity)) {
                                val cookiesFile = FileUtils.readCookiesFile(this@MainActivity)
                                val cookies = json.decodeFromString<MutableMap<String, String>>(cookiesFile)
                                val description = cookies["description"].toString()
                                val authResult = cookies["authResult"].toString()
                                if (description.isNotEmpty() && recoveryRomInfo.authResult != 1 && authResult != "-1") {
                                    loginIcon.setImageResource(R.drawable.ic_error)
                                    loginTitle.text = getString(R.string.login_expired)
                                    loginDesc.text = getString(R.string.login_expired_desc)
                                    cookies.clear()
                                    cookies["authResult"] = "-1"
                                    FileUtils.saveCookiesFile(this@MainActivity, Json.encodeToString(cookies))
                                    if (prefs.getString("auto_login", "") == "1") {
                                        showStringToast(this@MainActivity, getString(R.string.login_expired_auto), 1)
                                        LoginUtils().login(
                                            this@MainActivity,
                                            LoginUtils().getAccountAndPassword(this@MainActivity).first,
                                            LoginUtils().getAccountAndPassword(this@MainActivity).second,
                                            prefs.getString("global", "") ?: "0",
                                            prefs.getString("save_password", "") ?: "0",
                                            true
                                        )
                                    } else {
                                        showStringToast(this@MainActivity, getString(R.string.login_expired_dialog), 0)
                                        activityMainBinding.apply {
                                            toolbar.menu.findItem(R.id.login).isVisible = true
                                            toolbar.menu.findItem(R.id.logout).isVisible = false
                                        }
                                    }
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
                                    officialDownload = if (recoveryRomInfo.currentRom.md5 == recoveryRomInfo.latestRom?.md5) {
                                        getString(R.string.official1_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.latestRom.filename)
                                    } else {
                                        getString(R.string.official2_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.currentRom.filename)
                                    }
                                    officialText = if (recoveryRomInfo.currentRom.md5 == recoveryRomInfo.latestRom?.md5) {
                                        getString(R.string.official, "ultimateota")
                                    } else {
                                        getString(R.string.official, "bigota")
                                    }
                                    cdn1Download = if (recoveryRomInfo.currentRom.md5 == recoveryRomInfo.latestRom?.md5) {
                                        getString(R.string.cdn1_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.latestRom.filename)
                                    } else {
                                        getString(R.string.cdn1_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.currentRom.filename)
                                    }
                                    cdn2Download = if (recoveryRomInfo.currentRom.md5 == recoveryRomInfo.latestRom?.md5) {
                                        getString(R.string.cdn2_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.latestRom.filename)
                                    } else {
                                        getString(R.string.cdn2_link, recoveryRomInfo.currentRom.version, recoveryRomInfo.currentRom.filename)
                                    }
                                    changelog = log.toString().trimEnd()
                                }
                            } else {
                                mainViewModel.filename = null
                            }
                            setupCardViews()

                        } // Dispatchers.Main

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } // Coroutine

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
        val title = layoutInflater.inflate(R.layout.dialog_login, view, false)
        title.findViewById<MaterialCheckBox>(R.id.global).apply {
            isChecked = prefs.getString("global", "") == "1"
            setOnCheckedChangeListener { _, isChecked ->
                prefs.edit().putString("global", if (isChecked) "1" else "0").apply()
            }
        }
        val inputAccountLayout = createTextInputLayout(getString(R.string.account))
        val inputAccount = createTextInputEditText().apply {
            setText(LoginUtils().getAccountAndPassword(this@MainActivity).first)
        }
        inputAccountLayout.addView(inputAccount)
        val inputPasswordLayout = createTextInputLayout(getString(R.string.password), TextInputLayout.END_ICON_PASSWORD_TOGGLE)
        val inputPassword = createTextInputEditText(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD).apply {
            setText(LoginUtils().getAccountAndPassword(this@MainActivity).second)
        }
        inputPasswordLayout.addView(inputPassword)
        val savePasswordCheckBox =
            createCheckBox("save_password", R.string.save_password, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(23.dp, 0.dp, 0.dp, 0.dp)
            })
        val autoLoginCheckBox = createCheckBox("auto_login",
            R.string.auto_login,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f).apply {
                setMargins(23.dp, 0.dp, 27.dp, 0.dp)
            })
        savePasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putString("save_password", if (isChecked) "1" else "0").apply()
            autoLoginCheckBox.isEnabled = isChecked
            if (!isChecked) autoLoginCheckBox.isChecked = false
        }
        autoLoginCheckBox.isEnabled = savePasswordCheckBox.isChecked
        autoLoginCheckBox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putString("auto_login", if (isChecked) "1" else "0").apply()
        }
        val checkBoxLinearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            addView(savePasswordCheckBox)
            addView(autoLoginCheckBox)
        }
        view.apply {
            addView(title)
            addView(inputAccountLayout)
            addView(inputPasswordLayout)
            addView(checkBoxLinearLayout)
        }
        MaterialAlertDialogBuilder(this@MainActivity).apply {
            setView(view)
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                hapticReject(activityMainBinding.toolbar)
                dialog.dismiss()
            }
            setPositiveButton(getString(R.string.login)) { _, _ ->
                hapticConfirm(activityMainBinding.toolbar)
                val global = prefs.getString("global", "") ?: "0"
                val savePassword = prefs.getString("save_password", "") ?: "0"
                val mInputAccount = inputAccount.text.toString()
                val mInputPassword = inputPassword.text.toString()
                lifecycleScope.launch(Dispatchers.IO) {
                    val isValid = LoginUtils().login(this@MainActivity, mInputAccount, mInputPassword, global, savePassword)
                    withContext(Dispatchers.Main) {
                        if (isValid) {
                            mainContentBinding.apply {
                                loginIcon.setImageResource(R.drawable.ic_check_circle)
                                loginTitle.text = getString(R.string.logged_in)
                                loginDesc.text = getString(R.string.using_v2)
                            }
                            activityMainBinding.apply {
                                toolbar.menu.findItem(R.id.login).isVisible = false
                                toolbar.menu.findItem(R.id.logout).isVisible = true
                            }
                        }
                    }
                }
            }
        }.show()
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this@MainActivity).apply {
            setTitle(getString(R.string.logout))
            setMessage(getString(R.string.logout_desc)).setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                hapticReject(activityMainBinding.toolbar)
                dialog.dismiss()
            }
            setPositiveButton(getString(R.string.confirm)) { _, _ ->
                hapticConfirm(activityMainBinding.toolbar)
                LoginUtils().logout(this@MainActivity)
                mainContentBinding.apply {
                    loginIcon.setImageResource(R.drawable.ic_cancel)
                    loginTitle.text = getString(R.string.no_account)
                    loginDesc.text = getString(R.string.login_desc)
                }
                activityMainBinding.apply {
                    toolbar.menu.findItem(R.id.login).isVisible = true
                    toolbar.menu.findItem(R.id.logout).isVisible = false
                }
            }
        }.show()
    }

    private fun showAboutDialog() {
        val rootView = MaterialAlertDialogBuilder(this@MainActivity).setView(R.layout.dialog_about).show()
        val versionTextView = rootView.findViewById<TextView>(R.id.version)!!
        val githubSpannableTextView = rootView.findViewById<TextView>(R.id.github)!!

        versionTextView.text = getString(R.string.app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString())
        githubSpannableTextView.text = Html.fromHtml(getString(R.string.app_github), Html.FROM_HTML_MODE_COMPACT)
        githubSpannableTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupEdgeToEdge() {

        // Enable edge to edge
        enableEdgeToEdge()
        if (AppUtils.atLeast(Build.VERSION_CODES.Q)) window.isNavigationBarContrastEnforced = false

        // Add insets
        activityMainBinding.root.addInsetsByPadding(top = true)
        activityMainBinding.appBarLayout.addInsetsByPadding(left = true, right = true)
        activityMainBinding.implement.addInsetsByMargin(bottom = true, left = true, right = true)
        mainContentBinding.scrollView.addInsetsByPadding(left = true, right = true, bottom = true)

    }

    private fun setupCutoutMode() {
        if (AppUtils.atLeast(Build.VERSION_CODES.P)) {
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
                if (hasFocus) hideKeyBoard(this@MainActivity, view)
            }
            androidVersionDropdown.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) hideKeyBoard(this@MainActivity, view)
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
            (deviceName.editText as? MaterialAutoCompleteTextView)?.apply {
                setAdapter(deviceNamesAdapter)
                setDropDownBackgroundDrawable(AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_dropdown_background))
            }
            (codeName.editText as? MaterialAutoCompleteTextView)?.apply {
                setAdapter(codeNamesAdapter)
                setDropDownBackgroundDrawable(AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_dropdown_background))
            }
            (deviceRegion.editText as? MaterialAutoCompleteTextView)?.apply {
                setAdapter(deviceRegionAdapter)
                dropDownHeight = 280.dp
                setDropDownBackgroundDrawable(AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_dropdown_background))
            }
            (androidVersion.editText as? MaterialAutoCompleteTextView)?.apply {
                setAdapter(androidVersionAdapter)
                dropDownHeight = 280.dp
                setDropDownBackgroundDrawable(AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_dropdown_background))
            }

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
                changelogInfo.setCopyClickListener(this@MainActivity, mainViewModel.changelog)
                official.text = mainViewModel.officialText
                officialDownload.setDownloadClickListener(this@MainActivity, mainViewModel.filename, mainViewModel.officialDownload!!)
                officialCopy.setCopyClickListener(this@MainActivity, mainViewModel.officialDownload)
                cdn1Download.setDownloadClickListener(this@MainActivity, mainViewModel.filename, mainViewModel.cdn1Download!!)
                cdn1Copy.setCopyClickListener(this@MainActivity, mainViewModel.cdn1Download)
                cdn2Download.setDownloadClickListener(this@MainActivity, mainViewModel.filename, mainViewModel.cdn2Download!!)
                cdn2Copy.setCopyClickListener(this@MainActivity, mainViewModel.cdn2Download)
            } else {
                secondViewArray.forEach {
                    if (it.isVisible) it.fadOutAnimation()
                }
            }
        }
    }

    private fun setupTopAppBar() {
        activityMainBinding.toolbar.apply {
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
            val cookies = json.decodeFromString<MutableMap<String, String>>(cookiesFile)
            val description = cookies["description"].toString()
            val authResult = cookies["authResult"].toString()
            if (authResult == "-1") {
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
                    toolbar.menu.findItem(R.id.login).isVisible = false
                    toolbar.menu.findItem(R.id.logout).isVisible = true
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

    private fun createCheckBox(prefKey: String, textId: Int, layoutParams: LinearLayout.LayoutParams): MaterialCheckBox {
        return MaterialCheckBox(this).apply {
            isChecked = prefs.getString(prefKey, "") == "1"
            minimumHeight = 0
            text = getString(textId)
            this.layoutParams = layoutParams
        }
    }
}
