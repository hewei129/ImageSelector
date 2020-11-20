package com.hw.selpic_lib.util

import android.content.Context
import android.util.Log
import java.sql.Timestamp
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author hewei(David)
 * @date 2020/9/9  5:39 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

val ENG_DATE_FROMAT: String? = "EEE, d MMM yyyy HH:mm:ss z"
const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
const val YYYY_MM_DD = "yyyy-MM-dd"
const val YYYYMMDD = "yyyyMMdd"
const val MM_DD_HH_MM = "MM-dd HH:mm"
const val HH_MM = "HH:mm" //时分

const val YYYY = "yyyy"
const val MM = "MM"
const val DD = "dd"

fun generateTime(time: Long): String? {
    val totalSeconds = (time / 1000).toInt()
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) String.format(
        "%02d:%02d:%02d",
        hours,
        minutes,
        seconds
    ) else String.format("%02d:%02d", minutes, seconds)
}

/**
 * 根据long毫秒数，获得时分秒
 */
fun getDateFormatByLong(time: Long): String? {
    val totalSeconds = (time / 1000).toInt()
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

/**
 * @param
 * @return
 * @作者
 * @创建日期
 * @创建时间
 * @描述 —— 格式化日期对象
 */
fun date2date(date: Date?): Date? {
    var date = date
    val sdf = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
    val str = sdf.format(date)
    date = try {
        sdf.parse(str)
    } catch (e: Exception) {
        return null
    }
    return date
}

/**
 * @param
 * @return
 * @作者
 * @创建日期
 * @创建时间
 * @描述 —— 时间对象转换成字符串
 */
fun date2string(date: Date?): String? {
    var strDate = ""
    val sdf = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
    strDate = sdf.format(date)
    return strDate
}


/**
 * @param
 * @return
 * @作者
 * @创建日期
 * @创建时间
 * @描述 —— 时间对象转换成字符串
 */
fun date3string(date: Date?): String? {
    var strDate = ""
    val sdf = SimpleDateFormat("MM月dd日 HH:mm")
    strDate = sdf.format(date)
    return strDate
}

/**
 * @param
 * @return
 * @作者
 * @创建日期
 * @创建时间 时分
 * @描述 —— 时间对象转换成字符串
 */
fun date4string(date: Date?): String? {
    var strDate = ""
    val sdf = SimpleDateFormat(HH_MM)
    strDate = sdf.format(date)
    return strDate
}

/**
 * @param
 * @return
 * @作者
 * @创建日期
 * @创建时间 年月日
 * @描述 —— 时间对象转换成字符串
 */
fun date5string(date: Date?): String? {
    var strDate = ""
    val sdf = SimpleDateFormat(YYYY_MM_DD)
    strDate = sdf.format(date)
    return strDate
}


/**
 * @param
 * @return
 * @作者
 * @创建日期
 * @创建时间 年月日
 * @描述 —— 时间对象转换成字符串
 */
fun date6string(date: Date?): String? {
    var strDate = ""
    val sdf = SimpleDateFormat(YYYYMMDD)
    strDate = sdf.format(date)
    return strDate
}


/**
 * 通过时间获得文件名
 *
 * @param date
 * @return
 */
fun getFileNameByDate(date: Date?): String? {
    var strDate = ""
    val sdf = SimpleDateFormat("yyyyMMddHHmmss")
    strDate = sdf.format(date)
    return strDate
}

/**
 * @param
 * @return
 * @作者
 * @创建日期
 * @创建时间
 * @描述 —— sql时间对象转换成字符串
 */
fun timestamp2string(timestamp: Timestamp?): String? {
    var strDate = ""
    val sdf = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
    strDate = sdf.format(timestamp)
    return strDate
}

/**
 * @param dateString
 * @return
 * @作者 王建明
 * @创建日期 2012-7-13
 * @创建时间
 * @描述 —— 字符串转换成时间对象
 */
fun string2date(dateString: String?): Date? {
    var formateDate: Date? = null
    val format: DateFormat = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
    formateDate = try {
        format.parse(dateString)
    } catch (e: ParseException) {
        return null
    }
    return formateDate
}

/**
 * @param dateString
 * @return
 * @作者 zan
 * @创建日期 2017-8-4
 * @创建时间
 * @描述 —— 字符串转换成时间对象
 */
fun string3date(dateString: String?): Date? {
    var formateDate: Date? = null
    val format: DateFormat = SimpleDateFormat(YYYY_MM_DD)
    formateDate = try {
        format.parse(dateString)
    } catch (e: ParseException) {
        return null
    }
    return formateDate
}

/**
 * @param date
 * @return
 * @作者
 * @创建日期
 * @创建时间
 * @描述 —— Date类型转换为Timestamp类型
 */
fun date2timestamp(date: Date?): Timestamp? {
    return if (date == null) null else Timestamp(date.time)
}

/**
 * @return
 * @作者
 * @创建日期
 * @创建时间
 * @描述 —— 获得当前年份
 */
fun getNowYear(): String? {
    val sdf = SimpleDateFormat(YYYY)
    return sdf.format(Date())
}

/**
 * @return
 * @作者
 * @创建日期
 * @创建时间
 * @描述 —— 获得当前月份
 */
fun getNowMonth(): String? {
    val sdf = SimpleDateFormat(MM)
    return sdf.format(Date())
}

/**
 * @return
 * @作者
 * @创建日期
 * @创建时间
 * @描述 —— 获得当前日期中的日
 */
fun getNowDay(): String? {
    val sdf = SimpleDateFormat(DD)
    return sdf.format(Date())
}


/**
 * 格式化日期字符串
 *
 * @param currentTime
 * @return
 */
fun formatString(currentTime: String?): String? {
    val format: DateFormat = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
    return format.format(currentTime)
}

/**
 * 格式化时间
 *
 * @param date       时间date
 * @param formatType 格式化样式
 * @return
 */
fun formatString(date: Date?, formatType: String?): String? {
    val format = SimpleDateFormat(formatType)
    return format.format(date)
}

/**
 * 将时间戳转为Date类型
 *
 * @param millis 毫秒时间戳
 * @return Date类型时间
 */
fun millisToDate(millis: Long): Date? {
    return Date(millis)
}

/**
 * 获取当前 日期 格式化字符串
 * @param formatType
 * @return
 */
fun getFormatDateString(formatType: String?): String? {
    val simpleDateFormat = SimpleDateFormat(formatType)
    //获取当前时间
    val date = Date(System.currentTimeMillis())
    return simpleDateFormat.format(date)
}

/**
 * 返回当前程序版本名
 */
fun getAppVersionName(context: Context): String? {
    var versionName = ""
    try { // ---get the package info---
        val pm = context.packageManager
        val pi = pm.getPackageInfo(context.packageName, 0)
        versionName = pi.versionName
        //            versioncode = pi.versionCode;
        if (versionName == null || versionName.isEmpty()) {
            return ""
        }
    } catch (e: Exception) {
        Log.e("VersionInfo", "Exception", e)
    }
    return versionName
}

