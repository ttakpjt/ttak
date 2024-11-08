package com.ttak.android.domain.model

data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T
)