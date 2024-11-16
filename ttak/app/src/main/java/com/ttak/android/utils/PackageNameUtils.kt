package com.ttak.android.utils

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap

// 패키지명을 앱 이름으로 변환하는 함수
fun getAppNameFromPackageName(context: Context, packageName: String): String {
    return try {
        val packageManager: PackageManager = context.packageManager
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        packageManager.getApplicationLabel(appInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName // 패키지명을 그대로 반환
    }
}

// 이미지 URL을 파일명과 확장자 형식으로 변환
fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri): String {
    var fileName = "unknown_file"

    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            fileName = cursor.getString(nameIndex)
        }
    }

    return fileName
}

fun getMimeTypeFromExtension(extension: String): String {
    return when (extension.lowercase()) {
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "gif" -> "image/gif"
        "bmp" -> "image/bmp"
        else -> "application/octet-stream" // 기본값
    }
}
