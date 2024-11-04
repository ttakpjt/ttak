package com.ttak.android.utils

import android.content.Context
import android.content.pm.PackageManager

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