package com.gswxxn.restoresplashscreen.ui.apppage

import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.component.SpliceCard
import com.gswxxn.restoresplashscreen.utils.CommonUtils.notEqualsTo
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toMap
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toSet
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.AlertDialogMode
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.DrawableResIcon
import dev.lackluster.hyperx.compose.base.HazeScaffold
import dev.lackluster.hyperx.compose.base.IconSize
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.icon.Back
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextDialog
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Info
import top.yukonga.miuix.kmp.icon.icons.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.HorizontalDivider
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.getWindowSize

/**
 * 基础设置 - 遮罩最小持续时长
 */
@Composable
fun MinDurationPage(
    navController: NavController,
    adjustPadding: PaddingValues,
    mode: BasePageDefaults.Mode,
    blurEnabled: MutableState<Boolean> = MainActivity.blurEnabled,
    blurTintAlphaLight: MutableFloatState = MainActivity.blurTintAlphaLight,
    blurTintAlphaDark: MutableFloatState = MainActivity.blurTintAlphaDark,
) {
    val checkedListKey = DataConst.MIN_DURATION_LIST.key
    val configMapKey = DataConst.MIN_DURATION_CONFIG_MAP.key
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

    val dialogMessage = stringResource(R.string.set_min_duration) + "\n" + stringResource(R.string.set_min_duration_unit)
    var queryString by remember { mutableStateOf("") }

    val emptyMapString = stringResource(R.string.not_set_min_duration)

    // 完整应用列表
    var appInfoList by remember { mutableStateOf<List<DurationAppInfo>>(emptyList()) }

    // 在列表中的条目
    var appInfoFilter by remember { mutableStateOf<List<DurationAppInfo>>(emptyList()) }

    // 保存前的配置
    val tmpCheckedList = mutableSetOf<String>().apply {
        clear()
        addAll(checkedListKey.let { SafeSP.mSP?.getStringSet(it, emptySet()) } ?: emptySet())
    }
    val tmpConfigMap = mutableMapOf<String, String>().apply {
        clear()
        putAll((SafeSP.mSP?.getStringSet(checkedListKey, emptySet())?: emptySet()).toMap())
    }

    val coroutineScope = rememberCoroutineScope()
    var queryJob: Job? = null
    var isLoading by remember { mutableStateOf(true) }

    BackHandler(true) {
        val currentCheckedList = appInfoList.filter { it.isChecked.value }.map {
            it.packageName
        }.toSet()
        val currentConfigMap = appInfoList.filter {
            it.config.value != null && it.config.value != emptyMapString
        }.map {
            "${it.packageName}_${it.config.value}"
        }.toSet()
        if (currentCheckedList.notEqualsTo(tmpCheckedList) || currentConfigMap.notEqualsTo(tmpConfigMap.toSet())) {
            modifiedDialogVisibility.value = true
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        launch {
            isLoading = true
            delay(500)
            val pm = HyperXActivity.context.packageManager
            appInfoList = pm.getInstalledApplications(0).map {
                DurationAppInfo(
                    it.loadLabel(pm).toString(),
                    it.packageName,
                    it.loadIcon(pm),
                    mutableStateOf(it.packageName in tmpCheckedList),
                    mutableStateOf(tmpConfigMap[it.packageName])
                )
            }.toList()
            isLoading = false
        }
    }

    LaunchedEffect(appInfoList, queryString) {
        if (appInfoList.isEmpty()) return@LaunchedEffect
        appInfoFilter = emptyList()
        queryJob?.cancel()
        queryJob = launch {
            if (queryString.isBlank()) {
                delay(100)
                appInfoFilter = appInfoList.toMutableList().apply {
                    sortBy { it.appName }
                    sortByDescending { it.config.value != null}
                    sortByDescending { it.isChecked.value }
                }
            } else {
                delay(300)
                appInfoFilter = appInfoList.filter {
                    it.appName.contains(queryString, true) or it.packageName.contains(queryString, true)
                }.toMutableList().apply {
                    sortBy { it.appName }
                    sortByDescending { it.config.value != null }
                    sortByDescending { it.isChecked.value }
                }
            }
        }
    }
    val layoutDirection = LocalLayoutDirection.current
    val systemBarInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal).asPaddingValues()
    val navigationIconPadding = PaddingValues.Absolute(
        left = if (mode != BasePageDefaults.Mode.SPLIT_RIGHT) systemBarInsets.calculateLeftPadding(layoutDirection) else 0.dp
    )

    HazeScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { contentPadding ->
            TopAppBar(
                color = topAppBarBackground.copy(
                    if (topBarBlurState) 0f else 1f
                ),
                title = stringResource(R.string.min_duration_title),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier
                            .padding(navigationIconPadding)
                            .padding(start = 21.dp)
                            .size(40.dp),
                        onClick = {
                            val currentCheckedList = appInfoList.filter { it.isChecked.value }.map {
                                it.packageName
                            }.toSet()
                            val currentConfigMap = appInfoList.filter {
                                it.config.value != null && it.config.value != emptyMapString
                            }.map {
                                "${it.packageName}_${it.config.value}"
                            }.toSet()
                            if (currentCheckedList.notEqualsTo(tmpCheckedList) || currentConfigMap.notEqualsTo(tmpConfigMap.toSet())) {
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
                defaultWindowInsetsPadding = false,
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
                    TextButton(
                        modifier = Modifier
                            .padding(buttonPaddingValues)
                            .fillMaxWidth(),
                        text = stringResource(R.string.save),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        minHeight = 50.dp,
                        onClick = {
                            CoroutineScope(Dispatchers.Default).launch {
                                SafeSP.mSP?.let { sp ->
                                    val currentCheckedList = appInfoList.filter { it.isChecked.value }.map {
                                        it.packageName
                                    }.toSet()
                                    val currentConfigMap = appInfoList.filter {
                                        it.config.value != null && it.config.value != emptyMapString
                                    }.map {
                                        "${it.packageName}_${it.config.value}"
                                    }.toSet()
                                    sp.edit {
                                        putStringSet(checkedListKey, currentCheckedList)
                                        putStringSet(configMapKey, currentConfigMap)
                                        commit()
                                    }
                                    tmpCheckedList.apply {
                                        clear()
                                        addAll(checkedListKey.let { SafeSP.mSP?.getStringSet(it, emptySet()) } ?: emptySet())
                                    }
                                    tmpConfigMap.apply {
                                        clear()
                                        putAll((SafeSP.mSP?.getStringSet(checkedListKey, emptySet())?: emptySet()).toMap())
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
                .background(MiuixTheme.colorScheme.background),
            state = listState,
            contentPadding = paddingValues,
            topAppBarScrollBehavior = scrollBehavior
        ) {
            item {
                SearchBar(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
                    inputField = {
                        InputField(
                            query = queryString,
                            onQueryChange = { queryString = it },
                            onSearch = { },
                            expanded = false,
                            onExpandedChange = { },
                            label = stringResource(R.string.search_hint),
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.padding(start = 12.dp, end = 8.dp),
                                    imageVector = MiuixIcons.Search,
                                    tint = MiuixTheme.colorScheme.onSurfaceContainer,
                                    contentDescription = "Search"
                                )
                            },
                        )
                    },
                    expanded = false,
                    onExpandedChange = { },
                    content = { }
                )
            }
            item {
                PreferenceGroup {
                    BasicComponent(
                        insideMargin = PaddingValues(16.dp),
                        summary = stringResource(R.string.min_duration_sub_setting_hint),
                        leftAction = {
                            Image(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(28.dp),
                                imageVector = MiuixIcons.Info,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary)
                            )
                        }
                    )
                }
            }
            item {
                PreferenceGroup {
                    EditTextPreference(
                        title = stringResource(R.string.set_default_min_duration),
                        key = DataConst.MIN_DURATION.key,
                        dataType = EditTextDataType.INT,
                        dialogMessage = stringResource(R.string.set_min_duration_unit),
                        isValueValid = { (it as? Int) in 0..1000}
                    )
                }
            }
            if (isLoading) {
                item {
                    SmallTitle(
                        text = stringResource(R.string.loading),
                        modifier = Modifier.padding(top = 6.dp),
                        textColor = MiuixTheme.colorScheme.onBackgroundVariant
                    )
                }
            } else {
                item {
                    SmallTitle(
                        text = stringResource(R.string.min_duration_separate_configuration),
                        modifier = Modifier.padding(top = 6.dp),
                        textColor = MiuixTheme.colorScheme.onBackgroundVariant
                    )
                }
                itemsIndexed(appInfoFilter, key = { index, item ->
                    item.packageName + item.isChecked + index + appInfoFilter.size
                }) { index, item ->
                    val topCornerRadius = if (index == 0) CardDefaults.ConorRadius else 0.dp
                    val bottomCornerRadius = if (index == appInfoFilter.size - 1) CardDefaults.ConorRadius else 0.dp
                    SpliceCard(
                        topCornerRadius,
                        bottomCornerRadius
                    ) {
                        MinDurationPreference(
                            icon = ImageIcon(
                                iconBitmap = item.icon.toBitmap().asImageBitmap(),
                                iconSize = IconSize.App
                            ),
                            title = item.appName,
                            summary = item.packageName,
                            checked = item.isChecked,
                            defValue = item.config.value?.toIntOrNull() ?: 0,
                            dialogMessage = dialogMessage
                        ) { text, value ->
                            if (value == 0) {
                                item.config.value = null
                            } else {
                                item.config.value = text
                            }
                        }
                    }
                }
            }
            item {
                Spacer(
                    modifier = Modifier.height(6.dp)
                )
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
}

@Composable
fun MinDurationPreference(
    icon: ImageIcon? = null,
    title: String,
    summary: String? = null,
    checked: MutableState<Boolean>,
    defValue: Int = 0,
    dialogMessage: String? = null,
    onValueChange: ((String, Int) -> Unit)? = null,
) {
    val emptyString = stringResource(R.string.not_set_min_duration)
    val dialogVisibility = remember { mutableStateOf(false) }
    val spValue = remember { mutableIntStateOf(defValue) }
    val stringValue = if (spValue.intValue != 0) spValue.intValue.toString() else emptyString
    val doOnInputConfirm: (String) -> Unit = { newString: String ->
        val oldValue = spValue.intValue
        val newValue = newString.toIntOrNull()
        if (newValue != null && (newValue in 0..1000) && oldValue != newValue) {
            spValue.intValue = newValue
            onValueChange?.let { it(newString, newValue) }
        }
    }
    BasicComponent(
        title = title,
        summary = summary,
        leftAction = {
            icon?.let {
                DrawableResIcon(it)
            }
        },
        rightActions = {
            Text(
                modifier = Modifier.widthIn(max = 130.dp).padding(end = 12.dp),
                text = stringValue,
                fontSize = MiuixTheme.textStyles.body2.fontSize,
                color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Switch(
                checked = checked.value,
                onCheckedChange = { newValue ->
                    checked.value = newValue
                }
            )
        },
        insideMargin = PaddingValues((icon?.getHorizontalPadding() ?: 16.dp), 16.dp, 16.dp, 16.dp),
        onClick = {
            dialogVisibility.value = true
        }
    )
    EditTextDialog(
        visibility = dialogVisibility,
        title = title,
        message = dialogMessage,
        value = spValue.intValue.toString(),
        onInputConfirm = { newString ->
            doOnInputConfirm(newString)
        }
    )
}

data class DurationAppInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    // 该 isChecked 用于存储应用是否被勾选, 0 为未勾选, 1 为勾选
    var isChecked: MutableState<Boolean>,
    var config: MutableState<String?>,
)