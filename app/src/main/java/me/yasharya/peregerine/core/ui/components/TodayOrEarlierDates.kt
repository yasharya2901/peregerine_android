package me.yasharya.peregerine.core.ui.components

import androidx.compose.material3.SelectableDates
import java.util.Calendar
import java.util.TimeZone

object TodayOrEarlierDates: SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val utc = TimeZone.getTimeZone("UTC")

        val selectedCal = Calendar.getInstance(utc)
        selectedCal.timeInMillis = utcTimeMillis

        val todayCal = Calendar.getInstance(utc)

        return when {
            selectedCal.get(Calendar.YEAR) < todayCal.get(Calendar.YEAR) -> true
            selectedCal.get(Calendar.YEAR) > todayCal.get(Calendar.YEAR) -> false
            else -> selectedCal.get(Calendar.DAY_OF_YEAR) <= todayCal.get(Calendar.DAY_OF_YEAR)
        }
    }
}