package com.example.notificationsendemail.util

import android.content.Context
import android.content.Intent
import com.example.notificationsendemail.model.AppInfoData

object PackageUtil {
    fun getPackageList(context: Context): MutableList<AppInfoData>? {
        var result: MutableList<AppInfoData>? = null
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            this.addCategory(Intent.CATEGORY_LAUNCHER) // 설치된 앱 중 Category가 Launcher인 것만 가져오기 위함 (feat.시스템 앱 제외)
        }
        val packageManager = context.packageManager

        for (i in packageManager.queryIntentActivities(mainIntent, 0)) {
            if (result == null) {
                result = mutableListOf()
            }
            val applicationInfo = i.activityInfo.applicationInfo
            result.add(
                AppInfoData(
                    appPackageName = applicationInfo.packageName,
                    appLabelName = applicationInfo.loadLabel(packageManager).toString(),
                    appIco = applicationInfo.icon
                )
            )
        }
        return result
    }
}