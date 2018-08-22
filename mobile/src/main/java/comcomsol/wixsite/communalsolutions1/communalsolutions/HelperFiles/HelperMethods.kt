package comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles

import android.app.Activity
import android.support.v4.app.NavUtils
import android.util.Log
import java.io.IOException
import java.io.Reader

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

fun timeMethod(TAG: String, methodName: String): () -> Long {
    dLog(TAG, "Beginning $methodName")
    val start = System.currentTimeMillis()
    val stop: () -> Long = {
        val end= System.currentTimeMillis()
        dLog(TAG, "Finished $methodName in ${end-start} ms")
        end- start
    }
    return stop
}

@Throws(IOException::class)
fun readAll(rd: Reader): String {
    val text = rd.readText()
//    dLog("readText", text)
//    Thread.sleep(10000)
    return text
}

/*@Throws(IOException::class)
fun readAll(rd: Reader): String {
    val sb = StringBuilder()
    var cp: Int = rd.read()
    while (cp != -1) {
        sb.append(cp.toChar())
        cp = rd.read()
    }
    return sb.toString()
}*/

fun navigateUp(activity: Activity) {
    NavUtils.navigateUpTo(activity, NavUtils.getParentActivityIntent(activity))
}
