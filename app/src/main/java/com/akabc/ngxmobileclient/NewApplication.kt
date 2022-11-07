package com.akabc.ngxmobileclient

import android.app.Application
import com.akabc.ngxmobileclient.net.Repository

class NewApplication: Application() {
    val repository by lazy { Repository() }
}