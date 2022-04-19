package com.final_project.crowd_counting.base.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.final_project.crowd_counting.base.R
import com.final_project.crowd_counting.base.constant.Constant
import com.final_project.crowd_counting.base.model.DateHolder
import com.final_project.crowd_counting.base.view.rv.adapter.CountryListAdapter
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

object Util {
  fun getThemeAttribute(themeAttribute: Int, context: Context): Int {
    val typedValue = TypedValue()
    val theme = context.theme
    theme.resolveAttribute(themeAttribute, typedValue, true)
    return typedValue.data
  }

  fun getScreenDimension(context: Context, inDp: Boolean = true): Pair<Int, Int>{
    val idStatusBarHeight = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    var statusBarHeight = 0
    if (idStatusBarHeight > 0){
      statusBarHeight = context.resources.getDimensionPixelSize(idStatusBarHeight)
    }
    context.resources.displayMetrics.run {
      return if (inDp){
        Pair((widthPixels/density).toInt(), ((heightPixels-statusBarHeight)/density).toInt())
      } else {
        Pair(widthPixels, heightPixels-statusBarHeight)
      }
    }
  }

  fun Int?.orDefaultInt(defaultInt: Int? = null): Int = this ?: defaultInt ?: 0

  fun String?.orDefaultStr(defaultStr: String? = null): String = this ?: defaultStr ?: ""

  fun ceilInt(number1: Int, number2: Int): Int {
    return if (number1%number2 == 0) {
      number1/number2
    } else {
      (number1/number2)+1
    }
  }

  fun Int?.formatRp(defaultInt: Int? = null): String {
    val priceSplit = this.toString().toList()
    var result = "Rp. "
    var i = 1
    if (priceSplit.size > 3){
      for (c in priceSplit){
        if (i == priceSplit.size % 3 || (i - (priceSplit.size % 3)) % 3 == 0){
          result += c
          if (i < priceSplit.size)
            result += "."
        } else {
          result += c
        }
        i++
      }
    } else {
      result += this ?: defaultInt ?: 0
    }
    return result
  }

  fun Boolean?.orDefaultBool(defaultBool: Boolean? = null): Boolean = this ?: defaultBool ?: false

  fun Int.formatTime(): String {
    return if (this < 10){
      "0${this}"
    } else {
      this.toString()
    }
  }

  fun String.formatTime(): Int {
    val trim = this.split("")
    return if (trim[0] == "0"){
      trim[1].toInt()
    } else {
      this.toInt()
    }
  }

  fun getSeparateDate(): List<Int>{
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    return listOf(
      calendar.get(Calendar.DATE),
      calendar.get(Calendar.MONTH) + 1,
      calendar.get(Calendar.YEAR),
      calendar.get(Calendar.HOUR_OF_DAY),
      calendar.get(Calendar.MINUTE)
    )
  }

  fun formatDate(year: Int, month: Int, dayOfMonth: Int): String{
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)
    val dayName = when(calendar.get(Calendar.DAY_OF_WEEK)){
      1 -> "Sun"
      2 -> "Mon"
      3 -> "Tue"
      4 -> "Wed"
      5 -> "Thu"
      6 -> "Fri"
      else -> "Sat"
    }
    val monthName = when(month){
      0 -> "Jan"
      1 -> "Feb"
      2 -> "Mar"
      3 -> "Apr"
      4 -> "May"
      5 -> "Jun"
      6 -> "Jul"
      7 -> "Aug"
      8 -> "Sep"
      9 -> "Oct"
      10 -> "Nov"
      else -> "Dec"
    }

    return "$dayName, $dayOfMonth $monthName $year"
  }

  fun getCurrentDate(): String{
    val c: Date = Calendar.getInstance().time
    val df = SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault())
    return df.format(c)
  }

  fun getCurrentTime(): String{
    val c: Date = Calendar.getInstance().time
    val df = SimpleDateFormat("kk:mm", Locale.getDefault())
    return df.format(c)
  }

  private fun timeStampToDateHolder(timeStamp: Long): DateHolder {
    val dates = SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.getDefault()).format(timeStamp)
    val separateDate = dates.split("-")
    return DateHolder(
      separateDate[0].toInt(),
      separateDate[1].toInt(),
      separateDate[2].toInt(),
      separateDate[3].toInt(),
      separateDate[4].toInt()
    )
  }

  fun dateToMillis(
    date: String,
    pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'",
    timezone: TimeZone = TimeZone.getTimeZone("GMT")
  ): Long {
    try {
      SimpleDateFormat(pattern, Locale.getDefault()).run {
        timeZone = timezone
        return parse(sanitizeDate(date))!!.time
      }
    } catch (ex: Exception){
      ex.printStackTrace()
      return 0L
    }
  }

  fun millisToDate(
    timeStamp: Long,
    pattern: String = "dd MMM yyyy",
    timezone: TimeZone = TimeZone.getDefault()
  ): String{
    return try {
      SimpleDateFormat(pattern, Locale.getDefault()).run {
        timeZone = timezone
        format(timeStamp)
      }
    } catch (ex: Exception){
      ex.printStackTrace()
      ""
    }
  }

  fun gmtToLocal(date: String, from: String = "yyyy-MM-dd'T'HH:mm:ss'Z'", to: String = "dd MMM yyyy"): String{
    val timeMillis = dateToMillis(date,from)
    return millisToDate(timeMillis, to)
  }

  fun localToGmt(date: Long, pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): String{
    return millisToDate(date, pattern, TimeZone.getTimeZone("GMT"))
  }

  fun rangeTime(timeStamp: Long, hourRange: Int = 2): String{
    val dateHolder = timeStampToDateHolder(timeStamp)
    dateHolder.run {
      return hourOfDay.formatTime() + ":" + minute.formatTime() + "-" + ((hourOfDay+hourRange)%24).formatTime() + ":" + minute.formatTime()
    }
  }

  private fun sanitizeDate(date: String): String{
    return try {
      date.split(":").toMutableList().apply {
        removeLast()
        add("00Z")
      }.joinToString(":")
    } catch (e: Exception){
      ""
    }
  }

  fun makeBold(str: String, start: Int, end: Int): SpannableStringBuilder {
    val spannableString = SpannableStringBuilder(str)
    val styleSpan =  StyleSpan(Typeface.BOLD)
    spannableString.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    return spannableString
  }

  fun makeBoldAllAndColorPart(str: String, start: Int, end: Int, @ColorInt
  color: Int): SpannableStringBuilder {
    val spannableString = SpannableStringBuilder(str)
    spannableString.setSpan(
      StyleSpan(Typeface.BOLD),
      start,
      end,
      Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    spannableString.setSpan(
      ForegroundColorSpan(color),
      start,
      end,
      Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    return spannableString
  }

  fun SpannableStringBuilder.setColor(start: Int, end: Int, @ColorInt color: Int): SpannableStringBuilder{
    this.setSpan(
      ForegroundColorSpan(color),
      start,
      end,
      Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    return this
  }

  fun colorString(
    str: String, start: Int, end: Int, @ColorInt color: Int
  ): SpannableStringBuilder {
    val spannableString = SpannableStringBuilder(str)
    spannableString.setSpan(
      ForegroundColorSpan(color),
      start,
      end,
      Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    return spannableString
  }

  fun manyColorString(
    str: String, startList: List<Int>, endList: List<Int>, @ColorInt colorList: List<Int>
  ): SpannableStringBuilder {
    val spannableString = SpannableStringBuilder(str)
    startList.forEachIndexed { index, i ->
      spannableString.setSpan(
        ForegroundColorSpan(colorList[index]),
        startList[index],
        endList[index],
        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
      )
    }
    return spannableString
  }

  fun TextView.truncate(isSingleLine: Boolean = true, maxOfLines: Int? = null, mode: TextUtils.TruncateAt = TextUtils.TruncateAt.END){
    val single = (isSingleLine && maxOfLines == null) || maxOfLines == 1
    setSingleLine(single)
    maxOfLines?.let { maxLines = it }
    ellipsize = mode
  }

  fun AutoCompleteTextView.setCountryListAdapter(){
    setAdapter(CountryListAdapter(context, getCountryAndFlagList(context)))
  }

  fun TextInputLayout.setOnSelectedCounty(country: String, drawable: Drawable?){
    (editText as? AutoCompleteTextView)?.setText(country, false)
    drawable?.let {
      startIconDrawable = it
    } ?: run {
      startIconDrawable = getCountryAndFlagList(context).firstOrNull { it.first == country }?.second
    }
    isStartIconVisible = true
  }

  fun getCountryFlagDrawable(context: Context, country: String): Drawable?{
    val countryIndex = context.resources.getStringArray(R.array.country_names).indexOfFirst {
      it.equals(country)
    }
    val flags = context.resources.obtainTypedArray(R.array.country_flags)
    return flags.getDrawable(countryIndex)
  }

  fun TextView.setCountryFlag(context: Context, country: String, drawablePosition: Int = 0){
    text = makeBold(country, 0, country.length)
    val countryIndex = context.resources.getStringArray(R.array.country_names).indexOfFirst {
      it.equals(country)
    }
    val flags = context.resources.obtainTypedArray(R.array.country_flags)
    when(drawablePosition){
      0 -> setCompoundDrawablesWithIntrinsicBounds(flags.getDrawable(countryIndex), null,null,null)
      1 -> setCompoundDrawablesWithIntrinsicBounds(null, flags.getDrawable(countryIndex),null,null)
      2 -> setCompoundDrawablesWithIntrinsicBounds(null, null, flags.getDrawable(countryIndex),null)
      else -> setCompoundDrawablesWithIntrinsicBounds(null, null,null,flags.getDrawable(countryIndex))
    }
    flags.recycle()
  }

  fun TextView.setDrawableTint(@ColorRes res: Int){
    if (compoundDrawables[0] == null) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      compoundDrawables[0].setTint(ContextCompat.getColor(context, res))
    }
  }

  fun getCountryAndFlagList(context: Context): List<Pair<String, Drawable>>{
    return context.resources.getStringArray(R.array.country_names) zip context.resources.obtainTypedArray(R.array.country_flags).run {
      val flags = mutableListOf<Drawable>()
      for (i in 0 until this.length()){
        getDrawable(i)?.let {
          flags.add(it)
        }
      }
      Log.d("LENGTH DROP", flags.size.toString())
      recycle()
      flags
    }
  }

  fun Fragment.checkPermission(
    permissionType: String = Manifest.permission.READ_EXTERNAL_STORAGE,
    requestCode: Int = Constant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE,
    message: String = "External storage permission is necessary"
  ): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (ContextCompat.checkSelfPermission(requireContext(), permissionType) != PackageManager.PERMISSION_GRANTED) {
        if (shouldShowRequestPermissionRationale(permissionType)) {
          showDialog(permissionType, requestCode, message)
        } else {
          ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            requestCode
          )
        }
        false
      } else {
        true
      }
    } else {
      true
    }
  }

  private fun Fragment.showDialog(permissionType: String, requestCode: Int, message: String) {
    val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
    alertBuilder.setCancelable(true)
    alertBuilder.setTitle("Permission necessary")
    alertBuilder.setMessage(message)
    alertBuilder.setPositiveButton(android.R.string.yes
    ) { dialog, _ ->
      ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(permissionType),
        requestCode
      )
    }
    val alert: AlertDialog = alertBuilder.create()
    alert.show()
  }

  fun Fragment.hideKeyboard(){
    (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).run {
      requireActivity().currentFocus?.let {
        hideSoftInputFromWindow(it.windowToken, 0)
      } ?: run {
        view?.requestFocus()
        hideSoftInputFromWindow(view?.windowToken, 0)
      }
    }
  }

  fun ImageView.loadImage(url: String, radius: List<Float> = listOf(10.0F,10.0F,0.0F,0.0F)){
    Glide.with(context)
      .load(url)
      .run {
        if (radius.size == 4)
          transform(CenterCrop(), GranularRoundedCorners(radius[0], radius[1], radius[2], radius[3]))
        else
          transform(CenterCrop(), GranularRoundedCorners(0.0F, 0.0F, 0.0F, 0.0F))
      }
      .placeholder(R.drawable.ic_no_image)
      .into(this)
  }

  fun ImageView.loadCircularImage(url: String){
    Glide.with(context)
      .load(url)
      .placeholder(R.drawable.ic_user_circle)
      .circleCrop()
      .into(this)
  }

  fun NavController.safeNavigate(@IdRes actionId: Int, arg: Bundle? = null, navOptions: NavOptions? = null){
    currentDestination?.getAction(actionId)?.run {
      navigate(actionId, arg, navOptions)
    }
  }

  fun <T> MutableList<T>.removeRange(start: Int, itemCount: Int){
    try {
      val end = start+itemCount-1
      for (i in  end downTo start){
        removeAt(i)
      }
    } catch (e: Exception){
      e.printStackTrace()
    }
  }

  fun String?.toInteger(defaultInt: Int? = null): Int {
    return when {
      this.isNullOrEmpty() -> 0
      defaultInt != null -> defaultInt
      else -> toInt()
    }
  }

  fun validateEmpty(listView: List<EditText>, message: List<String>): Boolean {
    var isValid = true
    listView.forEachIndexed { index, editText ->
      if (editText.text.isNullOrEmpty()){
        isValid = false
        editText.error = editText.context.getString(R.string.empty_not_allowed_message, message[index])
      } else {
        editText.error = null
      }
    }
    return isValid
  }

  fun validateEmailFormat(editText: EditText): Boolean{
    if (!Patterns.EMAIL_ADDRESS.matcher(editText.text.toString()).matches()){
      editText.error = editText.context.getString(R.string.wrong_email_format)
      return false
    }
    editText.error = null
    return true
  }

  fun Double?.roundStr(digitAfter: Int = 2): String{
    if (this == null)
      return "0.0"
    return "%.${digitAfter}f".format(this)
  }

  fun Float?.roundStr(digitAfter: Int = 2): String{
    if (this == null)
      return "0.0"
    return "%.${digitAfter}f".format(this)
  }

  fun Double?.roundDouble(digitAfter: Int = 2): Double{
    if (this == null)
      return 0.0
    else
      return "%.${digitAfter}f".format(this).toDouble()
  }

  fun Float?.roundDouble(digitAfter: Int = 2): Float{
    if (this == null)
      return 0.0F
    else
      return "%.${digitAfter}f".format(this).toFloat()
  }

  fun isInternetAvailable(context: Context): Boolean {
    (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
      return activeNetworkInfo != null && activeNetworkInfo?.isConnected.orDefaultBool(false)
    }
  }

}