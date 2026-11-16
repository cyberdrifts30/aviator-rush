package com.Qempibo9.Pebfik.core.util

import java.util.Locale

fun groupNumber(value: Int): String {
    val raw = value.toString()
    if (raw.length <= 3) return raw
    val builder = StringBuilder()
    var count = 0
    for (index in raw.lastIndex downTo 0) {
        builder.append(raw[index])
        count++
        if (count % 3 == 0 && index > 0) builder.append(',')
    }
    return builder.reverse().toString()
}

fun decimal2(value: Float): String = String.format(Locale.US, "%.2f", value)

fun hundredthsToDecimal(value: Int): String = decimal2(value / 100f)
