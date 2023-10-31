package com.bangkit.scantion.presentation.examination

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import coil.compose.AsyncImage
import com.bangkit.scantion.R
import com.bangkit.scantion.data.database.SkinExamsDatabase
import com.bangkit.scantion.data.firebase.AuthRepositoryImpl
import com.bangkit.scantion.model.CancerType
import com.bangkit.scantion.model.ExaminationItems
import com.bangkit.scantion.model.SkinCase
import com.bangkit.scantion.model.UserLog
import com.bangkit.scantion.navigation.Graph
import com.bangkit.scantion.navigation.HomeScreen
import com.bangkit.scantion.ui.component.ConfirmationDialog
import com.bangkit.scantion.ui.component.ScantionButton
import com.bangkit.scantion.ui.component.TextFieldQuestion
import com.bangkit.scantion.util.DialogDate
import com.bangkit.scantion.util.ImageFileProvider
import com.bangkit.scantion.util.dateToMilliseconds
import com.bangkit.scantion.util.saveToPdf
import com.bangkit.scantion.viewmodel.AuthViewModel
import com.bangkit.scantion.viewmodel.ExaminationViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class ExaminationKtTest{
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: TestNavHostController
    private val viewModel = AuthViewModel(AuthRepositoryImpl(FirebaseAuth.getInstance()))
    @Before
    fun setUpNavHost(){
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }
    @Test
    fun verify_result_and_generate_pdf(){
        composeTestRule.setContent {
            viewModel.login("dssdjohnyn@example.com", "password").observeForever {  }
            ExaminationTesting(
                page = 2,
                howLongTest = "10 September 2021",
                bodyPartTest = "tangan",
                symptomTest = "gatal",
                photoUriTest = "https://storage.googleapis.com/kaggle-datasets-images/2035877/3376422/eefe34f4ff71025fced98dfcf6979b39/dataset-card.jpg?t=2022-03-29-11-56-39",
                viewModel = viewModel,
                examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao()))
        }
        // Perform actions and assertions within a @Composable function
        composeTestRule.onNodeWithTag("leftButton").assertIsEnabled().performClick()
        Thread.sleep(1000)
    }

    @Test
    fun verify_button_process_enable_and_disable_based_on_if_all_question_answered(){
        composeTestRule.setContent {
            viewModel.login("dssdjohnyn@example.com", "password").observeForever {  }
            ExaminationTesting(
                page = 1,
                howLongTest = "10 September 2021",
                photoUriTest = "https://storage.googleapis.com/kaggle-datasets-images/2035877/3376422/eefe34f4ff71025fced98dfcf6979b39/dataset-card.jpg?t=2022-03-29-11-56-39",
                viewModel = viewModel,
                examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao()))
        }
        composeTestRule.onNodeWithTag("rightButton").assertIsNotEnabled()
        // Perform actions and assertions within a @Composable function
        composeTestRule.onNodeWithTag("bodyPartField").performTextInput("Tangan")
        composeTestRule.onNodeWithTag("symptomField").performTextInput("Gatal")
        Thread.sleep(1000)
        composeTestRule.onNodeWithTag("rightButton").assertIsEnabled()

    }

    @Test
    fun verify_button_next_enable_if_image_not_null_in_first_page(){
        composeTestRule.setContent {
            viewModel.login("dssdjohnyn@example.com", "password").observeForever {  }
            ExaminationTesting(
                photoUriTest = "https://storage.googleapis.com/kaggle-datasets-images/2035877/3376422/eefe34f4ff71025fced98dfcf6979b39/dataset-card.jpg?t=2022-03-29-11-56-39",
                viewModel = viewModel,
                examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao()),
                howLongTest = "10 September 2021"
            )
        }
        Thread.sleep(1000)
        // Perform actions and assertions within a @Composable function
        composeTestRule.onNodeWithTag("rightButton").assertIsEnabled()
        Thread.sleep(1000)
        composeTestRule.onNodeWithTag("rightButton").performClick()
        Thread.sleep(1000)
    }

    @Test
    fun verify_Button_prev_not_visible_in_first_page(){
        composeTestRule.setContent {
            viewModel.login("dssdjohnyn@example.com", "password").observeForever {  }
            ExaminationTesting(
                viewModel = viewModel,
                examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao()),
                howLongTest = "10 September 2021"
            )
        }
        Thread.sleep(1000)
        // Perform actions and assertions within a @Composable function
        composeTestRule.onNodeWithTag("leftButton").assertDoesNotExist()
    }

    @Test
    fun verify_Button_next_disable_if_image_null_in_first_page(){
        composeTestRule.setContent {
            viewModel.login("dssdjohnyn@example.com", "password").observeForever {  }
            ExaminationTesting(
                viewModel = viewModel,
                examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao()),
                howLongTest = "10 September 2021"
            )
        }
        Thread.sleep(1000)
        // Perform actions and assertions within a @Composable function
        composeTestRule.onNodeWithTag("rightButton").assertIsNotEnabled()
    }
}


@SuppressLint("StateFlowValueCalledInComposition", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExaminationTesting(
    page: Int = 0,
    howLongTest: String = "",
    bodyPartTest: String = "",
    symptomTest: String = "",
    photoUriTest: String = "",
    viewModel: AuthViewModel = hiltViewModel(),
    examinationViewModel: ExaminationViewModel = hiltViewModel(),
) {
    val db = Firebase.firestore
    val context = LocalContext.current

    var userLog = UserLog()
    val user = viewModel.currentUser

    user.let {
        if (it != null) {
            userLog = UserLog(it.uid, it.displayName.toString(), it.email.toString())
        } else {
        }
    }

    val items = listOf(
        ExaminationItems(
            "Foto",
            "Tambahkan foto kulit anda yang ingin diperiksa.",
            ImageVector.vectorResource(id = R.drawable.ic_add_photo)
        ),
        ExaminationItems(
            "Pertanyaan",
            "Jawablah beberapa pertanyaan berikut.",
            ImageVector.vectorResource(id = R.drawable.ic_question)
        ),
        ExaminationItems(
            "Hasil",
            "Ini lah hasil dari pemeriksaan masalah kulit anda.",
            ImageVector.vectorResource(id = R.drawable.ic_result)
        )
    )

    val scope = rememberCoroutineScope()
    val pageState = rememberPagerState(page)

    var bodyPart by rememberSaveable { mutableStateOf(bodyPartTest) }
    var howLong by rememberSaveable { mutableStateOf(howLongTest) }
    var symptom by rememberSaveable { mutableStateOf(symptomTest) }
    var photoUri by rememberSaveable { mutableStateOf<Uri?>(if (photoUriTest != "") photoUriTest.toUri() else null) }
    var hasImage by rememberSaveable { mutableStateOf(photoUriTest != "") }
    var isProcessDone by rememberSaveable { mutableStateOf(bodyPartTest != "") }

    val isLoading = rememberSaveable { mutableStateOf(false) }

    val isQuestionAnswered =
        bodyPart.isNotEmpty() && howLong.isNotEmpty() && symptom.isNotEmpty()

    val skinCase by rememberSaveable { mutableStateOf(SkinCase(
        id = "case-id-${UUID.randomUUID()}",
        userId = userLog.id,
        photoUri = photoUri.toString(),
        bodyPart = bodyPart,
        howLong = howLong,
        symptom = symptom,
        cancerType = "melanoma",
        accuracy =  0.78f
    )) }

    val showDialog = rememberSaveable { mutableStateOf(false) }
    val backCallbackEnabled = rememberSaveable { mutableStateOf(false) }

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isProcessDone) {
                    showDialog.value = true
                } else {
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    DisposableEffect(dispatcher, backCallbackEnabled.value, lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (backCallbackEnabled.value) {
                        dispatcher?.addCallback(backCallback)
                    }
                }

                Lifecycle.Event.ON_STOP -> backCallback.remove()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            backCallback.remove()
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    ConfirmationDialog(
        showDialog = showDialog,
        title = "Apakah anda yakin keluar dari halaman pemeriksaan?",
        desc = "Data yang sudah anda masukan akan dihapus",
        confirmText = "Keluar",
        dismissText = "Batal",
        onConfirm = {
            ImageFileProvider.deleteImageUnused(context)
        }
    )

    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = { focusManager.clearFocus() }),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Pemeriksaan",
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (!hasImage || isProcessDone) {
                                } else {
                                    showDialog.value = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close, contentDescription = "close"
                            )
                        }
                    },
                )
                TopSection(
                    items = items,
                    index = pageState.currentPage
                )
            }
        },
        bottomBar = {
            BottomAppBar {
                BottomSection(
                    userLog,
                    context,
                    hasImage,
                    isQuestionAnswered,
                    skinCase,
                    items.size,
                    pageState.currentPage,
                    isLoading.value,
                    onNextClick = {
                        if (pageState.currentPage < items.size - 1) scope.launch {
                            pageState.animateScrollToPage(pageState.currentPage + 1)
                        }
                    },
                    onPrevClick = {
                        if (pageState.currentPage + 1 > 1) scope.launch {
                            pageState.animateScrollToPage(pageState.currentPage - 1)
                        }
                    }
                ) {
                    isProcessDone = true
                }
            }
        }
    ) {innerPadding ->
        if(isLoading.value){
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding,
                content = {
                    item{
                        HorizontalPager(
                            count = items.size,
                            state = pageState,
                            modifier = Modifier
                                .fillMaxWidth(),
                            userScrollEnabled = false
                        ) { page ->
                            ExaminationPage(
                                context,
                                userLog,
                                page,
                                bodyPart,
                                howLong,
                                symptom,
                                photoUri,
                                hasImage,
                                isProcessDone,
                                onBodyPartChange = { bodyPart = it },
                                onSymptomChange = { symptom = it },
                                onHowLongChange = { howLong = it },
                                onPhotoUriChange = { photoUri = it },
                                onHasImageChange = { hasImage = it },
                                backCallbackEnabled,
                                skinCase
                            )
                        }
                    }
                })
        }
    }
}

@Composable
fun ExaminationPage(
    context: Context,
    userLog: UserLog,
    page: Int,
    bodyPart: String,
    symptom: String,
    howLong: String,
    photoUri: Uri?,
    hasImage: Boolean,
    isProcessDone: Boolean = false,
    onBodyPartChange: (String) -> Unit,
    onSymptomChange: (String) -> Unit,
    onHowLongChange: (String) -> Unit,
    onPhotoUriChange: (Uri) -> Unit,
    onHasImageChange: (Boolean) -> Unit,
    backCallbackEnabled: MutableState<Boolean>,
    skinCase: SkinCase?
) {
    when (page) {
        0 -> {
            AddPhotoPageTesting(
                context,
                photoUri,
                hasImage,
                onPhotoUriChange,
                onHasImageChange,
                backCallbackEnabled
            )
        }

        1 -> {
            QuestionPageTesting(
                bodyPart,
                howLong,
                symptom,
                onBodyPartChange,
                onSymptomChange,
                onHowLongChange
            )
        }

        2 -> {
            if (isProcessDone && skinCase != null) {
                ResultPageTesting(
                    userLog = userLog,
                    skinCase = skinCase
                )
            }
        }
    }
}

@Composable
fun TopSection(
    items: List<ExaminationItems>,
    index: Int,
) {
    val size = items.size
    val progress =
        if (index == size - 1) (size + 1).toFloat() else (index.toFloat() + 1) / (items.size + 1)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                LinearProgressIndicator(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {}
                        .fillMaxWidth(),
                    progress = animatedProgress,
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            // Indicators
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(50.dp),
            ) {
                for (i in 0 until size) {
                    Indicator(i, index, items[i])
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = items[index].hint,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun Indicator(i: Int, index: Int, items: ExaminationItems) {
    val isDone = index >= i
    val boxColor =
        if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val iconColor =
        if (isDone) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier.height(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(60.dp)
                .background(color = boxColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = items.icon,
                contentDescription = "icon indicator",
                tint = iconColor,
                modifier = Modifier
                    .width(32.dp)
                    .aspectRatio(1f)
            )
        }
        Text(
            text = items.pageName,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun BottomSection(
    userLog: UserLog,
    context: Context,
    hasImage: Boolean,
    isQuestionAnswered: Boolean,
    skinCase: SkinCase?,
    size: Int,
    index: Int,
    isLoading: Boolean,
    onNextClick: () -> Unit = {},
    onPrevClick: () -> Unit = {},
    onProcessClick: () -> Unit = {},
) {
    val isOnResult = index == size - 1
    val isLastInput = index == size - 2

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            // Previous Button
            Row(modifier = Modifier.fillMaxWidth(.5f), horizontalArrangement = Arrangement.Start) {
                AnimatedVisibility(index > 0, enter = fadeIn(), exit = fadeOut()) {
                    ScantionButton(
                        modifier = Modifier.testTag("leftButton")
                            .fillMaxWidth()
                            .padding(end = 10.dp),
                        onClick = {
                            if (index > 0) {
                                if (!isOnResult) {
                                    onPrevClick.invoke()
                                } else {
                                    if (skinCase != null) {
                                        saveToPdf(context, skinCase, userLog.name)
                                    }
                                }
                            }
                        },
                        text = if (isOnResult) "Simpan PDF" else "Kembali",
                        textStyle = MaterialTheme.typography.bodySmall,
                        outlineButton = true,
                        iconStart = !isOnResult,
                        iconEnd = isOnResult,
                        icon = if (isOnResult) Icons.Outlined.ArrowDropDown else Icons.Outlined.KeyboardArrowLeft,
                        enabled = !isLoading
                    )
                }
            }

            val enabledNext =
                when (index) {
                    0 -> hasImage
                    1 -> isQuestionAnswered
                    else -> false
                }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                ScantionButton(
                    modifier = Modifier.testTag("rightButton")
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    enabled = if (isOnResult) true else enabledNext && !isLoading,
                    onClick = {
                        if (isLastInput) onProcessClick.invoke()
                    },
                    text = if (isOnResult) "Selesai" else if (isLastInput) "Proses" else "Selanjutnya",
                    textStyle = MaterialTheme.typography.bodySmall,
                    iconEnd = true,
                    icon = Icons.Outlined.KeyboardArrowRight
                )
            }
        }
    }
}

@Composable
fun AddPhotoPageTesting(
    context: Context,
    photoUri: Uri?,
    hasImage: Boolean,
    onPhotoUriChange: (Uri) -> Unit,
    onHasImageChange: (Boolean) -> Unit,
    backCallbackEnabled: MutableState<Boolean>
) {
    var tempUri: Uri? = null
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            onPhotoUriChange.invoke(tempUri!!)
            onHasImageChange.invoke(true)
            backCallbackEnabled.value = true
        } else if (hasImage) {
            backCallbackEnabled.value = true
        }

    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uriResult ->
        if (photoUri != uriResult && uriResult != null) {
            onPhotoUriChange.invoke(uriResult)
            onHasImageChange.invoke(true)
            backCallbackEnabled.value = true
        } else if (hasImage) {
            backCallbackEnabled.value = true
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 20.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = if (hasImage) photoUri else null,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentScale = ContentScale.Crop,

                )
            if (!hasImage) {
                Text(
                    text = "Kosong",
                    style = TextStyle(color = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(.5f)) {
                ScantionButton(
                    modifier = Modifier.fillMaxWidth().padding(end = 5.dp),
                    onClick = {
                        tempUri = ImageFileProvider.getImageUri(context)
                        takePictureLauncher.launch(tempUri)
                    },
                    text = "Kamera",
                    outlineButton = hasImage
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                ScantionButton(
                    modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                    onClick = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        ) },
                    text = "Galeri",
                    outlineButton = hasImage
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionPageTesting(
    bodyPart: String,
    symptom: String,
    howLong: String,
    onBodyPartChange: (String) -> Unit,
    onSymptomChange: (String) -> Unit,
    onHowLongChange: (String) -> Unit,
) {
    val bodyPartFocusRequester = remember { FocusRequester() }
    val symptomFocusRequester = remember { FocusRequester() }

    val snackScope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (howLong.isNotEmpty()) dateToMilliseconds(howLong) else null
    )
    val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }

    val focusManager = LocalFocusManager.current
    DialogDate(snackScope, openDialog, datePickerState, confirmEnabled, onHowLongChange)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TextFieldQuestion(
            modifier = Modifier.testTag("bodyPartField")
                .fillMaxWidth()
                .focusRequester(bodyPartFocusRequester),
            text = "Bagian kulit mana yang diperiksa?",
            placeholder = "Bagian Kulit (misal: Tangan)",
            value = bodyPart,
            onChangeValue = onBodyPartChange,
            nextFocusRequester = symptomFocusRequester
        )
        TextFieldQuestion(modifier = Modifier.testTag("symptomField")
            .fillMaxWidth()
            .focusRequester(symptomFocusRequester),
            text = "Apa saja gejala kulit yang anda alami?",
            placeholder = "Gejala (misal: gatal, panas, kering)",
            value = symptom,
            onChangeValue = onSymptomChange,
            isLast = true,
            performAction = { focusManager.clearFocus() })

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Sejak kapan masalah kulit ini muncul?")
            OutlinedButton(
                modifier = Modifier.testTag("howLongField")
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                onClick = {
                    focusManager.clearFocus()
                    openDialog.value = true
                }, shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = howLong.ifEmpty { "Pilih tanggal" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(imageVector = Icons.Outlined.DateRange, contentDescription = "datePicker")
                }
            }
        }
    }
}

@Composable
fun ResultPageTesting(navController: NavHostController = rememberNavController(), userLog: UserLog, skinCase: SkinCase, isFromDetail: Boolean = false) {
    val name = userLog.name
    val uriHandler = LocalUriHandler.current
    val hospitalParamSearch = "rumah+sakit"

    val cancerTypes = CancerType.getData()
    var cancerType: CancerType? = null

    if (skinCase.cancerType in CancerType.listKey){
        cancerType = cancerTypes.getValue(skinCase.cancerType)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ColumnPartResult {
            TitleTextResult(text = "Detail Pemeriksaan")
            RowSpaceBetweenTwoText(
                modifier = Modifier.fillMaxWidth(),
                textFirst = skinCase.id,
                textSecond = ""
            )
            RowSpaceBetweenTwoText(
                modifier = Modifier.fillMaxWidth(),
                textFirst = "Nama",
                textSecond = name
            )
            RowSpaceBetweenTwoText(
                modifier = Modifier.fillMaxWidth(),
                textFirst = "Tanggal Pemeriksaan",
                textSecond = skinCase.dateCreated
            )
        }
        ResultSpacer()
        ColumnPartResult {
            TitleTextResult(text = "Hasil")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(.5f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = skinCase.photoUri.ifEmpty { null },
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(.8f)
                            .aspectRatio(1f)
                            .clip(shape = RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ){
                        PercentageCircleBox(accuracy = skinCase.accuracy, 110.dp, isFromDetail = isFromDetail)
                        Text(
                            text = skinCase.cancerType,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        if (cancerType != null){
            ResultSpacer()
            ColumnPartResult{
                TitleTextResult(text = "Penjelasan tentang ${cancerType.displayName}")
                Column {
                    Text(text = cancerType.desc, maxLines = 4)
                    Text(text = "...", maxLines = 1)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .alpha(0.3f)
                        .background(color = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Spacer(modifier = Modifier.fillMaxSize())
                }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text(
                        text = "Baca Selengkapnya",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("resultExplanation").clickable {
                            navController.navigate(HomeScreen.Explanation.createRoute(cancerType.displayName))
                        }
                    )
                }
            }
        }
        ResultSpacer()
        ColumnPartResult {
            TitleTextResult(text = "Keterangan Pasien")
            RowSpaceBetweenTwoText(
                modifier = Modifier.fillMaxWidth(),
                textFirst = "Bagian kulit",
                textSecond = skinCase.bodyPart,
                oneThirdFirst = true
            )
            RowSpaceBetweenTwoText(
                modifier = Modifier.fillMaxWidth(),
                textFirst = "Sejak",
                textSecond = skinCase.howLong,
                oneThirdFirst = true
            )
            RowSpaceBetweenTwoText(
                modifier = Modifier.fillMaxWidth(),
                textFirst = "Gejala",
                textSecond = skinCase.symptom,
                oneThirdFirst = true
            )
        }
        ResultSpacer()
        ColumnPartResult {
            Text(
                text = "Ingin memastikan pemeriksaan",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Cari rumah sakit",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    uriHandler.openUri("https://www.google.co.id/maps/search/$hospitalParamSearch/")
                }
            )
        }
    }
}

@Composable
fun PercentageCircleBox(accuracy: Float, circleSize: Dp, strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth, isFromDetail: Boolean = false) {
    var animationProgress by rememberSaveable { mutableStateOf(false) }
    val animationDuration = 2500

    val currentPercentage by animateFloatAsState(
        targetValue = if(animationProgress) accuracy else 0f,
        animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
        label = ""
    )

    LaunchedEffect(key1 = true){
        delay(500)
        animationProgress = true
    }

    val percentage = if (isFromDetail) accuracy else currentPercentage

    Box(
        modifier = Modifier
            .size(circleSize)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = percentage,
            strokeWidth = strokeWidth,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "${(percentage * 100).toInt()}%",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ResultSpacer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .alpha(0.3f)
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Spacer(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun ColumnPartResult(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        content()
    }
}

@Composable
fun RowSpaceBetweenTwoText(
    modifier: Modifier,
    textFirst: String,
    textSecond: String,
    oneThirdFirst: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (oneThirdFirst) Arrangement.Start else Arrangement.SpaceBetween
    ) {
        Text(
            text = textFirst,
            style = MaterialTheme.typography.titleSmall,
            modifier = if (oneThirdFirst) Modifier.fillMaxWidth(.35f) else Modifier
        )
        Text(
            text = textSecond,
            style = MaterialTheme.typography.titleSmall,
            modifier = if (oneThirdFirst) Modifier.fillMaxWidth() else Modifier
        )
    }
}

@Composable
fun TitleTextResult(
    text: String
){
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}