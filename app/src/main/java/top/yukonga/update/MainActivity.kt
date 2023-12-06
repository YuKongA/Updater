package top.yukonga.update

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import top.yukonga.update.utils.Utils.getRomInfo
import top.yukonga.update.view.CustomAutoCompleteTextView

private val dropDownList = arrayOf("12", "13", "14")

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val autoCompleteTextView = findViewById<View>(R.id.android_version_dropdown) as CustomAutoCompleteTextView
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dropDownList)
        autoCompleteTextView.setAdapter(adapter)

        val codename = findViewById<View>(R.id.code_name) as TextInputLayout
        codename.editText?.setText("houji")
        val codenameText = codename.editText?.text.toString()

        val system = findViewById<View>(R.id.system_version) as TextInputLayout
        system.editText?.setText("OS1.0.26.0.UNCCNXM")
        val systemText = system.editText?.text.toString()

        val android = findViewById<View>(R.id.android_version) as TextInputLayout
        android.editText?.setText("14")
        val androidText = android.editText?.text.toString()

        val implement = findViewById<View>(R.id.implement) as ExtendedFloatingActionButton
        implement.setOnClickListener {
            val romInfo = getRomInfo(codenameText, systemText, androidText)
            Log.d("MIUI_UPDATE_INFO-rseult", romInfo)
            // TODO
        }

    }
}