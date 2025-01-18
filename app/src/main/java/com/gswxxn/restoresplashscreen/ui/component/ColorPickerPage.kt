package com.gswxxn.restoresplashscreen.ui.component

import android.content.Context
import androidx.annotation.ColorInt
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toMap
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toSet
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.gswxxn.restoresplashscreen.utils.GraphicUtils.getBgColor
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.AlertDialogMode
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.HazeScaffold
import dev.lackluster.hyperx.compose.icon.Back
import dev.lackluster.hyperx.compose.preference.EditTextDialog
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SliderColors
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextButtonColors
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SpinnerEntry
import top.yukonga.miuix.kmp.extra.SpinnerMode
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperSpinner
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.HorizontalDivider
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize
import kotlin.math.pow
import kotlin.math.round

/**
 * 手动选择背景颜色界面
 */
@Composable
fun ColorPickerPage(
    navController: NavController,
    adjustPadding: PaddingValues,
    pkgName: String?,
    mode: BasePageDefaults.Mode
) {
    // 模块 App 透明度配置
    val blurEnabled = MainActivity.blurEnabled
    val blurTintAlphaLight = MainActivity.blurTintAlphaLight
    val blurTintAlphaDark = MainActivity.blurTintAlphaDark

    // 顶部栏模糊状态
    val topAppBarBackground = MiuixTheme.colorScheme.background
    val listState = rememberLazyListState()
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val topBarBlurState by remember {
        derivedStateOf {
            blurEnabled.value &&
                    scrollBehavior.state.collapsedFraction >= 1.0f &&
                    (listState.isScrollInProgress || listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 12)
        }
    }

    // dialog 的显示状态
    val modifiedDialogVisibility = remember { mutableStateOf(false) }
    val dropdownDialogVisibility = remember { mutableStateOf(false) }

    // 已选颜色的状态, app 已保存的信息
    val appColorConfig = AppColorConfig(pkgName, LocalContext.current)
    val currentDarkMode = isSystemInDarkTheme().let { remember { mutableStateOf(it) } }
    val defaultColor = appColorConfig.getDefaultBGColor(currentDarkMode.value)
    val pickedColor = PickedColor(
        rgbColorState = remember { mutableIntStateOf(defaultColor) },
        hsvColorState = remember { defaultColor.toHSVColorList().toMutableStateList() }
    )

    // 显示内容脚手架
    HazeScaffold(
        modifier = Modifier.fillMaxSize(),
        blurTopBar = blurEnabled.value,
        blurBottomBar = blurEnabled.value,
        hazeStyle = HazeStyle(
            blurRadius = 66.dp,
            backgroundColor = topAppBarBackground,
            tint = HazeTint(topAppBarBackground.copy(alpha =
                if (topAppBarBackground.luminance() >= 0.5f)
                    blurTintAlphaLight.floatValue
                else
                    blurTintAlphaDark.floatValue),
            )
        ),
        adjustPadding = adjustPadding,
        topBar = { TopBar(
            paddingValues = it,
            mode = mode,
            appName = appColorConfig.appName,
            topAppBarBackground = topAppBarBackground,
            topBarBlurState = topBarBlurState,
            scrollBehavior = scrollBehavior,
            onBack = { onBack(
                navController = navController,
                appColorConfig = appColorConfig,
                currentDarkMode = currentDarkMode,
                pickedColor = pickedColor,
                modifiedDialogVisibility = modifiedDialogVisibility
            ) }
        ) },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .height(getWindowSize().height.dp)
                    .background(MiuixTheme.colorScheme.background),
                state = listState,
                contentPadding = paddingValues,
                topAppBarScrollBehavior = scrollBehavior,
                content = { item { MainContent(
                    appColorConfig,
                    pickedColor,
                    currentDarkMode,
                    dropdownDialogVisibility
                ) } }
            )
        },
        bottomBar = { BottomBar(
            contentPadding = it,
            appColorConfig = appColorConfig,
            pickedColor = pickedColor,
            blurEnabled = blurEnabled,
            currentDarkMode = currentDarkMode
        ) }
    )

    // 注册界面上的 Dialog
    AlertDialogs(
        navController = navController,
        modifiedDialogVisibility = modifiedDialogVisibility,
        dropdownDialogVisibility = dropdownDialogVisibility,
        pickedColor = pickedColor,
        appColorConfig = appColorConfig,
        currentDarkMode = currentDarkMode
    )
}

/**
 * 界面顶部内容 (返回键及标题)
 */
@Composable
private fun TopBar(
    paddingValues: PaddingValues,
    mode: BasePageDefaults.Mode,
    appName: String,
    topAppBarBackground: Color,
    onBack: () -> Unit,
    scrollBehavior: ScrollBehavior,
    topBarBlurState: Boolean,
) {
    val layoutDirection = LocalLayoutDirection.current
    val systemBarInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(
        WindowInsetsSides.Horizontal).asPaddingValues()
    val navigationIconPadding = PaddingValues.Absolute(
        left = if (mode != BasePageDefaults.Mode.SPLIT_RIGHT)
            systemBarInsets.calculateLeftPadding(layoutDirection)
        else
            0.dp
    )
    TopAppBar(
        color = topAppBarBackground.copy(if (topBarBlurState) 0f else 1f),
        title = appName,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                modifier = Modifier
                    .padding(navigationIconPadding)
                    .padding(start = 21.dp)
                    .size(40.dp),
                onClick = onBack
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = MiuixIcons.Back,
                    contentDescription = "Back",
                    tint = MiuixTheme.colorScheme.onSurfaceSecondary
                )
            }
        },
        defaultWindowInsetsPadding = false,
        horizontalPadding = 28.dp + paddingValues.calculateLeftPadding(LocalLayoutDirection.current)
    )
}

/**
 * 界面主要内容
 */
@Composable
private fun MainContent(
    appColorConfig: AppColorConfig,
    pickedColor: PickedColor,
    darkMode: MutableState<Boolean>,
    dropdownDialogVisibility: MutableState<Boolean>,
) {
    PreferenceGroup {
        DisplayCard(appColorConfig, pickedColor)
    }
    PreferenceGroup {
        InputColor(
            pickedColor = pickedColor,
            appColorConfig = appColorConfig,
            darkMode = darkMode,
            dropdownDialogVisibility = dropdownDialogVisibility
        )
    }
    PreferenceGroup(title = stringResource(R.string.rgb_color_space)) {
        RGBPreference(pickedColor)
    }
    PreferenceGroup(title = stringResource(R.string.hsv_color_space)) {
        HSVPreference(pickedColor)
    }
    PreferenceGroup(first = true, last = true) {
        ResetText(
            appColorConfig = appColorConfig,
            pickedColor = pickedColor,
            currentDarkMode = darkMode
        )
    }
}

/**
 * 预览卡片
 */
@Composable
private fun DisplayCard(
    appColorConfig: AppColorConfig,
    pickedColor: PickedColor,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 6.dp, top = 12.dp),
        color = colorResource(R.color.colorDemoBackground)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DisplayColorDemo(
                appColorConfig = appColorConfig,
                pickedColor = pickedColor
            )
            DisplayColorSelection(
                appColorConfig = appColorConfig,
                pickedColor = pickedColor
            )
        }
    }
}

/**
 * 展示预览颜色
 */
@Composable
private fun DisplayColorDemo(
    pickedColor: PickedColor,
    appColorConfig: AppColorConfig
) {
    Box(modifier = Modifier.padding(16.dp).wrapContentSize()) {
        var magnifierCenter by remember { mutableStateOf(Offset.Unspecified) }
        val appIcon = appColorConfig.appIcon
        var collimationVisibility by remember { mutableStateOf(false) }
        Image(
            painter = painterResource(id = R.drawable.demo_transparency),
            contentDescription = null,
            modifier = Modifier
                .drawBehind { drawRect(Color(pickedColor.colorInt)) }
                .height(250.dp)
        )

        Image(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Center)
                .magnifier(
                    sourceCenter = { magnifierCenter },
                    magnifierCenter = { magnifierCenter - Offset(0f, 100.dp.toPx()) },
                    zoom = 5f,
                    size = DpSize(100.dp, 100.dp),
                    cornerRadius = 50.dp
                ).pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            collimationVisibility = true
                            magnifierCenter = offset
                            appIcon.getColor(
                                (appIcon.width * magnifierCenter.x / size.width).toInt().coerceIn(0, appIcon.width - 1),
                                (appIcon.height * magnifierCenter.y / size.height).toInt().coerceIn(0, appIcon.height - 1)
                            ).toArgb().let { pickedColor.colorInt = it }
                        },
                        onDrag = { _, delta ->
                            collimationVisibility = true
                            magnifierCenter += delta
                            appIcon.getColor(
                                (appIcon.width * magnifierCenter.x / size.width).toInt().coerceIn(0, appIcon.width - 1),
                                (appIcon.height * magnifierCenter.y / size.height).toInt().coerceIn(0, appIcon.height - 1)
                            ).toArgb().let { pickedColor.colorInt = it }
                        },
                        onDragEnd = {
                            collimationVisibility = false
                            magnifierCenter = Offset.Unspecified
                        },
                        onDragCancel = {
                            collimationVisibility = false
                            magnifierCenter = Offset.Unspecified
                        }
                    )
                },
            bitmap =  appIcon.asImageBitmap(),
            contentDescription = null,
        )
    }
}

/**
 * 列出自动取出的颜色
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DisplayColorSelection(
    pickedColor: PickedColor,
    appColorConfig: AppColorConfig
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.please_select),
            fontSize = MiuixTheme.textStyles.body2.fontSize,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            maxItemsInEachRow = 4
        ) {
            val paletteColors = remember { mutableStateListOf<Int>() }
            LaunchedEffect(Unit) {
                val colors = with(
                    Palette.from(appColorConfig.appIcon).maximumColorCount(8).generate()
                ) {
                    listOf(
                        getDominantColor(0),
                        getLightVibrantColor(0),
                        getVibrantColor(0),
                        getDarkVibrantColor(0),
                        getLightMutedColor(0),
                        getMutedColor(0),
                        getDarkMutedColor(0)
                    ).distinct().filter { it != 0 }
                }
                paletteColors.clear()
                paletteColors.addAll(colors)
            }

            paletteColors.forEach { color ->
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp),
                    border = BorderStroke(2.dp, MiuixTheme.colorScheme.dividerLine),
                    shape = CircleShape,
                    color = Color(color),
                    content = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { pickedColor.colorInt = color }
                        )
                    }
                )
            }
        }
    }
}

/**
 * 颜色生效模式 及 手动输入颜色
 */
@Composable
private fun InputColor(
    pickedColor: PickedColor,
    appColorConfig: AppColorConfig,
    darkMode: MutableState<Boolean>,
    dropdownDialogVisibility: MutableState<Boolean>
) {
    // 颜色生效模式
    SuperSpinner(
        title = stringResource(R.string.target_color_mode),
        summary = stringResource(R.string.target_color_mode_tips),
        items = listOf(
            SpinnerEntry(title = stringResource(R.string.target_color_mode_light)),
            SpinnerEntry(title = stringResource(R.string.target_color_mode_dark))
        ),
        selectedIndex = if (darkMode.value) 1 else 0,
        mode = SpinnerMode.AlwaysOnRight,
        showValue = true
    ) {
        if (appColorConfig.getDefaultBGColor(darkMode.value) != pickedColor.colorInt) {
            dropdownDialogVisibility.value = true
        } else {
            darkMode.value = !darkMode.value
            pickedColor.colorInt = appColorConfig.getDefaultBGColor(darkMode.value)
        }
    }

    // 手动输入颜色
    val dialogVisibility = remember { mutableStateOf(false) }
    val context = LocalContext.current
    SuperArrow(
        title = stringResource(R.string.manual_input),
        rightText = "#" + "%08X".format(pickedColor.colorInt).substring(2),
        insideMargin = PaddingValues(16.dp),
        onClick = { dialogVisibility.value = true }
    )
    EditTextDialog(
        visibility = dialogVisibility,
        title = stringResource(R.string.manual_input),
        message = stringResource(R.string.manual_input_hint),
        value = "%08X".format(pickedColor.colorInt).substring(2),
        onInputConfirm = { newString ->
            val trimmedString = newString.replace("#", "")
            try {
                pickedColor.colorInt = android.graphics.Color.parseColor("#$trimmedString")
            } catch (_: IllegalArgumentException) {
                context.toast(R.string.color_input_invalid)
            }
        }
    )
}

/**
 * RGB 空间色彩设置项
 */
@Composable
private fun RGBPreference(pickedColor: PickedColor) {
    IntColorSeekBar(
        title = stringResource(R.string.rgb_r),
        value = pickedColor.r,
        colors = SliderColors(
            foregroundColor = Color(0xFFF36060),
            disabledForegroundColor = Color(0x7FF36060),
            backgroundColor = MiuixTheme.colorScheme.tertiaryContainerVariant
        ),
        onValueChange = { pickedColor.r = it }
    )
    IntColorSeekBar(
        title = stringResource(R.string.rgb_g),
        value = pickedColor.g,
        colors = SliderColors(
            foregroundColor = Color(0xFF5FF25F),
            disabledForegroundColor = Color(0x7F5FF25F),
            backgroundColor = MiuixTheme.colorScheme.tertiaryContainerVariant
        ),
        onValueChange = { pickedColor.g = it }
    )
    IntColorSeekBar(
        title = stringResource(R.string.rgb_b),
        value = pickedColor.b,
        colors = SliderColors(
            foregroundColor = Color(0xFF5F5FF3),
            disabledForegroundColor = Color(0x7F5F5FF3),
            backgroundColor = MiuixTheme.colorScheme.tertiaryContainerVariant
        ),
        onValueChange = { pickedColor.b = it }
    )
}

/**
 * HSV 空间色彩设置项
 */
@Composable
private fun HSVPreference(
    pickedColor: PickedColor
) {
    HueSeekBar(
        title = stringResource(R.string.hue),
        value = pickedColor.h,
        onValueChange = { pickedColor.h = it }
    )
    FloatColorSeekBar(
        title = stringResource(R.string.saturation),
        value = pickedColor.s,
        onValueChange = { pickedColor.s = it }
    )
    FloatColorSeekBar(
        title = stringResource(R.string.value),
        value = pickedColor.v,
        onValueChange = { pickedColor.v = it }
    )
}

/**
 * 恢复默认 设置项
 */
@Composable
private fun ResetText(
    appColorConfig: AppColorConfig,
    pickedColor: PickedColor,
    currentDarkMode: MutableState<Boolean>
) {
    val context = LocalContext.current
    val prefs = context.prefs()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (appColorConfig.isConfiguringOverallBGColor) {
                    val targetKey = if (currentDarkMode.value) {
                        DataConst.OVERALL_BG_COLOR_NIGHT
                    } else {
                        DataConst.OVERALL_BG_COLOR
                    }
                    prefs.edit { remove(targetKey) }
                    appColorConfig.defaultColorLight = Color.White.toArgb()
                    appColorConfig.defaultColorDark = Color.Black.toArgb()
                } else {
                    val targetKey = if (currentDarkMode.value) {
                        DataConst.INDIVIDUAL_BG_COLOR_APP_MAP_DARK
                    } else {
                        DataConst.INDIVIDUAL_BG_COLOR_APP_MAP
                    }

                    val tmpConfigMap = prefs.get(targetKey).toMap()
                    tmpConfigMap.remove(appColorConfig.packageName)

                    prefs.edit { put(targetKey, tmpConfigMap.toSet()) }
                    appColorConfig.defaultColorLight = getBgColor(appColorConfig.appIcon, true)
                    appColorConfig.defaultColorDark = getBgColor(appColorConfig.appIcon, false)
                }
                pickedColor.colorInt = appColorConfig.getDefaultBGColor(currentDarkMode.value)
                context.toast(R.string.save_successful)
            }
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(R.string.reset),
            fontSize = MiuixTheme.textStyles.headline1.fontSize,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.colorTextRed)
        )
    }
}

/**
 * 界面底部按钮
 */
@Composable
private fun BottomBar(
    contentPadding: PaddingValues,
    appColorConfig: AppColorConfig,
    pickedColor: PickedColor,
    blurEnabled: MutableState<Boolean>,
    currentDarkMode: MutableState<Boolean>
) {
    val captionBarBottomPadding by rememberUpdatedState(
        WindowInsets.captionBar.only(WindowInsetsSides.Bottom).asPaddingValues().calculateBottomPadding()
    )
    val buttonPaddingValues = with(LocalLayoutDirection.current) {
        PaddingValues(
            start = contentPadding.calculateStartPadding(this) + 16.dp,
            top = 12.dp,
            end = contentPadding.calculateEndPadding(this) + 16.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + captionBarBottomPadding + 12.dp
        )
    }
    Surface(color = MiuixTheme.colorScheme.background.copy(if (blurEnabled.value) 0f else 1f)) {
        Column(modifier = Modifier.fillMaxWidth().background(Color.Transparent)) {
            HorizontalDivider(
                thickness = 0.75.dp,
                color = MiuixTheme.colorScheme.dividerLine
            )
            Row(
                modifier = Modifier
                    .padding(buttonPaddingValues)
                    .fillMaxWidth()
            ) {
                val context = LocalContext.current
                val prefs = context.prefs()
                TextButton(
                    modifier = Modifier.weight(1.0f),
                    text = stringResource(R.string.undo_modification),
                    minHeight = 50.dp,
                    onClick = { pickedColor.colorInt = appColorConfig.getDefaultBGColor(currentDarkMode.value) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextButton(
                    modifier = Modifier.weight(1.0f),
                    text = stringResource(R.string.save),
                    colors = Color(pickedColor.colorInt).let {
                        TextButtonColors(
                            color = it,
                            disabledColor = it,
                            textColor = if (it.luminance() > 0.5f) Color.Black else Color.White,
                            disabledTextColor = it
                        )
                    },
                    minHeight = 50.dp,
                    onClick = {
                        val colorHexString = "#" + "%08X".format(pickedColor.colorInt).substring(2)

                        if (appColorConfig.isConfiguringOverallBGColor) {
                            val targetKey = if (currentDarkMode.value)
                                DataConst.OVERALL_BG_COLOR_NIGHT
                            else
                                DataConst.OVERALL_BG_COLOR
                            prefs.edit { put(targetKey, colorHexString) }
                        } else {
                            val targetKey = if (currentDarkMode.value)
                                DataConst.INDIVIDUAL_BG_COLOR_APP_MAP_DARK
                            else
                                DataConst.INDIVIDUAL_BG_COLOR_APP_MAP
                            val tmpConfigMap = prefs.get(targetKey).toMap()
                            tmpConfigMap[appColorConfig.packageName] = colorHexString
                            prefs.edit { put(targetKey, tmpConfigMap.toSet()) }
                        }

                        appColorConfig.defaultColorLight = if (appColorConfig.isConfiguringOverallBGColor) {
                            val value = prefs.get(DataConst.OVERALL_BG_COLOR)
                            if (value.isBlank())
                                Color.White.toArgb()
                            else
                                android.graphics.Color.parseColor(value)
                        } else {
                            val value = prefs.get(DataConst.INDIVIDUAL_BG_COLOR_APP_MAP).toMap()[appColorConfig.packageName]
                            if (value.isNullOrBlank())
                                getBgColor(appColorConfig.appIcon, true)
                            else
                                android.graphics.Color.parseColor(value)
                        }
                        appColorConfig.defaultColorDark = if (appColorConfig.isConfiguringOverallBGColor) {
                            val value = prefs.get(DataConst.OVERALL_BG_COLOR_NIGHT)
                            if (value.isBlank())
                                Color.Black.toArgb()
                            else
                                android.graphics.Color.parseColor(value)
                        } else {
                            val value = prefs.get(DataConst.INDIVIDUAL_BG_COLOR_APP_MAP_DARK).toMap()[appColorConfig.packageName]
                            if (value.isNullOrBlank())
                                getBgColor(appColorConfig.appIcon, false)
                            else
                                android.graphics.Color.parseColor(value)
                        }
                        context.toast(R.string.save_successful)
                    }
                )
            }
        }
    }
}

/**
 * 界面中会出现的提示框
 */
@Composable
private fun AlertDialogs(
    navController: NavController,
    modifiedDialogVisibility: MutableState<Boolean>,
    dropdownDialogVisibility: MutableState<Boolean>,
    pickedColor: PickedColor,
    appColorConfig: AppColorConfig,
    currentDarkMode: MutableState<Boolean>
) {
    AlertDialog(
        visibility = modifiedDialogVisibility,
        title = stringResource(R.string.not_saved_title),
        message = stringResource(R.string.not_saved_hint),
        cancelable = false,
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_abandonment),
        positiveText = stringResource(R.string.button_reedit),
        onNegativeButton = {
            dismissDialog(modifiedDialogVisibility)
            navController.popBackStack()
        }
    )
    AlertDialog(
        visibility = dropdownDialogVisibility,
        title = stringResource(R.string.not_saved_title),
        message = stringResource(R.string.not_saved_hint),
        cancelable = false,
        mode = AlertDialogMode.NegativeAndPositive,
        negativeText = stringResource(R.string.button_abandonment),
        positiveText = stringResource(R.string.button_reedit),
        onNegativeButton = {
            dismissDialog(dropdownDialogVisibility)
            currentDarkMode.value = !currentDarkMode.value
            pickedColor.colorInt = appColorConfig.getDefaultBGColor(currentDarkMode.value)
        }
    )
}

/**
 * RGB 空间色彩下 SeekBar 组件
 */
@Composable
private fun IntColorSeekBar(
    title: String,
    value: Int,
    colors: SliderColors,
    onValueChange: ((Int) -> Unit)? = null
) {
    Column {
        BasicComponent(
            modifier = Modifier,
            insideMargin = PaddingValues(16.dp, 16.dp, 16.dp, 12.dp),
            title = title,
            rightActions = {
                Text(
                    text = "$value / 255",
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            }
        )
        Slider(
            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp),
            progress = value.toFloat(),
            minValue = 0f,
            maxValue = 255f,
            height = 28.dp,
            colors = colors,
            onProgressChange = { onValueChange?.invoke(it.toInt()) }
        )
    }
}

/**
 * HSV 空间色彩下色相的 SeekBar 组件
 */
@Composable
private fun HueSeekBar(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        BasicComponent(
            modifier = Modifier,
            insideMargin = PaddingValues(16.dp, 16.dp, 16.dp, 12.dp),
            title = title,
            rightActions = {
                Text(
                    text = "${value.let { it1 -> "%.2f".format(it1) }} / ${"%.2f".format(360.0f)}",
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            }
        )
        val hapticFeedback = LocalHapticFeedback.current
        var dragOffset by remember { mutableFloatStateOf(0f) }
        var isDragging by remember { mutableStateOf(false) }
        var currentValue by remember { mutableFloatStateOf(value) }
        var hapticTriggered by remember { mutableStateOf(false) }
        val updatedOnProgressChange by rememberUpdatedState(onValueChange)
        val calculateProgress = { offset: Float, width: Int, height: Int ->
            val newValue = ((offset - height / 2) / (width - height)).coerceIn(0.0f, 1.0f) * 360.0f
            (round(newValue * 10f.pow(2)) / 10f.pow(2)).coerceIn(0.0f, 360.0f)
        }

        Box(
            modifier = Modifier
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragOffset = offset.x
                            currentValue = calculateProgress(dragOffset, size.width, size.height)
                            updatedOnProgressChange(currentValue)
                            hapticTriggered = false
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragOffset = (dragOffset + dragAmount).coerceIn(0f, size.width.toFloat())
                            currentValue = calculateProgress(dragOffset, size.width, size.height)
                            updatedOnProgressChange(currentValue)
                            if ((currentValue == 0.0f || currentValue == 360.0f) && !hapticTriggered) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                hapticTriggered = true
                            } else if (currentValue != 0.0f && currentValue != 360.0f) {
                                hapticTriggered = false
                            }
                        },
                        onDragEnd = {
                            isDragging = false
                        }
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Canvas(
                modifier = Modifier.fillMaxWidth().height(28.dp)
                    .clip(SmoothRoundedCornerShape(28.dp))
                    .drawBehind {
                        val barHeight = size.height
                        val barWidth = size.width
                        val cornerRadius = CornerRadius.Zero
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.hsv(0.0f, 1.0f, 1.0f),
                                    Color.hsv(60.0f, 1.0f, 1.0f),
                                    Color.hsv(120.0f, 1.0f, 1.0f),
                                    Color.hsv(180.0f, 1.0f, 1.0f),
                                    Color.hsv(240.0f, 1.0f, 1.0f),
                                    Color.hsv(300.0f, 1.0f, 1.0f),
                                    Color.hsv(360.0f, 1.0f, 1.0f),
                                ),
                                startX = size.height,
                                endX = size.width - size.height,
                                tileMode = TileMode.Clamp
                            ),
                            size = Size(barWidth, barHeight),
                            topLeft = Offset(0f, center.y - barHeight / 2),
                            cornerRadius = cornerRadius
                        )
                    }
            ) {
                val barHeight = size.height
                val barWidth = size.width
                val circleX = barHeight / 2 + (value / 360.0f) * (barWidth - barHeight)
                drawCircle(
                    color = Color.White.copy(0.9f),
                    radius = size.height / 2 - 6.dp.toPx(),
                    center = Offset(circleX, barHeight / 2),
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }
    }
}

/**
 * HSV 空间色彩下饱和度, 明度的 SeekBar 组件
 */
@Composable
private fun FloatColorSeekBar(
    title: String,
    value: Float,
    onValueChange: ((Float) -> Unit)? = null
) {
    Column {
        BasicComponent(
            modifier = Modifier,
            insideMargin = PaddingValues(16.dp, 16.dp, 16.dp, 12.dp),
            title = title,
            rightActions = {
                Text(
                    text = "${"%.2f".format(value) } / 1.0",
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            }
        )
        Slider(
            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp),
            progress = value,
            minValue = 0.0f,
            maxValue = 1.0f,
            height = 28.dp,
            onProgressChange = { onValueChange?.invoke(it) }
        )
    }
}

/**
 * 界面返回行为
 */
private fun onBack(
    navController: NavController,
    appColorConfig: AppColorConfig,
    pickedColor: PickedColor,
    currentDarkMode: MutableState<Boolean>,
    modifiedDialogVisibility: MutableState<Boolean>
) {
    if (appColorConfig.getDefaultBGColor(currentDarkMode.value) != pickedColor.colorInt) {
        modifiedDialogVisibility.value = true
    } else {
        navController.popBackStack()
    }
}

/**
 * ColorInt 转换为 HSV 颜色数组
 */
private fun @receiver:ColorInt Int.toHSVColorList() =
    FloatArray(3).also { android.graphics.Color.colorToHSV(this, it) }.toList()

/**
 * 获取当前应用的基本信息
 * @property isConfiguringOverallBGColor 当前是否在配置整体默认背景颜色
 * @property packageName 当前配置 app 的包名, 如果配置整体默认背景颜色, 包名则为模块app的包名
 * @property appName 当前配置 app 的应用名, 如果配置整体默认背景颜色, 则为 '自定义背景颜色', 该项应配置为界面标题名
 * @property appIcon 当前配置 app 的图标, 如果配置整体默认背景颜色, 则为模块 app 的图标
 * @property defaultColorLight 默认浅色背景颜色
 * @property defaultColorDark 默认暗色背景颜色
 */
private class AppColorConfig(realPackageName: String?, context: Context) {
    private val pm = context.packageManager
    private val prefs = context.prefs()

    val isConfiguringOverallBGColor = realPackageName.isNullOrBlank()

    val packageName: String = realPackageName.takeIf { !it.isNullOrBlank() } ?: context.packageName
    val appName = if (isConfiguringOverallBGColor) {
        context.getString(R.string.set_custom_bg_color)
    } else {
        pm.getApplicationInfo(packageName, 0).loadLabel(pm).toString()
    }

    val appIcon = (IconPackManager(context, prefs.get(DataConst.ICON_PACK_PACKAGE_NAME))
        .getIconByPackageName(packageName)                      // 优先获取图标包中的图标
        ?: pm.getApplicationIcon(packageName)).toBitmap()       // 使用默认方式获取图标
    var defaultColorLight = processDefaultBGColor(false)
    var defaultColorDark = processDefaultBGColor(true)

    /**
     * 获取默认背景颜色
     * @param isDark 是否为暗色
     */
    fun getDefaultBGColor(isDark: Boolean): Int = if (isDark) {
        defaultColorDark
    } else {
        defaultColorLight
    }

    /**
     * 通过读取 Prefs 来获取已保存的背景颜色
     */
    private fun processDefaultBGColor(isDark: Boolean): Int {
        val colorValue = when {
            isConfiguringOverallBGColor && isDark -> {
                prefs.get(DataConst.OVERALL_BG_COLOR_NIGHT)
            }
            isConfiguringOverallBGColor -> {
                prefs.get(DataConst.OVERALL_BG_COLOR)
            }
            !isConfiguringOverallBGColor && isDark -> {
                prefs.get(DataConst.INDIVIDUAL_BG_COLOR_APP_MAP_DARK)
                    .toMap()[packageName].takeIf { !it.isNullOrBlank() }
            }
            !isConfiguringOverallBGColor -> {
                prefs.get(DataConst.INDIVIDUAL_BG_COLOR_APP_MAP)
                    .toMap()[packageName].takeIf { !it.isNullOrBlank() }
            }
            else -> null
        }

        val color = colorValue?.let { android.graphics.Color.parseColor(it) }
            ?: getBgColor(appIcon, !isDark)

        return color
    }
}

/**
 * 配置已选择的背景颜色
 *
 * @param rgbColorState 存储 RGB 颜色的状态
 * @param hsvColorState 存储 HSV 颜色的状态
 *
 * @property r 设置或获取颜色的 R 值
 * @property g 设置或获取颜色的 G 值
 * @property b 设置或获取颜色的 B 值
 * @property h 设置或获取颜色的 H 值
 * @property s 设置或获取颜色的 S 值
 * @property v 设置或获取颜色的 V 值
 * @property colorInt 设置或获取颜色的 Int 值
 */
class PickedColor(
    private val rgbColorState: MutableIntState,
    private val hsvColorState: SnapshotStateList<Float>
) {
    var r
        get() = (rgbColorState.intValue shr 16) and 0xFF
        set(value) {
            colorInt = (rgbColorState.intValue and 0xFF00FFFF.toInt()) or ((value and 0xFF) shl 16)
        }
    var g
        get() = (rgbColorState.intValue shr 8) and 0xFF
        set(value) {
            colorInt = (rgbColorState.intValue and 0xFFFF00FF.toInt()) or ((value and 0xFF) shl 8)
        }
    var b
        get() = rgbColorState.intValue and 0xFF
        set(value) {
            colorInt = (rgbColorState.intValue and 0xFFFF00FF.toInt()) or ((value and 0xFF) shl 8)
        }

    var h
        get() = hsvColorState[0]
        set(value) { setHsvColor(0, value) }
    var s
        get() = hsvColorState[1]
        set(value) { setHsvColor(1, value) }
    var v
        get() = hsvColorState[2]
        set(value) { setHsvColor(2, value) }

    var colorInt = 0
        get() = rgbColorState.intValue
        set(value) {
            if (value == 0) field = 0xFFFFFFFF.toInt()
            else {
                hsvColorState.addAll(value.toHSVColorList())
                hsvColorState.removeRange(0, 3)
                rgbColorState.intValue = value
            }
        }

    /**
     * 配置 HSV 颜色其中之一属性
     */
    private fun setHsvColor(index: Int, value: Float) {
        hsvColorState[index] = value
        rgbColorState.intValue = android.graphics.Color.HSVToColor(hsvColorState.toFloatArray())
    }
}