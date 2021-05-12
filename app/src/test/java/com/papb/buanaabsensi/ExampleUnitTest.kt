package com.papb.buanaabsensi

import android.text.format.DateUtils
import com.google.firebase.Timestamp
import org.junit.Test

import org.junit.Assert.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val today = Timestamp.now().toDate()
        val calendar = Calendar.getInstance()
        calendar.time = today
        var result = DateUtils.isToday(Timestamp.now().toDate().time)
        assertEquals(4, result)
    }
}