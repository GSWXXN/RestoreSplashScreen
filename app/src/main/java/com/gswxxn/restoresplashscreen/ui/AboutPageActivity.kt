package com.gswxxn.restoresplashscreen.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cn.fkj233.ui.activity.dp2px
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.RoundDegree
import com.gswxxn.restoresplashscreen.databinding.ActivityAboutPageBinding
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.gswxxn.restoresplashscreen.utils.GraphicUtils.drawable2Bitmap
import com.gswxxn.restoresplashscreen.utils.GraphicUtils.roundBitmapByShader
import com.highcapable.yukihookapi.hook.factory.prefs

/**
 * 关于页面
 */
class AboutPageActivity : BaseActivity<ActivityAboutPageBinding>() {

    override fun onCreate() {
        window.statusBarColor = getColor(R.color.colorThemeBackground)
        binding.apply {

            titleBackIcon.setOnClickListener { finishAfterTransition() }

            appIcon.setImageBitmap(roundBitmapByShader(
                getDrawable(R.mipmap.ic_launcher)?.let {
                    drawable2Bitmap(
                        it,
                        it.intrinsicHeight * 2
                    )
                }, RoundDegree.RoundCorner))

            var count = 0
            var lastClickTime: Long = 0
            appIcon.setOnClickListener {
                val now = System.currentTimeMillis()

                if (now - lastClickTime < 500) count++
                else count = 1

                lastClickTime = now

                if (count != 5) return@setOnClickListener
                count = 0

                if (!prefs().get(DataConst.ENABLE_DEV_SETTINGS) ) {
                    prefs().edit { put(DataConst.ENABLE_DEV_SETTINGS, true) }
                    Toast.makeText(
                        this@AboutPageActivity,
                        getString(R.string.enable_dev_settings),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@AboutPageActivity,
                        getString(R.string.enable_dev_settings),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            miluIcon.setImageBitmap(roundBitmapByShader(
                getDrawable(R.mipmap.img_developer)?.let {
                    drawable2Bitmap(
                        it,
                        it.intrinsicHeight
                    )
                }, RoundDegree.Circle
            )
            )

            version.text = getString(R.string.version, BuildConfig.VERSION_NAME)

            developerMilu.setOnClickListener {
                toast(getString(R.string.follow_me))
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("coolmarket://u/1189245")))
                } catch (e: Exception) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.coolapk.com/u/1189245")
                        )
                    )
                }
            }

            githubRepo.setOnClickListener {
                toast(getString(R.string.star_project))
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/GSWXXN/RestoreSplashScreen")
                    )
                )
            }

            iconfont.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.iconfont.cn")))
            }

            licenseLayout.apply {
                addLicenseV(
                    "MIUINativeNotifyIcon",
                    "fankes",
                    "https://github.com/fankes/MIUINativeNotifyIcon",
                    "GNU Affero General Public License v3.0"
                )
                addLicenseV(
                    "Hide-My-Applist",
                    "Dr-TSNG",
                    "https://github.com/Dr-TSNG/Hide-My-Applist",
                    "GNU Affero General Public License v3.0"
                )
                addLicenseV(
                    "YukiHookAPI",
                    "fankes",
                    "https://github.com/fankes/YukiHookAPI",
                    "MIT License"
                )
                addLicenseV(
                    "MiuiHomeR",
                    "YuKongA",
                    "https://github.com/qqlittleice/MiuiHome_R",
                    "GNU General Public License v3.0"
                )
                addLicenseV(
                    "BlockMIUI",
                    "577fkj",
                    "https://github.com/Block-Network/blockmiui",
                    "GNU Lesser General Public License v2.1"
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun LinearLayout.addLicenseV(
        projectName: String,
        author: String,
        url: String,
        licenseName: String
    ) {
        addView(LinearLayout(this@AboutPageActivity).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(
                    0,
                    dp2px(this@AboutPageActivity, 10F),
                    0,
                    dp2px(this@AboutPageActivity, 15F),
                )
                orientation = LinearLayout.VERTICAL
            }
            addView(
                TextView(this@AboutPageActivity).apply {
                    text = "$projectName - $author"
                    textSize = 15F
                    setTextColor(getColor(R.color.colorTextGray))
                }
            )
            addView(
                TextView(this@AboutPageActivity).apply {
                    text = "$url\n$licenseName"
                    textSize = 12F
                    setTextColor(getColor(R.color.colorTextDark))
                    setLineSpacing(dp2px(this@AboutPageActivity, 3F).toFloat(), 1F)
                }
            )
            setOnClickListener {
                toast(getString(R.string.thanks_to, author))
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
        )
    }
}