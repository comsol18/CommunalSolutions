package comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles

import android.app.Activity
import android.support.v4.app.NavUtils
import android.util.Log

fun eLog(tag: String, msg: String) {
    Log.e(tag, msg)
    Thread.sleep(1)
}

fun dLog(tag: String, msg: String) {
    Log.d(tag, msg)
    Thread.sleep(1)
}

fun formatObject(obj: String): String {
    val name = obj.substringBefore('(')
    val lead = "$name Object"
    val values: ArrayList<String> = ArrayList()

    var str = obj.substringAfter('(')
    while (true) {
        if (str.contains(", ")) {
            values.add(str.substringBefore(", "))
            str = str.substringAfter(", ")
        } else {
            values.add(str.substringBefore(')'))
            break
        }
    }

    var result = "$lead\n$name("
    for (value in values) { result += "\n\t$value" }
    result += "\n)"
    return result
}

fun navigateUp(activity: Activity) {
    NavUtils.navigateUpTo(activity, NavUtils.getParentActivityIntent(activity))
}
