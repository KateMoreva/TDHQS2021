package map.together

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.io.IOException
import java.io.InputStream


class TestUtils {
    val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create()

    fun log(log: String?) {
        println(log)
    }

    fun <T> readJson(fileName: String?, inClass: Class<T>?): T {
        return gson.fromJson(readString(fileName), inClass)
    }

    fun readString(fileName: String?): String? {
        val stream: InputStream? = javaClass.classLoader.getResourceAsStream(fileName)
        return try {
            val size: Int = stream!!.available()
            val buffer = ByteArray(size)
            val result: Int = stream.read(buffer)
            String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                if (stream != null) {
                    stream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}