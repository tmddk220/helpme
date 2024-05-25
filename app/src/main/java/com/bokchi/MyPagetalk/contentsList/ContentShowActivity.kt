package com.bokchi.MyPagetalk.contentsList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.bokchi.MyPagetalk.R

class ContentShowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_show)

        val getUrl = intent.getStringExtra("url")

        val webView : WebView = findViewById(R.id.webView)
        webView.loadUrl(getUrl.toString())

    }
}