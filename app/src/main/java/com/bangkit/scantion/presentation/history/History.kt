package com.bangkit.scantion.presentation.history

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bangkit.scantion.model.SkinCase
import com.bangkit.scantion.model.UserLog
import com.bangkit.scantion.navigation.Graph
import com.bangkit.scantion.navigation.HomeScreen
import com.bangkit.scantion.util.Constants.orPlaceHolderList
import com.bangkit.scantion.util.Resource
import com.bangkit.scantion.util.getDayFormat
import com.bangkit.scantion.viewmodel.ExaminationViewModel
import com.bangkit.scantion.viewmodel.HomeViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    examinationViewModel: ExaminationViewModel = hiltViewModel()
) {

    var userLog = UserLog()

    val lifecycleOwner = LocalLifecycleOwner.current
    val userNotFoundUnit = {
        navController.popBackStack()
        navController.navigate(Graph.AUTHENTICATION)
    }

    try {
        userLog = homeViewModel.userLog.value!!
    } catch (e: Exception){
        homeViewModel.getUser().observe(lifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        userLog = UserLog(
                            result.data.id,
                            result.data.name,
                            result.data.email,
                            result.data.age,
                            result.data.province,
                            result.data.city
                        )
                        homeViewModel.saveUser(userLog)
                        Log.d("user", "Get User success")
                    }

                    is Resource.Error -> {
                        Log.d("user", "Get User Failed")
                        userNotFoundUnit.invoke()
                    }
                }
            } else {
                Log.d("user", "Get User Failed")
                userNotFoundUnit.invoke()
            }
        }
    }

    val skinCaseQuery = remember { mutableStateOf("") }
    val skinExams = examinationViewModel.skinExams.observeAsState()
    val skinCases = skinExams.value.orPlaceHolderList(userLog.id)


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
                    IconButton(onClick = { navController.popBackStack() }) {
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
                    modifier = Modifier
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
                        SkinCaseListItem(
                            skinCase,
                            navController = navController
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SkinCaseListItem(skinCase: SkinCase, navController: NavController) {
    return Box(
        modifier = Modifier
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
                            navController.navigate(HomeScreen.Detail.createRoute(skinCase.id))
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
