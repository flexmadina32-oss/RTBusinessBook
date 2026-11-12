package com.riotech.rtbusinessbook.util

import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object Fmt {
    fun money(minor: Long): String {
        val a = abs(minor)
        val whole = NumberFormat.getIntegerInstance(Locale.US).format(a / 100)
        val frac = (a % 100).toString().padStart(2, '0')
        return (if (minor < 0) "-" else "") + "Rs $whole.$frac"
    }

    fun date(ms: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date(ms))

    fun parseDate(s: String): Long? = try {
        val f = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        f.isLenient = false
        f.parse(s.trim())?.time
    } catch (e: Exception) { null }

    /** "1,250.50" -> 125050 paisa. Zero ya ghalat input par null. */
    fun parseAmount(s: String): Long? {
        val d = s.trim().replace(",", "").toBigDecimalOrNull() ?: return null
        if (d.signum() <= 0) return null
        return d.setScale(2, RoundingMode.HALF_UP).movePointRight(2).toLong()
    }

    fun minorToText(m: Long): String = "${m / 100}.${(m % 100).toString().padStart(2, '0')}"

    fun startOfToday(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    fun startOfMonth(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }
}
