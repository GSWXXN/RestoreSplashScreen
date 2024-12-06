package com.gswxxn.restoresplashscreen.ui.component

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toMap
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toSet
import com.gswxxn.restoresplashscreen.utils.GraphicUtils.getBgColor
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.AlertDialogMode
import dev.lackluster.hyperx.compose.base.HazeScaffold
import dev.lackluster.hyperx.compose.icon.Back
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.EditTextDialog
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
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
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.HorizontalDivider
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round

@Composable
fun ColorPickerPage(
    navController: NavController,
    adjustPadding: PaddingValues,
    pkgName: String,
    keyLight: String,
    keyDark: String,
    blurEnabled: MutableState<Boolean> = MainActivity.blurEnabled,
    blurTintAlphaLight: MutableFloatState = MainActivity.blurTintAlphaLight,
    blurTintAlphaDark: MutableFloatState = MainActivity.blurTintAlphaDark,
) {
    val topAppBarBackground = MiuixTheme.colorScheme.background
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()
    val topBarBlurState by remember {
        derivedStateOf {
            blurEnabled.value &&
                    scrollBehavior.state.collapsedFraction >= 1.0f &&
                    (listState.isScrollInProgress || listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 12)
        }
    }
    val topBarBlurTintAlpha = remember { mutableFloatStateOf(
        if (topAppBarBackground.luminance() >= 0.5f) blurTintAlphaLight.floatValue
        else blurTintAlphaDark.floatValue
    ) }
    val modifiedDialogVisibility = remember { mutableStateOf(false) }
    val dropdownDialogVisibility = remember { mutableStateOf(false) }
    val colorModeEntries = listOf(
        DropDownEntry(title = stringResource(R.string.target_color_mode_light)),
        DropDownEntry(title = stringResource(R.string.target_color_mode_dark))
    )
    val deviceDarkMode = isSystemInDarkTheme()
    val currentDarkMode = remember { mutableStateOf(deviceDarkMode) }
    var magnifierCenter by remember { mutableStateOf(Offset.Unspecified) }

    val appName: String
    val appIcon: Bitmap
    with(HyperXActivity.context) {
        val pm = this.packageManager
        val realPkgName: String
        if (pkgName.isNotBlank()) {
            realPkgName = pkgName
            appName = pm.getApplicationInfo(realPkgName, 0).loadLabel(pm).toString()
        } else {
            realPkgName = packageName
            appName = stringResource(R.string.set_custom_bg_color)
        }
        val iconPackPkg = SafeSP.getString(DataConst.ICON_PACK_PACKAGE_NAME.key, DataConst.ICON_PACK_PACKAGE_NAME.value)
        appIcon = (IconPackManager(this, iconPackPkg).getIconByPackageName(realPkgName) ?: pm.getApplicationIcon(realPkgName)).toBitmap()
    }

    var defaultColorLight = pkgName.let {
        if (it.isNotBlank()) {
            val value = (SafeSP.mSP?.getStringSet(keyLight, emptySet()) ?: emptySet()).toMap()[pkgName]
            if (value.isNullOrBlank())
                Color(getBgColor(appIcon, true))
            else
                Color(android.graphics.Color.parseColor(value))
        } else {
            val value = SafeSP.getString(keyLight, "")
            if (value.isBlank())
                Color.White
            else
                Color(android.graphics.Color.parseColor(value))
        }
    }
    var defaultColorDark = pkgName.let {
        if (it.isNotBlank()) {
            val value = (SafeSP.mSP?.getStringSet(keyDark, emptySet()) ?: emptySet()).toMap()[pkgName]
            if (value.isNullOrBlank())
                Color(getBgColor(appIcon, false))
            else
                Color(android.graphics.Color.parseColor(value))
        } else {
            val value = SafeSP.getString(keyDark, "")
            if (value.isBlank())
                Color.Black
            else
                Color(android.graphics.Color.parseColor(value))
        }
    }
    val selectedColor = remember { mutableStateOf(if (currentDarkMode.value) defaultColorDark else defaultColorLight) }
    val paletteColors = remember { mutableStateListOf<Color>() }
    val colorR = selectedColor.value.r
    val colorG = selectedColor.value.g
    val colorB = selectedColor.value.b
    val hueColor = remember { mutableStateOf(
        (if (currentDarkMode.value) defaultColorDark else defaultColorLight).let {
            Triple(it.h, it.s, it.v)
        }
    ) }
    val colorH: Float = hueColor.value.first
    val colorS: Float = hueColor.value.second
    val colorV: Float = hueColor.value.third

    val coroutineScope = rememberCoroutineScope()

    BackHandler(true) {
        val defColor = if (currentDarkMode.value) defaultColorDark else defaultColorLight
        if (defColor != selectedColor.value) {
            modifiedDialogVisibility.value = true
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        launch {
            paletteColors.clear()
            delay(500)
            val palette = Palette.from(appIcon).maximumColorCount(8).generate()
            val colors = listOf(
                palette.getDominantColor(0),
                palette.getLightVibrantColor(0),
                palette.getVibrantColor(0),
                palette.getDarkVibrantColor(0),
                palette.getLightMutedColor(0),
                palette.getMutedColor(0),
                palette.getDarkMutedColor(0)
            ).distinct().map {
                Color(it)
            }.filter {
                it.alpha == 1.0f
            }
            paletteColors.addAll(colors)
        }
    }

    HazeScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { contentPadding ->
            TopAppBar(
                color = topAppBarBackground.copy(
                    if (topBarBlurState) 0f else 1f
                ),
                title = appName,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier
                            .padding(start = 21.dp)
                            .size(40.dp),
                        onClick = {
                            val defColor = if (currentDarkMode.value) defaultColorDark else defaultColorLight
                            if (defColor != selectedColor.value) {
                                modifiedDialogVisibility.value = true
                            } else {
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(26.dp),
                            imageVector = MiuixIcons.Back,
                            contentDescription = "Back",
                            tint = MiuixTheme.colorScheme.onSurfaceSecondary
                        )
                    }
                },
                horizontalPadding = 28.dp + contentPadding.calculateLeftPadding(LocalLayoutDirection.current)
            )
        },
        bottomBar = { contentPadding ->
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
            Surface(
                color = MiuixTheme.colorScheme.background.copy(
                    if (blurEnabled.value) 0f else 1f
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                ) {
                    HorizontalDivider(
                        thickness = 0.75.dp,
                        color = MiuixTheme.colorScheme.dividerLine
                    )
                    Row(
                        modifier = Modifier
                            .padding(buttonPaddingValues)
                            .fillMaxWidth()
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1.0f),
                            text = stringResource(R.string.undo_modification),
                            minHeight = 50.dp,
                            onClick = {
                                (if (currentDarkMode.value) defaultColorDark else defaultColorLight).let { color ->
                                    selectedColor.value = color
                                    hueColor.value = Triple(color.h, color.s, color.v)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        TextButton(
                            modifier = Modifier.weight(1.0f),
                            text = stringResource(R.string.save),
                            colors = selectedColor.value.let {
                                TextButtonColors(
                                    color = it,
                                    disabledColor = it,
                                    textColor = if (it.luminance() > 0.5f) Color.Black else Color.White,
                                    disabledTextColor = it
                                )
                            },
                            minHeight = 50.dp,
                            onClick = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    SafeSP.mSP?.let { sp ->
                                        val targetKey = if (currentDarkMode.value) {
                                            keyDark
                                        } else {
                                            keyLight
                                        }
                                        val colorHexString = "#" + "%08X".format(selectedColor.value.toArgb()).substring(2)
                                        if (pkgName.isNotBlank()) {
                                            val tmpConfigMap = (SafeSP.mSP?.getStringSet(targetKey, emptySet()) ?: emptySet()).toMap()
                                            tmpConfigMap[pkgName] = colorHexString
                                            sp.edit {
                                                putStringSet(targetKey, tmpConfigMap.toSet())
                                                commit()
                                            }
                                        } else {
                                            sp.edit {
                                                putString(targetKey, colorHexString)
                                            }
                                        }
                                        defaultColorLight = pkgName.let {
                                            if (it.isNotBlank()) {
                                                val value = (SafeSP.mSP?.getStringSet(keyLight, emptySet()) ?: emptySet()).toMap()[pkgName]
                                                if (value.isNullOrBlank())
                                                    Color(getBgColor(appIcon, true))
                                                else
                                                    Color(android.graphics.Color.parseColor(value))
                                            } else {
                                                val value = SafeSP.getString(keyLight, "")
                                                if (value.isBlank())
                                                    Color.White
                                                else
                                                    Color(android.graphics.Color.parseColor(value))
                                            }
                                        }
                                        defaultColorDark = pkgName.let {
                                            if (it.isNotBlank()) {
                                                val value = (SafeSP.mSP?.getStringSet(keyDark, emptySet()) ?: emptySet()).toMap()[pkgName]
                                                if (value.isNullOrBlank())
                                                    Color(getBgColor(appIcon, false))
                                                else
                                                    Color(android.graphics.Color.parseColor(value))
                                            } else {
                                                val value = SafeSP.getString(keyDark, "")
                                                if (value.isBlank())
                                                    Color.Black
                                                else
                                                    Color(android.graphics.Color.parseColor(value))
                                            }
                                        }
                                        coroutineScope.launch {
                                            HyperXActivity.context.let {
                                                Toast.makeText(it, it.getString(R.string.save_successful), Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } ?: coroutineScope.launch {
                                        HyperXActivity.context.let {
                                            Toast.makeText(it, it.getString(R.string.save_failed), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        blurTopBar = blurEnabled.value,
        blurBottomBar = blurEnabled.value,
        hazeStyle = HazeStyle(
            blurRadius = 66.dp,
            backgroundColor = topAppBarBackground,
            tint = HazeTint(
                topAppBarBackground.copy(alpha = topBarBlurTintAlpha.floatValue),
            )
        ),
        adjustPadding = adjustPadding,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .height(getWindowSize().height.dp)
                .background(MiuixTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)),
            state = listState,
            contentPadding = paddingValues,
            topAppBarScrollBehavior = scrollBehavior
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp, top = 12.dp),
                    color = colorResource(R.color.colorDemoBackground)
                ) {
                    Layout(
                        content = {
                            Image(
                                modifier = Modifier.drawBehind {
                                    drawRect(selectedColor.value)
                                },
                                painter = painterResource(R.drawable.demo_transparency),
                                contentDescription = null
                            )
                            val dp100px = with(LocalDensity.current) { 100.dp.toPx() }
                            Image(
                                modifier = Modifier
                                    .magnifier(
                                        sourceCenter = { magnifierCenter },
                                        magnifierCenter = { magnifierCenter - Offset(0f, dp100px) },
                                        zoom = 5f,
                                        size = DpSize(100.dp, 100.dp),
                                        cornerRadius = 50.dp
                                    )
                                    .pointerInput(Unit) {
                                        detectDragGestures(
                                            // Show the magnifier at the original pointer position.
                                            onDragStart = {
                                                magnifierCenter = it
                                                Color(
                                                    appIcon.getColor(
                                                        (appIcon.width * magnifierCenter.x / size.width).toInt().coerceIn(0, appIcon.width - 1),
                                                        (appIcon.height * magnifierCenter.y / size.height).toInt().coerceIn(0, appIcon.height - 1)
                                                    ).toArgb()
                                                ).copy(1.0f).let { color ->
                                                    selectedColor.value = color
                                                    hueColor.value = Triple(color.h, color.s, color.v)
                                                }
                                            },
                                            // Make the magnifier follow the finger while dragging.
                                            onDrag = { _, delta ->
                                                magnifierCenter += delta
                                                Color(
                                                    appIcon.getColor(
                                                        (appIcon.width * magnifierCenter.x / size.width).toInt().coerceIn(0, appIcon.width - 1),
                                                        (appIcon.height * magnifierCenter.y / size.height).toInt().coerceIn(0, appIcon.height - 1)
                                                    ).toArgb()
                                                ).copy(1.0f).let { color ->
                                                    selectedColor.value = color
                                                    hueColor.value = Triple(color.h, color.s, color.v)
                                                }
                                            },
                                            // Hide the magnifier when the finger lifts.
                                            onDragEnd = { magnifierCenter = Offset.Unspecified },
                                            onDragCancel = { magnifierCenter = Offset.Unspecified }
                                        )
                                    }
                                ,
                                bitmap = appIcon.asImageBitmap(),
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(R.string.please_select),
                                fontSize = MiuixTheme.textStyles.body2.fontSize,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                            for (color in paletteColors) {
                                Surface(
                                    border = BorderStroke(2.dp, MiuixTheme.colorScheme.dividerLine),
                                    shape = CircleShape,
                                    color = color,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                color.let { color ->
                                                    selectedColor.value = color
                                                    hueColor.value = Triple(color.h, color.s, color.v)
                                                }
                                            }
                                    )
                                }
                            }
                        }
                    ) { measurables, constraints ->
                        if (measurables.size < 3) {
                            layout(0, 0) { }
                        }
                        val px16dp = 16.dp.roundToPx()
                        val px32dp = 32.dp.roundToPx()
                        val px48dp = 48.dp.roundToPx()
                        val px250dp = 250.dp.roundToPx()

                        val image = measurables[0].measure(constraints.copy(
                            minHeight = px250dp, maxHeight = px250dp
                        ))
                        val icon = measurables[1].measure(constraints.copy(
                            minHeight = px48dp, maxHeight = px48dp,
                            minWidth = px48dp, maxWidth = px48dp
                        ))
                        val rightWidth = constraints.maxWidth - image.width - px48dp
                        val text = measurables[2].measure(constraints.copy(
                            maxWidth = rightWidth
                        ))
                        val colorCount = measurables.size - 3
                        var colorPerRow = 1
                        val colorLines: Int
                        val colors: List<Placeable>?
                        if (colorCount > 0) {
                            val colorConstraints = constraints.copy(
                                minHeight = px32dp, maxHeight = px32dp,
                                minWidth = px32dp, maxWidth = px32dp
                            )
                            colors = measurables.subList(3, measurables.size).map {
                                it.measure(colorConstraints)
                            }
                            while (px48dp * (colorPerRow + 1) - px16dp < rightWidth) {
                                colorPerRow++
                            }
                            colorLines = colorCount / colorPerRow + if (colorCount % colorPerRow > 0) 1 else 0
                        } else {
                            colorLines = 0
                            colors = null
                        }
                        val rightHeight = text.height + (colorLines + 1) * px32dp + colorLines * px16dp
                        val totalHeight = max(image.height, rightHeight) + px32dp
                        layout(constraints.maxWidth, totalHeight) {
                            image.place(px16dp, (totalHeight - image.height) / 2)
                            icon.place(
                                px16dp + image.width / 2 - icon.width / 2,
                                totalHeight / 2 - icon.height / 2
                            )
                            val rightCenterX = px16dp + image.width + (constraints.maxWidth - px16dp - image.width) / 2
                            val textY = (totalHeight - rightHeight) / 2
                            text.place(rightCenterX - text.width / 2, textY)
                            if (colors != null) {
                                var line = 0
                                val colorX = rightCenterX - (px48dp * colorPerRow - px16dp) / 2
                                while (colorCount >= colorPerRow * (line + 1)) {
                                    val colorY = textY + text.height + px16dp + px48dp * line
                                    for (i in 0 until colorPerRow) {
                                        colors[line * colorPerRow + i].place(
                                            colorX + px48dp * i, colorY
                                        )
                                    }
                                    line++
                                }
                                val lastLineCount = colorCount % colorPerRow
                                if (lastLineCount > 0) {
                                    val lastLineWidth = px48dp * lastLineCount - px16dp
                                    var lastColorX = rightCenterX - lastLineWidth / 2
                                    val colorY = textY + text.height + px16dp + px48dp * line
                                    for (i in line * colorPerRow until colorCount) {
                                        colors[i].place(lastColorX, colorY)
                                        lastColorX += px48dp
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                PreferenceGroup {
                    ColorModePreference(
                        title = stringResource(R.string.target_color_mode),
                        summary = stringResource(R.string.target_color_mode_tips),
                        entries = colorModeEntries,
                        darkMode = currentDarkMode
                    ) {
                        val defColor = if (currentDarkMode.value) defaultColorDark else defaultColorLight
                        if (defColor != selectedColor.value) {
                            dropdownDialogVisibility.value = true
                        } else {
                            currentDarkMode.value = !currentDarkMode.value
                            (if (currentDarkMode.value) defaultColorDark else defaultColorLight).let { color ->
                                selectedColor.value = color
                                hueColor.value = Triple(color.h, color.s, color.v)
                            }
                        }
                    }
                    ColorStringPreference(
                        title = stringResource(R.string.manual_input),
                        colorValue = selectedColor,
                        hueColor = hueColor,
                        dialogMessage = stringResource(R.string.manual_input_hint)
                    )
                }
            }
            item {
                PreferenceGroup(
                    title = stringResource(R.string.rgb_color_space)
                ) {
                    IntColorSeekBar(
                        title = stringResource(R.string.rgb_r),
                        value = colorR,
                        min = 0,
                        max = 255,
                        colors = SliderColors(
                            foregroundColor = Color(0xFFF36060),
                            disabledForegroundColor = Color(0x7FF36060),
                            backgroundColor = MiuixTheme.colorScheme.tertiaryContainerVariant
                        )
                    ) {
                        Color(it, colorG, colorB).let { color ->
                            selectedColor.value = color
                            hueColor.value = Triple(color.h, color.s, color.v)
                        }
                    }
                    IntColorSeekBar(
                        title = stringResource(R.string.rgb_g),
                        value = colorG,
                        min = 0,
                        max = 255,
                        colors = SliderColors(
                            foregroundColor = Color(0xFF5FF25F),
                            disabledForegroundColor = Color(0x7F5FF25F),
                            backgroundColor = MiuixTheme.colorScheme.tertiaryContainerVariant
                        )
                    ) {
                        Color(colorR, it, colorB).let { color ->
                            selectedColor.value = color
                            hueColor.value = Triple(color.h, color.s, color.v)
                        }
                    }
                    IntColorSeekBar(
                        title = stringResource(R.string.rgb_b),
                        value = colorB,
                        min = 0,
                        max = 255,
                        colors = SliderColors(
                            foregroundColor = Color(0xFF5F5FF3),
                            disabledForegroundColor = Color(0x7F5F5FF3),
                            backgroundColor = MiuixTheme.colorScheme.tertiaryContainerVariant
                        )
                    ) {
                        Color(colorR, colorG, it).let { color ->
                            selectedColor.value = color
                            hueColor.value = Triple(color.h, color.s, color.v)
                        }
                    }
                }
            }
            item {
                PreferenceGroup(
                    title = stringResource(R.string.hsv_color_space)
                ) {
                    HueSeekBar(
                        title = stringResource(R.string.hue),
                        value = colorH
                    ) {
                        selectedColor.value = Color.hsv(it, colorS, colorV)
                        hueColor.value = hueColor.value.copy(
                            first = it
                        )
                    }
                    FloatColorSeekBar(
                        title = stringResource(R.string.saturation),
                        value = colorS,
                        min = 0.0f,
                        max = 1.0f,
                    ) {
                        selectedColor.value = Color.hsv(colorH, it, colorV)
                        hueColor.value = hueColor.value.copy(
                            second = it
                        )
                    }
                    FloatColorSeekBar(
                        title = stringResource(R.string.value),
                        value = colorV,
                        min = 0.0f,
                        max = 1.0f
                    ) {
                        selectedColor.value = Color.hsv(colorH, colorS, it)
                        hueColor.value = hueColor.value.copy(
                            third = it
                        )
                    }
                }
            }
            item {
                PreferenceGroup(
                    first = true,
                    last = true
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                CoroutineScope(Dispatchers.Default).launch {
                                    SafeSP.mSP?.let { sp ->
                                        val targetKey = if (currentDarkMode.value) {
                                            keyDark
                                        } else {
                                            keyLight
                                        }
                                        if (pkgName.isNotBlank()) {
                                            val tmpConfigMap =
                                                (SafeSP.mSP?.getStringSet(targetKey, emptySet())
                                                    ?: emptySet()).toMap()
                                            tmpConfigMap.remove(pkgName)
                                            sp.edit {
                                                putStringSet(targetKey, tmpConfigMap.toSet())
                                                commit()
                                            }
                                            defaultColorLight = Color(getBgColor(appIcon, true))
                                            defaultColorDark = Color(getBgColor(appIcon, false))
                                        } else {
                                            sp.edit {
                                                remove(targetKey)
                                                commit()
                                            }
                                            defaultColorLight = Color.White
                                            defaultColorDark = Color.Black
                                        }
                                        coroutineScope.launch {
                                            selectedColor.value =
                                                if (currentDarkMode.value) defaultColorDark
                                                else defaultColorLight
                                            HyperXActivity.context.let {
                                                Toast.makeText(
                                                    it,
                                                    it.getString(R.string.save_successful),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } ?: coroutineScope.launch {
                                        HyperXActivity.context.let {
                                            Toast.makeText(
                                                it,
                                                it.getString(R.string.save_failed),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
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
            }
        }
    }
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
            (if (currentDarkMode.value) defaultColorDark else defaultColorLight).let { color ->
                selectedColor.value = color
                hueColor.value = Triple(color.h, color.s, color.v)
            }
        }
    )
}

@Composable
fun ColorModePreference(
    title: String,
    summary: String? = null,
    entries: List<DropDownEntry>,
    darkMode: MutableState<Boolean>,
    onSelectedIndexChange: ((Int) -> Unit)
) {
    val wrappedEntries = entries.map { entry ->
        SpinnerEntry(
            title = entry.title,
        )
    }
    SuperSpinner(
        title = title,
        summary = summary,
        items = wrappedEntries,
        selectedIndex = if (darkMode.value) 1 else 0,
        mode = SpinnerMode.AlwaysOnRight,
        showValue = true
    ) { newValue ->
        onSelectedIndexChange(newValue)
    }
}

@Composable
fun ColorStringPreference(
    title: String,
    summary: String? = null,
    colorValue: MutableState<Color>,
    hueColor: MutableState<Triple<Float, Float, Float>>,
    dialogMessage: String? = null
) {
    val dialogVisibility = remember { mutableStateOf(false) }
    val stringValue = "#" + "%08X".format(colorValue.value.toArgb()).substring(2)
    val doOnInputConfirm: (String) -> Unit = { newString: String ->
        val trimmedString = newString.replace("#", "")
        if (trimmedString.length <= 6) {
            val oldValue = colorValue.value.toArgb()
            val newValue = trimmedString.toIntOrNull(16)
            if (newValue != null && oldValue != newValue) {
               Color(android.graphics.Color.parseColor("#$trimmedString")).let { color ->
                   colorValue.value = color
                   hueColor.value = Triple(color.h, color.s, color.v)
               }
            }
        }
    }
    SuperArrow(
        title = title,
        summary = summary,
        rightText = stringValue,
        insideMargin = PaddingValues(16.dp),
        onClick = {
            dialogVisibility.value = true
        }
    )
    EditTextDialog(
        visibility = dialogVisibility,
        title = title,
        message = dialogMessage,
        value = "%08X".format(colorValue.value.toArgb()).substring(2),
        onInputConfirm = { newString ->
            doOnInputConfirm(newString)
        }
    )
}

@Composable
fun IntColorSeekBar(
    title: String,
    value: Int,
    min: Int,
    max: Int,
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
                    text = "$value / $max",
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            }
        )
        Slider(
            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp),
            progress = value.toFloat(),
            minValue = min.toFloat(),
            maxValue = max.toFloat(),
            height = 28.dp,
            colors = colors,
            onProgressChange = { newValue ->
                val newInt = newValue.toInt()
                onValueChange?.let { it1 -> it1(newInt) }
            }
        )
    }
}

@Composable
fun HueSeekBar(
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
                    style = Stroke(
                        width = 4.dp.toPx()
                    )
                )
            }
        }
    }
}

@Composable
fun FloatColorSeekBar(
    title: String,
    value: Float,
    min: Float,
    max: Float,
    onValueChange: ((Float) -> Unit)? = null
) {
    Column {
        BasicComponent(
            modifier = Modifier,
            insideMargin = PaddingValues(16.dp, 16.dp, 16.dp, 12.dp),
            title = title,
            rightActions = {
                Text(
                    text = "${value.let { it1 -> "%.2f".format(it1) }} / ${"%.2f".format(max)}",
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            }
        )
        Slider(
            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp),
            progress = value,
            minValue = min,
            maxValue = max,
            height = 28.dp,
            onProgressChange = { newValue ->
                onValueChange?.let { it1 -> it1(newValue) }
            }
        )
    }
}

private val Color.r: Int
    get() {
        return ((convert(ColorSpaces.Srgb).value shr 32).toInt() shr 16) and 0xFF
    }

private val Color.g: Int
    get() {
        return ((convert(ColorSpaces.Srgb).value shr 32).toInt() shr 8) and 0xFF
    }

private val Color.b: Int
    get() {
        return (convert(ColorSpaces.Srgb).value shr 32).toInt() and 0xFF
    }

private val Color.h: Float
    get() {
        val r = red.toDouble()
        val g = green.toDouble()
        val b = blue.toDouble()
        val min = minOf(r, g, b)
        val h = when (val max = maxOf(r, g, b)) {
            min -> 0.0
            r -> 60 * (g - b) / (max - min)
            g -> 120 + 60 * (b - r) / (max - min)
            b -> 240 + 60 * (r - g) / (max - min)
            else -> 0.0
        }
        return ((h + 360) % 360).toFloat().coerceIn(0.0f, 360.0f)
    }

private val Color.s: Float
    get() {
        val r = red.toDouble()
        val g = green.toDouble()
        val b = blue.toDouble()
        val min = minOf(r, g, b)
        val max = maxOf(r, g, b)
        return (
                if (max == 0.0) 0.0
                else ((max - min) / max)
        ).toFloat()
    }

private val Color.v: Float
    get() {
        val r = red.toDouble()
        val g = green.toDouble()
        val b = blue.toDouble()
        return maxOf(r, g, b).toFloat()
    }

object ColorPickerPageArgs {
    const val PACKAGE_NAME = "PkgName"
    const val KEY_LIGHT = "KeyLight"
    const val KEY_DARK = "KeyDark"
}