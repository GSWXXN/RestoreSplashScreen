package com.gswxxn.restoresplashscreen.ui.component

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.utils.CommonUtils.notEqualsTo
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.AlertDialogMode
import dev.lackluster.hyperx.compose.base.HazeScaffold
import dev.lackluster.hyperx.compose.base.IconSize
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.icon.Back
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
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
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ImmersionMore
import top.yukonga.miuix.kmp.icon.icons.Info
import top.yukonga.miuix.kmp.icon.icons.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.HorizontalDivider
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissPopup
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun AppListPage(
    navController: NavController,
    adjustPadding: PaddingValues,
    title: String,
    checkedListKey: String?,
    blurEnabled: MutableState<Boolean> = MainActivity.blurEnabled,
    blurTintAlphaLight: MutableFloatState = MainActivity.blurTintAlphaLight,
    blurTintAlphaDark: MutableFloatState = MainActivity.blurTintAlphaDark,
    extraContent: (LazyListScope.() -> Unit)? = null
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
    val isTopPopupExpanded = remember { mutableStateOf(false) }
    val showTopPopup = remember { mutableStateOf(false) }
    val modifiedDialogVisibility = remember { mutableStateOf(false) }

    var selectSystemAppRequest by remember { mutableStateOf(false) }
    var clearSelectedRequest by remember { mutableStateOf(false) }
    var queryString by remember { mutableStateOf("") }

    // 完整应用列表
    var appInfoList by remember { mutableStateOf<List<MyAppInfo>>(emptyList()) }

    // 在列表中的条目
    var appInfoFilter by remember { mutableStateOf<List<MyAppInfo>>(emptyList()) }

    // 保存前的配置
    val tmpCheckedList = mutableSetOf<String>().apply {
        clear()
        addAll(checkedListKey?.let { SafeSP.mSP?.getStringSet(it, emptySet()) } ?: emptySet())
    }

    val coroutineScope = rememberCoroutineScope()
    var queryJob: Job? = null
    var isLoading by remember { mutableStateOf(true) }

    BackHandler(true) {
        val currentCheckedList = appInfoList.filter { it.isChecked.value }.map {
            it.packageName
        }.toSet()
        if (currentCheckedList.notEqualsTo(tmpCheckedList)) {
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
                MyAppInfo(
                    it.loadLabel(pm).toString(),
                    it.packageName,
                    it.loadIcon(pm),
                    mutableStateOf(it.packageName in tmpCheckedList),
                    it.flags and ApplicationInfo.FLAG_SYSTEM != 0
                )
            }.toList()
            isLoading = false
        }
    }

    if (selectSystemAppRequest) {
        selectSystemAppRequest = false
        appInfoList.filter{ it.isSystemApp }.forEach {
            it.isChecked.value = true
        }
        appInfoFilter = appInfoList.toMutableList().apply {
            sortBy { it.appName }
            sortByDescending { it.isChecked.value }
        }
    }

    if (clearSelectedRequest) {
        clearSelectedRequest = false
        appInfoList.forEach {
            it.isChecked.value = false
        }
        appInfoFilter = appInfoList.toMutableList().apply {
            sortBy { it.appName }
            sortByDescending { it.isChecked.value }
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
                    sortByDescending { it.isChecked.value }
                }
            } else {
                delay(300)
                appInfoFilter = appInfoList.filter {
                    it.appName.contains(queryString, true) or it.packageName.contains(queryString, true)
                }.toMutableList().apply {
                    sortBy { it.appName }
                    sortByDescending { it.isChecked.value }
                }
            }
        }
    }

    HazeScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { contentPadding ->
            TopAppBar(
                color = topAppBarBackground.copy(
                    if (topBarBlurState) 0f else 1f
                ),
                title = title,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier
                            .padding(start = 21.dp)
                            .size(40.dp),
                        onClick = {
                            val currentCheckedList = appInfoList.filter { it.isChecked.value }.map {
                                it.packageName
                            }.toSet()
                            if (currentCheckedList.notEqualsTo(tmpCheckedList)) {
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
                actions = {
                    if (isTopPopupExpanded.value) {
                        ListPopup(
                            show = showTopPopup,
                            popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                            alignment = PopupPositionProvider.Align.TopRight,
                            onDismissRequest = {
                                isTopPopupExpanded.value = false
                            }
                        ) {
                            ListPopupColumn {
                                DropdownImpl(
                                    text = stringResource(R.string.select_system_apps),
                                    optionSize = 2,
                                    isSelected = false,
                                    onSelectedIndexChange = {
                                        selectSystemAppRequest = true
                                        dismissPopup(showTopPopup)
                                        isTopPopupExpanded.value = false
                                    },
                                    index = 0
                                )
                                DropdownImpl(
                                    text = stringResource(R.string.clear_selected_apps),
                                    optionSize = 2,
                                    isSelected = false,
                                    onSelectedIndexChange = {
                                        clearSelectedRequest = true
                                        dismissPopup(showTopPopup)
                                        isTopPopupExpanded.value = false
                                    },
                                    index = 1
                                )
                            }
                        }
                        showTopPopup.value = true
                    }
                    AnimatedVisibility(
                        queryString.isBlank()
                    ) {
                        IconButton(
                            modifier = Modifier
                                .padding(end = 21.dp)
                                .size(40.dp),
                            onClick = {
                                isTopPopupExpanded.value = true
                            }
                        ) {
                            Icon(
                                imageVector = MiuixIcons.ImmersionMore,
                                contentDescription = "Menu"
                            )
                        }
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
                                    sp.edit {
                                        checkedListKey?.let { key ->
                                            putStringSet(key, currentCheckedList)
                                        }
                                        commit()
                                    }
                                    tmpCheckedList.apply {
                                        clear()
                                        addAll(checkedListKey?.let { SafeSP.mSP?.getStringSet(it, emptySet()) } ?: emptySet())
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
                .background(MiuixTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)),
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
                        summary = stringResource(R.string.save_hint),
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
            extraContent?.let { it1 -> it1() }
            if (isLoading) {
                item {
                    SmallTitle(
                        text = stringResource(R.string.loading),
                        modifier = Modifier.padding(top = 6.dp),
                        textColor = MiuixTheme.colorScheme.onBackgroundVariant
                    )
                }
            } else {
                itemsIndexed(appInfoFilter, key = { index, item ->
                    item.packageName + item.isChecked + index + appInfoFilter.size
                }) { index, item ->
                    val topCornerRadius = if (index == 0) CardDefaults.ConorRadius else 0.dp
                    val bottomCornerRadius = if (index == appInfoFilter.size - 1) CardDefaults.ConorRadius else 0.dp
                    SpliceCard(
                        topCornerRadius,
                        bottomCornerRadius
                    ) {
                        SwitchPreference(
                            icon = ImageIcon(
                                iconBitmap = item.icon.toBitmap().asImageBitmap(),
                                iconSize = IconSize.App
                            ),
                            title = item.appName,
                            summary = item.packageName,
                            checked = item.isChecked
                        )
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
fun SpliceCard(
    topCornerRadius: Dp,
    bottomCornerRadius: Dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = remember {
        if (topCornerRadius == 0.dp && bottomCornerRadius == 0.dp)
            RectangleShape
        else
            SmoothRoundedCornerShape(
                topStart = topCornerRadius, topEnd = topCornerRadius,
                bottomStart = bottomCornerRadius, bottomEnd = bottomCornerRadius
            )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = if (topCornerRadius != 0.dp) 6.dp else 0.dp,
                bottom = if (bottomCornerRadius != 0.dp) 6.dp else 0.dp,
                start = 12.dp,
                end = 12.dp
            ),
        shape = shape,
        color = CardDefaults.DefaultColor(),
    ) {
        Column(
            modifier = Modifier.padding(CardDefaults.InsideMargin),
            content = content
        )
    }
}

data class MyAppInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    // 该 isChecked 用于存储应用是否被勾选, 0 为未勾选, 1 为勾选
    var isChecked: MutableState<Boolean>,
    val isSystemApp: Boolean
)