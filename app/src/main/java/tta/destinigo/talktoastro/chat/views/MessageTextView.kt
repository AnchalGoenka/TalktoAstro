package tta.destinigo.talktoastro.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import tta.destinigo.talktoastro.R

class MessageTextView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    init {
        var font: String? = attrs.getAttributeValue(androidNS, "fontFamily")
        if (font == null) {
            font = "AlegreyaSans-Light"
        }
        //this.typeface = Typeface.createFromAsset(context.assets, "fonts/$font.ttf")
        this.typeface = ResourcesCompat.getFont(context, R.font.open_sans)
    }

    companion object {
        private val androidNS = "http://schemas.android.com/apk/res/android"
    }
}
