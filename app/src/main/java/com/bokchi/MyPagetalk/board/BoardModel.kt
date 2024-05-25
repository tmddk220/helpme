package com.bokchi.MyPagetalk.board

data class BoardModel (
    val title : String = "",
    val content : String = "",
    val uid : String = "",
    val time : String = "",
    val reviewtitle : String = "",
    val author: String = "",
    val rating: Float = 0.0f
)