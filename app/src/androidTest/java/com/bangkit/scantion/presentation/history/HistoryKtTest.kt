package com.bangkit.scantion.presentation.history

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bangkit.scantion.data.DataDummy
import com.bangkit.scantion.data.database.SkinExamsDatabase
import com.bangkit.scantion.model.SkinCase
import com.bangkit.scantion.model.UserLog
import com.bangkit.scantion.util.Constants.orPlaceHolderList
import com.bangkit.scantion.util.getDayFormat
import com.bangkit.scantion.viewmodel.ExaminationViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class HistoryKtTest{
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var examinationViewModel:ExaminationViewModel

    @Test
    fun verify_empty_history_examination(){
        composeTestRule.setContent {
            examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao())
            examinationViewModel.clearAllSkinExam()
            HistoryTest(examinationViewModel)
        }
        composeTestRule.onNodeWithTag("infoHistoryAreEmpty").assertIsDisplayed()
    }

    @Test
    fun verify_not_empty_history_examination(){
        composeTestRule.setContent {
            examinationViewModel = ExaminationViewModel(SkinExamsDatabase.getInstance(LocalContext.current).skinExamsDao())
            for (skinCase in DataDummy.generateDummyCaseEntity()){
                examinationViewModel.addSkinExam(skinCase)
            }
            HistoryTest(examinationViewModel)
        }

        val listSkinCaseExpected = examinationViewModel.skinExams.value.orPlaceHolderList("202010370311197")

        for (i in 0 .. 3) {
            composeTestRule.onNodeWithTag("infoHistoryIsDisplayed${listSkinCaseExpected[i].id}").assertIsDisplayed()
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTest(
    examinationViewModel: ExaminationViewModel = hiltViewModel()
) {
    var userLog = UserLog()

    val skinCaseQuery = remember { mutableStateOf("") }
    val skinExams = examinationViewModel.skinExams.observeAsState()
    val skinCases = skinExams.value.orPlaceHolderList("202010370311197")


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daftar Riwayat Pemeriksaan",
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
        },
        content = {innerPadding ->
            if (skinCases[0].id == "empty") {
                Column(
                    modifier = Modifier.testTag("infoHistoryAreEmpty")
                        .fillMaxWidth()
                        .padding(vertical = 200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = skinCases[0].userId)
                    Text(text = skinCases[0].bodyPart)
                }
            } else {
                LazyColumn (
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    val queriedSkinExams = if (skinCaseQuery.value.isEmpty()) {
                        skinCases
                    } else {
                        skinCases.filter { it.id.contains(skinCaseQuery.value) || it.bodyPart.contains(skinCaseQuery.value) }
                    }
                    itemsIndexed(queriedSkinExams) { _, skinCase ->
                        SkinCaseListItemTest(
                            skinCase
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SkinCaseListItemTest(skinCase: SkinCase) {
    return Box(
        modifier = Modifier.testTag("infoHistoryIsDisplayed${skinCase.id}")
            .height(120.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
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
                        text = getDayFormat(skinCase.dateCreated),
                    )
                }
            }

        }
    }
}
