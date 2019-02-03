package io.github.droidkaigi.confsched2019.session.ui.bindingadapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import io.github.droidkaigi.confsched2019.session.R
import java.util.regex.Pattern

@BindingAdapter(value = ["app:highlightText"])
fun setHighlightText(view: TextView, highlightText: String?) {
    // By toString, clear highlight text.
    val stringBuilder = SpannableStringBuilder(view.text.toString())
    if (TextUtils.isEmpty(highlightText)) {
        view.text = stringBuilder
        return
    }
    val quotedHighlightText = Pattern.quote(highlightText)
    val pattern = Pattern.compile(quotedHighlightText, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(view.text)
    while (matcher.find()) {
        stringBuilder.setSpan(
            BackgroundColorSpan(ContextCompat.getColor(view.context, R.color.colorSecondary_50)),
            matcher.start(),
            matcher.end(),
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        stringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            matcher.start(),
            matcher.end(),
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
    view.text = stringBuilder
}
