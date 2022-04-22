package com.gswxxn.restoresplashscreen.ui

import com.gswxxn.restoresplashscreen.databinding.ActivityMainBinding

class AboutPageActivity : BaseActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}