package top.yukonga.update.activity.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class CustomMaterialAutoCompleteTextView : MaterialAutoCompleteTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun performFiltering(text: CharSequence, keyCode: Int) {
        super.performFiltering("", 0)
    }

    override fun setDropDownBackgroundDrawable(d: Drawable?) {
        super.setDropDownBackgroundDrawable(d)
    }
}