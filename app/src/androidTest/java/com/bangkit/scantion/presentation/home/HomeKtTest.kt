package com.bangkit.scantion.presentation.home

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.bangkit.scantion.R
import com.bangkit.scantion.data.DataDummy
import com.bangkit.scantion.data.database.SkinExamsDatabase
import com.bangkit.scantion.model.News
import com.bangkit.scantion.model.SkinCase
import com.bangkit.scantion.model.UserLog
import com.bangkit.scantion.util.Constants.orPlaceHolderList
import com.bangkit.scantion.util.getDayFormat
import com.bangkit.scantion.viewmodel.ExaminationViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test


class HomeKtTest{
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var examinationViewModel:ExaminationViewModel

    @Test
    fun verify_empty_history_examination_in_home(){
        composeTestRule.setContent {
            examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao())
            examinationViewModel.clearAllSkinExam()
            HomeTest(examinationViewModel)
        }
        composeTestRule.onNodeWithTag("infoHistoryAreEmpty").assertIsDisplayed()
    }

    @Test
    fun verify_recent_two_history_examination_in_home(){
        composeTestRule.setContent {
            examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao())
            for (skinCase in DataDummy.generateDummyCaseEntity()){
                examinationViewModel.addSkinExam(skinCase)
            }
            HomeTest(examinationViewModel)
        }

        val listSkinCaseExpected = examinationViewModel.skinExams.value.orPlaceHolderList("202010370311197")

        val expectedFirstLastSkinCase = listSkinCaseExpected[0]
        val expectedSecondLastSkinCase = listSkinCaseExpected[1]
        val expectedNotTwoLastSkinCase = listSkinCaseExpected[3]

        composeTestRule.onNodeWithTag("infoHistoryIsDisplayed${expectedFirstLastSkinCase.id}").assertIsDisplayed()
        composeTestRule.onNodeWithTag("infoHistoryIsDisplayed${expectedSecondLastSkinCase.id}").assertIsDisplayed()
        composeTestRule.onNodeWithTag("infoHistoryIsDisplayed${expectedNotTwoLastSkinCase.id}").assertDoesNotExist()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeTest(
    examinationViewModel: ExaminationViewModel = hiltViewModel()
) {
    val userLog = UserLog()

    val newsList = News.getData()

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    modifier = Modifier.testTag("titleHome"),
                    text = "Halo, ${userLog.name}",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
        Card(
            shape = MaterialTheme.shapes.large, modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(start = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Ingin periksa kulit anda?",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Periksa sekarang",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Button(onClick = {
                        }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.width(86.dp)
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_examination),
                                    contentDescription = "Icon Add",
                                    modifier = Modifier
                                        .width(30.dp)
                                        .aspectRatio(1f)
                                )
                                Text(text = "Periksa")
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.img_card_examination),
                        contentDescription = "image examination illustration"
                    )
                }
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)){
            CarouselNewsTest(newsList = newsList)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Riwayat Pemeriksaan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Lihat semua",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {

                })
        }
        Column(modifier = Modifier.fillMaxSize()) {
            LastExamTest(examinationViewModel, "202010370311197")
        }
    }
}
@Composable
fun LastExamTest(examinationViewModel: ExaminationViewModel, userId: String) {
    val skinExams = examinationViewModel.skinExams.observeAsState()
    val total = if (skinExams.value.orPlaceHolderList(userId).size > 2) 2 else skinExams.value.orPlaceHolderList(userId).size
    LastSkinExamsTest(skinCases = skinExams.value.orPlaceHolderList(userId), total)
}

@Composable
fun LastSkinExamsTest(skinCases: List<SkinCase>, total: Int) {
    if (skinCases[0].id == "empty"){
        Column(modifier = Modifier.testTag("infoHistoryAreEmpty")
            .fillMaxWidth()
            .padding(vertical = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = skinCases[0].userId)
            Text(text = skinCases[0].bodyPart)
        }
    } else if (total >= 1){
        for (i in 0 until total){
            SkinCaseListItemTest(
                skinCases[i]
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CarouselNewsTest(
    newsList: List<News>
) {
    val carouselState: PagerState = rememberPagerState()

    val autoSlideEnabled = remember { mutableStateOf(true) }
    val autoSlideInterval = 4000L

    LaunchedEffect(autoSlideEnabled.value) {
        if (autoSlideEnabled.value) {
            while (true) {
                delay(autoSlideInterval)
                carouselState.animateScrollToPage((carouselState.currentPage + 1) % newsList.size)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        HorizontalPager(
            state = carouselState,
            count = newsList.size,
            contentPadding = PaddingValues(horizontal = 65.dp),
            modifier = Modifier.height(180.dp)
        ) { page ->
            Card(shape = MaterialTheme.shapes.large,
                modifier = Modifier.clickable {
                }) {
                ImageContainer(newsList, page)
            }
        }
        DotsIndicator(totalDots = newsList.size, selectedIndex = carouselState.currentPage)
    }
}

@Composable
private fun ImageContainer(newsList: List<News>, page: Int) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(newsList[page].thumb).crossfade(true).scale(
            Scale.FILL).build(),
        contentDescription = "Image News",
    )
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int
) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(), horizontalArrangement = Arrangement.Center
    ) {

        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.primary)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SkinCaseListItemTest(skinCase: SkinCase) {
    return Box(
        modifier = Modifier
            .height(120.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.testTag("infoHistoryIsDisplayed${skinCase.id}")
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .fillMaxWidth()
                .height(120.dp)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = {
                        if (skinCase.id.isNotEmpty()) {
                        }
                    },
                )

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (skinCase.photoUri.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(data = Uri.parse(skinCase.photoUri))
                                .build()
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .fillMaxHeight(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)) {
                    Text(
                        text = if (skinCase.cancerType == "Normal") "Aman" else "Terindikasi",
                        color = if (skinCase.cancerType == "Normal") Color.Green else Color.Red
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = skinCase.bodyPart,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${skinCase.cancerType} ${(skinCase.accuracy * 100).toInt()}%",
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier.testTag("dateCase${skinCase.id}"),
                        text = getDayFormat(skinCase.dateCreated),
                    )
                }
            }

        }
    }
}
