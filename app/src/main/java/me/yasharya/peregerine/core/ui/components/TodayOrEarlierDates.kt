package me.yasharya.peregerine.core.ui.components

import androidx.compose.material3.SelectableDates
import java.util.Calendar
import java.util.TimeZone

object TodayOrEarlierDates: SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val selectedCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        selectedCal.timeInMillis = utcTimeMillis

        val todayCal = Calendar.getInstance()

        return when {
            selectedCal.get(Calendar.YEAR) < todayCal.get(Calendar.YEAR) -> true
            selectedCal.get(Calendar.YEAR) > todayCal.get(Calendar.YEAR) -> false
            else -> selectedCal.get(Calendar.DAY_OF_YEAR) <= todayCal.get(Calendar.DAY_OF_YEAR)
        }
    }
}