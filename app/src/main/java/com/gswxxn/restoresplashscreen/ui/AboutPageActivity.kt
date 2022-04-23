package com.gswxxn.restoresplashscreen.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.databinding.ActivityAboutPageBinding
import com.gswxxn.restoresplashscreen.hook.Utils
import com.gswxxn.restoresplashscreen.hook.Utils.circleBitmapByShader
import com.gswxxn.restoresplashscreen.hook.Utils.roundBitmapByShader

class AboutPageActivity : BaseActivity() {
    private lateinit var binding : ActivityAboutPageBinding

    override fun onCreate() {
        binding = ActivityAboutPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            appIcon.setImageBitmap(roundBitmapByShader(
                BitmapFactory.decodeResource(resources, R.mipmap.img_developer),
                this.appIcon.layoutParams.width,
                Utils.dp2px(this@AboutPageActivity, this.appIcon.layoutParams.width.toFloat()/10)
                ))

            miluIcon.setImageBitmap(circleBitmapByShader(
                BitmapFactory.decodeResource(resources, R.mipmap.img_developer),
                this.miluIcon.layoutParams.width,
                this.miluIcon.layoutParams.width
            ))

            version.text = "v${BuildConfig.VERSION_NAME}"

            developerMilu.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolapk.com/u/1189245")))
            }

            githubRepo.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/GSWXXN/RestoreSplashScreen")))
            }

            iconfont.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.iconfont.cn")))
            }

            MIUINativeNotifyIcon.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/MIUINativeNotifyIcon")))
            }

            YukiHookAPI.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/YukiHookAPI")))
            }

            HideMyApplist.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Dr-TSNG/Hide-My-Applist")))
            }

            libsu.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/topjohnwu/libsu")))
            }

        }
    }
}