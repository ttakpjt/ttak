package com.ttak.android.utils

fun formatDuration(durationInMinutes: Int): String {
    val hours = durationInMinutes / 60
    val minutes = durationInMinutes % 60
    return if (hours > 0) {
        "${hours}시간 ${minutes}분"
    } else {
        "${minutes}분"
    }
}
