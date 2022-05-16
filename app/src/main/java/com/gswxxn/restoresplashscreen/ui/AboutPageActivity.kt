package com.gswxxn.restoresplashscreen.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.databinding.ActivityAboutPageBinding
import com.gswxxn.restoresplashscreen.utils.Utils.roundBitmapByShader
import com.gswxxn.restoresplashscreen.utils.Utils.toast

class AboutPageActivity : BaseActivity() {
    private lateinit var binding : ActivityAboutPageBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        window.insetsController?.setSystemBarsAppearance(
            APPEARANCE_LIGHT_STATUS_BARS,
            APPEARANCE_LIGHT_STATUS_BARS
        )

        binding = ActivityAboutPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            titleBackIcon.setOnClickListener { onBackPressed() }

            appIcon.setImageBitmap(roundBitmapByShader(
                BitmapFactory.decodeResource(resources, R.mipmap.icon), false))

            miluIcon.setImageBitmap(roundBitmapByShader(
                BitmapFactory.decodeResource(resources, R.mipmap.img_developer), true))

            version.text = "v${BuildConfig.VERSION_NAME}"

            developerMilu.setOnClickListener {
                toast("点个关注，不迷路")
                try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("coolmarket://u/1189245"))) }
                catch (e: Exception){startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolapk.com/u/1189245")))}
            }

            githubRepo.setOnClickListener {
                toast("点个小星星呗~")
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/GSWXXN/RestoreSplashScreen")))
            }

            iconfont.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.iconfont.cn")))
            }

            MIUINativeNotifyIcon.setOnClickListener {
                toast("感谢fankes")
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/MIUINativeNotifyIcon")))
            }

            YukiHookAPI.setOnClickListener {
                toast("感谢fankes")
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/YukiHookAPI")))
            }

            HideMyApplist.setOnClickListener {
                toast("感谢Dr-TSNG")
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Dr-TSNG/Hide-My-Applist")))
            }

            libsu.setOnClickListener {
                toast("感谢topjohnwu")
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/topjohnwu/libsu")))
            }

        }
    }
}