package de.tum.`in`.tumcampusapp.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.BatteryManager
import android.os.Build
import android.preference.PreferenceManager
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.widget.Toast
import com.google.common.base.Charsets
import com.google.common.escape.CharEscaperBuilder
import com.google.common.hash.Hashing
import com.google.gson.Gson
import de.tum.`in`.tumcampusapp.BuildConfig
import java.io.IOException
import java.io.InputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.util.regex.Pattern

/**
 * Class for common helper functions used by a lot of classes
 */
object Utils {
    private val LOGGING_REGEX = "[a-zA-Z0-9.]+\\."

    /**
     * Builds a HTML document out of a css file and the body content.
     *
     * @param css  The CSS specification
     * @param body The body content
     * @return The HTML document.
     */
    fun buildHTMLDocument(css: String, body: String) =
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"de\" lang=\"de\">" +
                    "<head><meta name=\"viewport\" content=\"width=device-width\" />" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
                    "</head>" +
                    "<style type=\"text/css\">$css</style>" +
                    "<body>$body</body>" +
                    "</html>"


    /**
     * Cut substring from a text.
     *
     * @param text        The text.
     * @param startString Start string where the cutting begins.
     * @param endString   End string where the cutting ends.
     * @return The cut text.
     */
    fun cutText(text: String, startString: String, endString: String) =
            text.substringAfter(startString).substringBefore(endString)

    /**
     * Get a value from the default shared preferences
     *
     * @param c          Context
     * @param key        setting name
     * @param defaultVal default value
     * @return setting value, "" if undefined
     */
    fun getSetting(c: Context, key: String, defaultVal: String): String =
            PreferenceManager.getDefaultSharedPreferences(c)
                    .getString(key, defaultVal)


    /**
     * Get a value from the default shared preferences
     *
     * @param c         Context
     * @param key       setting name
     * @param classInst e.g. ChatMember.class
     * @return setting value, "" if undefined
     */
    fun <T> getSetting(c: Context, key: String, classInst: Class<T>) =
            PreferenceManager.getDefaultSharedPreferences(c)
                    .getString(key, null)
                    ?.let {
                        Gson().fromJson(it, classInst)
                    }


    /**
     * Return the boolean value of a setting
     *
     * @param c          Context
     * @param name       setting name
     * @param defaultVal default value
     * @return true if setting was checked, else value
     */
    fun getSettingBool(c: Context, name: String, defaultVal: Boolean) =
            PreferenceManager.getDefaultSharedPreferences(c)
                    .getBoolean(name, defaultVal)

    /**
     * Logs an exception and additional information
     * Use this anywhere in the app when a fatal error occurred.
     * If you can give a better description of what went wrong
     * use [.log] instead.
     *
     * @param e Exception (source for message and stack trace)
     */
    fun log(e: Throwable) =
            try {
                StringWriter().use { sw ->
                    e.printStackTrace(PrintWriter(sw))
                    val s = Thread.currentThread()
                            .stackTrace[3].className
                            .replace(LOGGING_REGEX.toRegex(), "")
                    Log.e(s, e.toString() + "\n" + sw)
                }
            } catch (e1: IOException) {
                // there is a time to stop logging errors
            }


    /**
     * Logs an exception and additional information
     * Use this anywhere in the app when a fatal error occurred.
     * If you can't give an exact error description simply use
     * [.log] instead.
     *
     * @param e       Exception (source for message and stack trace)
     * @param message Additional information for exception message
     */
    fun log(e: Throwable, message: String) =
            try {
                StringWriter().use { sw ->
                    e.printStackTrace(PrintWriter(sw))
                    val s = Thread.currentThread()
                            .stackTrace[3].className
                            .replace(LOGGING_REGEX.toRegex(), "")
                    Log.e(s, e.toString() + " " + message + '\n' + sw)
                }
            } catch (e1: IOException) {
                // there is a time to stop logging errors
            }

    /**
     * Logs a message
     * Use this to log the current app state.
     *
     * @param message Information or Debug message
     */
    fun log(message: String) {
        if (!BuildConfig.DEBUG) {
            return
        }
        val s = Thread.currentThread()
                .stackTrace[3].className
                .replace(LOGGING_REGEX.toRegex(), "")
        Log.d(s, message)
    }

    /**
     * Logs a message
     * Use this to log additional information that is not important in most cases.
     *
     * @param message Information or Debug message
     */
    fun logv(message: String) {
        if (!BuildConfig.DEBUG) {
            return
        }
        val s = Thread.currentThread()
                .stackTrace[3].className
                .replace(LOGGING_REGEX.toRegex(), "")
        Log.v(s, message)
    }

    /**
     * Logs a message with specified tag
     * Use this to log a particular work
     *
     * @param message Information or Debug message
     */
    fun logwithTag(tag: String, message: String) {
        if (!BuildConfig.DEBUG) {
            return
        }
        Log.v(tag, message)
    }

    /**
     * Get a persistent hash from string
     *
     * @param str String to hash
     * @return hash hash as string
     */
    fun hash(str: String) =
            Hashing.murmur3_128()
                    .hashBytes(str.toByteArray(Charsets.UTF_8))
                    .toString()

    /**
     * Returns a String[]-List from a CSV input stream
     *
     * @param fin CSV input stream
     * @return String[]-List with Columns matched to array values
     */
    fun readCsv(fin: InputStream) =
            fin.bufferedReader().useLines {
                it.map(this::splitCsvLine)
                        .toList()
            }

    /**
     * Sets the value of a setting
     *
     * @param c   Context
     * @param key setting key
     */
    fun setSetting(c: Context, key: String, value: Boolean) =
            PreferenceManager.getDefaultSharedPreferences(c)
                    .edit()
                    .putBoolean(key, value)
                    .apply()

    /**
     * Sets the value of a setting
     *
     * @param c     Context
     * @param key   setting key
     * @param value String value
     */
    fun setSetting(c: Context, key: String, value: String) =
            PreferenceManager.getDefaultSharedPreferences(c)
                    .edit()
                    .putString(key, value)
                    .apply()

    /**
     * Sets the value of a setting
     *
     * @param c   Context
     * @param key setting key
     */
    fun setSetting(c: Context, key: String, value: Any) =
            PreferenceManager.getDefaultSharedPreferences(c)
                    .edit()
                    .putString(key, Gson().toJson(value))
                    .apply()

    /**
     * Shows a long [Toast] message.
     *
     * @param context The activity where the toast is shown
     * @param msg     The toast message id
     */
    fun showToast(context: Context, msg: Int) =
            Toast.makeText(context, msg, Toast.LENGTH_LONG)
                    .show()


    /**
     * Shows a long [Toast] message.
     *
     * @param context The activity where the toast is shown
     * @param msg     The toast message
     */
    fun showToast(context: Context, msg: CharSequence) =
            Toast.makeText(context, msg, Toast.LENGTH_LONG)
                    .show()

    /**
     * Splits a line from a CSV file into column values
     *
     *
     * e.g. "aaa;aaa";"bbb";1 gets aaa,aaa;bbb;1;
     *
     * @param str CSV line
     * @return String[] with CSV column values
     */
    private fun splitCsvLine(str: CharSequence): Array<String> {
        val result = StringBuilder()
        var open = false
        for (i in 0 until str.length) {
            val c = str[i]
            if (c == '"') {
                open = !open
                continue
            }
            if (open && c == ';') {
                result.append(',')
            } else {
                result.append(c)
            }
        }
        // fix trailing ";", e.g. ";;;".split().length = 0
        result.append("; ")
        return result.toString()
                .split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     * Converts a meter based value to a formatted string
     *
     * @param meters Meters to represent
     * @return Formatted meters. e.g. 10m, 12.5km
     */
    fun formatDist(meters: Float) = when {
        meters < 1000 -> meters.toInt().toString() + "m"
        meters < 10000 -> {
            val front = (meters / 1000f).toInt()
            val back = Math.abs(meters / 100f).toInt() % 10
            front.toString() + "." + back + "km"
        }
        else -> (meters / 1000f).toInt().toString() + "km"
    }

    /**
     * Sets an internal preference's boolean value
     *
     * @param context Context
     * @param key     Key
     * @param value   Value
     */
    fun setInternalSetting(context: Context, key: String, value: Boolean) =
            context.getSharedPreferences(Const.INTERNAL_PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(key, value)
                    .apply()

    /**
     * Sets an internal preference's integer value
     *
     * @param context Context
     * @param key     Key
     * @param value   Value
     */
    fun setInternalSetting(context: Context, key: String, value: Int) =
            context.getSharedPreferences(Const.INTERNAL_PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putInt(key, value)
                    .apply()


    /**
     * Sets an internal preference's long value
     *
     * @param context Context
     * @param key     Key
     * @param value   Value
     */
    fun setInternalSetting(context: Context, key: String, value: Long) =
            context.getSharedPreferences(Const.INTERNAL_PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(key, value)
                    .apply()


    /**
     * Sets an internal preference's string value
     *
     * @param context Context
     * @param key     Key
     * @param value   Value
     */
    fun setInternalSetting(context: Context, key: String, value: String) =
            context.getSharedPreferences(Const.INTERNAL_PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putString(key, value)
                    .apply()

    /**
     * Sets an internal preference's string value
     *
     * @param context Context
     * @param key     Key
     * @param value   Value
     */
    fun setInternalSetting(context: Context, key: String, value: Float) =
            context.getSharedPreferences(Const.INTERNAL_PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putFloat(key, value)
                    .apply()

    /**
     * Gets an internal preference's boolean value
     *
     * @param context Context
     * @param key     Key
     * @param value   Default value
     * @return The value of the setting or the default value,
     * if no setting with the specified key exists
     */
    fun getInternalSettingBool(context: Context, key: String, value: Boolean) =
            context.getSharedPreferences(Const.INTERNAL_PREFS, Context.MODE_PRIVATE)
                    .getBoolean(key, value)

    /**
     * Gets an internal preference's integer value
     *
     * @param context Context
     * @param key     Key
     * @param value   Default value
     * @return The value of the setting or the default value,
     * if no setting with the specified key exists
     */
    fun getInternalSettingInt(context: Context, key: String, value: Int) =
            context.getSharedPreferences(Const.INTERNAL_PREFS, Context.MODE_PRIVATE)
                    .getInt(key, value)

    /**
     * Gets an internal preference's long value
     *
     * @param context Context
     * @param key     Key
     * @param value   Default value
     * @return The value of the setting or the default value,
     * if no setting with the specified key exists
     */
    fun getInternalSettingLong(context: Context, key: String, value: Long) =
            context.getSharedPreferences(Const.INTERNAL_PREFS, Context.MODE_PRIVATE)
                    .getLong(key, value)

    /**
     * Gets an internal preference's string value
     *
     * @param context Context
     * @param key     Key
     * @param value   Default value
     * @return The value of the setting or the default value,
     * if no setting with the specified key exists
     */
    fun getInternalSettingString(context: Context, key: String, value: String): String =
            context.getSharedPreferences(Const.INTERNAL_PREFS, Context.MODE_PRIVATE)
                    .getString(key, value)

    /**
     * Gets an internal preference's float value
     *
     * @param context Context
     * @param key     Key
     * @param value   Default value
     * @return The value of the setting or the default value,
     * if no setting with the specified key exists
     */
    fun getInternalSettingFloat(context: Context, key: String, value: Float) =
            context.getSharedPreferences(Const.INTERNAL_PREFS, Context.MODE_PRIVATE)
                    .getFloat(key, value)

    /**
     * @return Application's version code from the `PackageManager`.
     */
    fun getAppVersion(context: Context) = try {
        val packageInfo = context.packageManager
                .getPackageInfo(context.packageName, 0)
        packageInfo.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        // should never happen
        throw AssertionError("Could not get package name: " + e)
    }

    fun showToastOnUIThread(activity: Activity, s: Int) =
            activity.runOnUiThread { Utils.showToast(activity, s) }


    fun showToastOnUIThread(activity: Activity, s: CharSequence) =
            activity.runOnUiThread { Utils.showToast(activity, s) }

    /**
     * Removes all html tags from a string
     *
     * @param html text which contains html tags
     * @return cleaned text without any tags
     */
    fun stripHtml(html: String): String = fromHtml(html).toString()

    fun isBackgroundServicePermitted(context: Context) =
            isBackgroundServiceEnabled(context) && (isBackgroundServiceAlwaysEnabled(context) || NetUtils.isConnectedWifi(context))

    private fun isBackgroundServiceEnabled(context: Context) =
            Utils.getSettingBool(context, Const.BACKGROUND_MODE, false)

    private fun isBackgroundServiceAlwaysEnabled(context: Context) =
            "0" == Utils.getSetting(context, "background_mode_set_to", "0")

    @TargetApi(Build.VERSION_CODES.N)
    fun fromHtml(source: String): Spanned =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
            else
                Html.fromHtml(source)

    private fun umlautEscaper() =
            CharEscaperBuilder()
                    .addEscape('ä', "&auml;")
                    .addEscape('ö', "&ouml;")
                    .addEscape('ü', "&uuml;")
                    .toEscaper()

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun escapeUmlauts(text: String): String =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Html.escapeHtml(text)
            } else {
                umlautEscaper().escape(text)
            }
    // Just escape umlauts for older devices, MVV should be happy with that

    fun getBatteryLevel(context: Context): Float {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        return if (level == -1 || scale == -1) {
            -1f
        } else {
            level.toFloat() / scale.toFloat() * 100.0f
        }
    }

    fun extractRoomNumberFromLocation(location: String): String {
        val pattern = Pattern.compile("\\((.*?)\\)")
        val matcher = pattern.matcher(location)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            location
        }
    }

    /**
     * Creates a bitmap for a vector image (.xml) to be able to use it for notifications
     * @param c
     * @param res
     * @return
     */
    fun getLargeIcon(c: Context, res: Int): Bitmap {
        val icon = c.resources.getDrawable(res)
        val bitmap = Bitmap.createBitmap(icon.intrinsicWidth, icon.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        icon.setBounds(0, 0, canvas.width, canvas.height)
        icon.draw(canvas)
        return bitmap
    }
}// Utils is a utility class
