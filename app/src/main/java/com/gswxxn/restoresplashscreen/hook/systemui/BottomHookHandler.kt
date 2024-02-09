package com.gswxxn.restoresplashscreen.hook.systemui

import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker
import com.gswxxn.restoresplashscreen.hook.base.BaseHookHandler
import com.gswxxn.restoresplashscreen.utils.YukiHelper.printLog
import com.gswxxn.restoresplashscreen.wrapper.SplashScreenViewBuilderWrapper

/**
 * 此对象用于处理底部图片 Hook
 */
object BottomHookHandler: BaseHookHandler() {

    /** 开始 Hook */
    override fun onHook() {
        /**
         * 移除底部图片
         */
        NewSystemUIHooker.Members.build_SplashScreenViewBuilder.addBeforeHook {
            val isRemoveBrandingImage = prefs.get(DataConst.REMOVE_BRANDING_IMAGE) &&
                    if (prefs.get(DataConst.IS_REMOVE_BRANDING_IMAGE_EXCEPTION_MODE))
                        GenerateHookHandler.currentPackageName !in prefs.get(DataConst.REMOVE_BRANDING_IMAGE_LIST)
                    else
                        GenerateHookHandler.currentPackageName in prefs.get(DataConst.REMOVE_BRANDING_IMAGE_LIST)

            if (isRemoveBrandingImage)
                SplashScreenViewBuilderWrapper.getInstance(instance).setBrandingDrawable(null, 0, 0)
            printLog("SplashScreenViewBuilder(): ${if (isRemoveBrandingImage) "" else "Not"} remove Branding Image")
        }
    }
}