package com.example.musicoblaka

import android.content.Context
import android.os.Environment
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object DownloadUtil {

    fun downloadFile(context: Context, fileUrl: String, fileName: String): String? {
        val url = URL(fileUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            return null
        }

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
        val inputStream: InputStream = connection.inputStream
        val outputStream: OutputStream = FileOutputStream(file)

        try {
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
        } finally {
            inputStream.close()
            outputStream.close()
        }

        return file.absolutePath
    }
}
