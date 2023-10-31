package com.bangkit.scantion.presentation.detail

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bangkit.scantion.data.DataDummy
import com.bangkit.scantion.data.database.SkinExamsDatabase
import com.bangkit.scantion.model.CancerType
import com.bangkit.scantion.model.SkinCase
import com.bangkit.scantion.model.UserLog
import com.bangkit.scantion.navigation.HomeScreen
import com.bangkit.scantion.ui.component.ConfirmationDialog
import com.bangkit.scantion.ui.component.ScantionButton
import com.bangkit.scantion.util.Constants.orPlaceHolderList
import com.bangkit.scantion.util.saveToPdf
import com.bangkit.scantion.viewmodel.ExaminationViewModel
import kotlinx.coroutines.delay
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class DetailKtTest{
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var examinationViewModel:ExaminationViewModel

    @Test
    fun verify_display_detail_examination_history(){
        composeTestRule.setContent {
            examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao())
            examinationViewModel.clearAllSkinExam()
            for (skinCase in DataDummy.generateDummyCaseEntity()){
                examinationViewModel.addSkinExam(skinCase)
            }
            DetailTesting(examinationViewModel)
        }

        val listSkinCaseExpected = examinationViewModel.skinExams.value.orPlaceHolderList("202010370311197")
        val expectedSkinCase = listSkinCaseExpected[0]

        composeTestRule.onNodeWithText(expectedSkinCase.id).assertExists()
        composeTestRule.onNodeWithText(expectedSkinCase.cancerType).assertExists()
        composeTestRule.onNodeWithText(expectedSkinCase.dateCreated).assertExists()
        composeTestRule.onNodeWithText("Simpan PDF").assertExists()
        composeTestRule.onNodeWithText("Hapus").assertExists()
    }

    @Test
    fun verify_generate_pdf_result_in_detail_history(){
        composeTestRule.setContent {
            examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao())
            examinationViewModel.clearAllSkinExam()
            for (skinCase in DataDummy.generateDummyCaseEntity()){
                examinationViewModel.addSkinExam(skinCase)
            }
            DetailTesting(examinationViewModel)
        }
        composeTestRule.onNodeWithText("Simpan PDF").assertExists().performClick()
    }

    @Test
    fun verify_delete_skin_case_history(){
        composeTestRule.setContent {
            examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao())
            examinationViewModel.clearAllSkinExam()
            for (skinCase in DataDummy.generateDummyCaseEntity()){
                examinationViewModel.addSkinExam(skinCase)
            }
            DetailTesting(examinationViewModel)
        }
        composeTestRule.onNodeWithTag("rightButtonDelete").performClick()
        composeTestRule.onNodeWithText("Yakin Hapus").assertExists().performClick()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DetailTesting(
    examinationViewModel: ExaminationViewModel = hiltViewModel()
) {
    val userLog = UserLog()

    val listSkinCase = examinationViewModel.skinExams.observeAsState()
    val skinCase = listSkinCase.value.orPlaceHolderList("202010370311197")[0]


    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Pemeriksaan",
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = "back"
                        )
                    }
                },
            )
        }, bottomBar = {
            BottomAppBar {
                BottomSection(
                    skinCase = skinCase,
                    userLog = userLog,
                    context = context,
                    examinationViewModel
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            content = {
                item {
                    ResultPageTesting(
                        userLog = userLog,
                        skinCase = skinCase,
                        isFromDetail = true
                    )
                }
            })
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.9f)
        ) {

        }

    }
}

@Composable
fun BottomSection(
    skinCase: SkinCase,
    userLog: UserLog,
    context: Context,
    examinationViewModel: ExaminationViewModel
) {
    val showDialog = rememberSaveable { mutableStateOf(false) }

    ConfirmationDialog(
        showDialog = showDialog,
        title = "Apakah anda yakin ingin menghapus riwayat ini?",
        desc = "Data yang dihapus tidak dapat dikembalikan",
        confirmText = "Yakin Hapus",
        dismissText = "Batal",
        redAlert = true,
        onConfirm = {
            examinationViewModel.deleteSkinExam(skinCase)

        }
    )

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
                ScantionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp),
                    onClick = {
                        saveToPdf(context, skinCase, userLog.name)
                    },
                    text = "Simpan PDF",
                    textStyle = MaterialTheme.typography.bodySmall,
                    iconEnd = true,
                    icon = Icons.Outlined.ArrowDropDown,
                    outlineButton = true
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                ScantionButton(
                    modifier = Modifier.testTag("rightButtonDelete")
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    onClick = { showDialog.value = true },
                    text = "Hapus",
                    textStyle = MaterialTheme.typography.bodySmall,
                    iconEnd = true,
                    icon = Icons.Outlined.KeyboardArrowRight,
                    isDeleteButton = true
                )
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
                        modifier = Modifier
                            .testTag("resultExplanation")
                            .clickable {
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