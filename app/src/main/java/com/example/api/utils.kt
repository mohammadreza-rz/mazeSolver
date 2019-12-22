package com.example.api

//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.FragmentPagerAdapter
//import androidx.viewpager.widget.ViewPager
//import com.beust.klaxon.Klaxon
//import com.google.android.material.snackbar.Snackbar
//import com.google.android.material.tabs.TabLayout
//import enthusi4stic.mpm7.GenericFragmentPagerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

//private val klaxon = Klaxon()

//fun Any.toJson(): String = klaxon.toJsonString(this)

inline fun <reified T : Activity> Activity.open(vararg args: Pair<String, Serializable?>) {
    val move = Intent(this, T::class.java)
    args.forEach { (name, value) ->
        move.putExtra(name, value)
    }
    startActivity(move)
}

@JvmName("openWithParcelableArgs")
inline fun <reified T : Activity> Activity.open(vararg args: Pair<String, Parcelable?>) {
    val move = Intent(this, T::class.java)
    args.forEach { (name, value) ->
        move.putExtra(name, value)
    }
    startActivity(move)
}

inline fun <reified T : Activity> Activity.open() {
    open<T>(*emptyArray<Pair<String, Serializable>>())
}

inline fun <reified T : Activity> Activity.replaceWith(vararg args: Pair<String, Serializable?>) {
    open<T>(*args)
    finish()
}

inline fun <reified T : Activity> Activity.replaceWith() {
    replaceWith<T>(*emptyArray<Pair<String, Serializable>>())
}

@JvmName("replaceWithParcelableArgs")
inline fun <reified T : Activity> Activity.replaceWith(vararg args: Pair<String, Parcelable?>) {
    open<T>(*args)
    finish()
}

///**
// * Sets up a simple [FragmentPagerAdapter] that binds the receiver [TabLayout] to [viewPager] with
// * [Pair]<[String],[Fragment]> as a map of Fragments to their names to bind.
// * @author mohamadhassan ebrahimi
// */
//fun TabLayout.setupWithViewPager(
//    fragmentManager: FragmentManager,
//    viewPager: ViewPager,
//    vararg fragments: Pair<String, Fragment>
//) {
//    viewPager.adapter = GenericFragmentPagerAdapter(fragmentManager, *fragments)
//    setupWithViewPager(viewPager)
//}

/**
 * Simple Listener action for [TextView].
 * @author mohamadhassan ebrahimi
 */
fun TextView.setOnTextChangedListener(onChanged: (newText: String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            onChanged(p0?.toString() ?: "")
        }
    })
}

private val enToFaDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")

/**
 * Converts any digit numbers in English language in a text to their Persian counterparts.
 * @author mohamadhassan ebrahimi
 */
fun String.convertDigitsToPersian() =
    replace("\\d".toRegex()) {
        enToFaDigits[it.value.toInt()]
    }

enum class Orientation(val orientation: Int) {
    Horizontal(DividerItemDecoration.VERTICAL), Vertical(DividerItemDecoration.HORIZONTAL)
}

/**
 * Adds a divider to [RecyclerView] and returns it.
 * @param orientation Orientation
 * @author mohamadhassan ebrahimi
 */
fun RecyclerView.withDivider(orientation: Orientation = Orientation.Horizontal): RecyclerView {
    addItemDecoration(DividerItemDecoration(context, orientation.orientation))
    return this
}

//fun View.snackMessage(text: String) {
//    val bar = Snackbar.make(this, text, Snackbar.LENGTH_LONG)
//    val textView = bar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
//    textView.gravity = Gravity.CENTER_HORIZONTAL
//    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
//    bar.show()
//}

//fun View.snackMessage(textId: Int) {
//    val bar = Snackbar.make(this, textId, Snackbar.LENGTH_LONG)
//    val textView = bar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
//    textView.gravity = Gravity.CENTER_HORIZONTAL
//    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
//    bar.show()
//}

fun String.trimAround(prefix: String, suffix: String): String {
    val prefixReg = ".*?$prefix".toRegex()
    val suffixReg = ".*?$suffix".toRegex()
    val pref = prefixReg.find(this)?.value ?: ""
    val suff = suffixReg.find(this.reversed())?.value ?: ""
    return removeSurrounding(pref, suff)
}