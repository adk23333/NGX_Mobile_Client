package com.akabc.ngxmobileclient

import android.app.Application
import com.akabc.ngxmobileclient.net.Repository
import com.akabc.ngxmobileclient.net.SingletonVolley

class NewApplication: Application() {
    val repository by lazy { Repository(SingletonVolley.getInstance(applicationContext)) }
}