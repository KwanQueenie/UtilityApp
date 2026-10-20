package au.edu.jcu.assessment.utilityapp.ui

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

// Australians read times in 12-hour format ("1 pm", "9:10 am").
private val hourFormatter = DateTimeFormatter.ofPattern("h a", Locale.ENGLISH)
private val clockFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
private val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)

fun LocalDateTime.hourLabel(): String = format(hourFormatter).lowercase(Locale.ENGLISH)

fun LocalTime.clockLabel(): String = format(clockFormatter).lowercase(Locale.ENGLISH)

fun LocalDate.dayLabel(): String = format(dayFormatter)

/** 24.6 -> "25°" */
fun Double.degrees(): String = "${roundToInt()}°"