package com.ttak.android.features.history.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttak.android.R
import com.ttak.android.common.ui.components.AppButton
import com.ttak.android.common.ui.theme.Grey
import com.ttak.android.data.repository.history.HistoryRepositoryImpl
import com.ttak.android.features.history.ui.components.HistoryMessageItem
import com.ttak.android.features.history.ui.components.SystemNotificationCard
import com.ttak.android.features.history.viewmodel.HistoryViewModel
import com.ttak.android.features.history.viewmodel.HistoryViewModelFactory
import com.ttak.android.network.util.ApiConfig
import com.ttak.android.features.auth.viewmodel.MemberViewModel

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(
            repository = HistoryRepositoryImpl(
                api = ApiConfig.createHistoryApi(context = LocalContext.current)
            )
        )
    )
) {

    val weeklyPickCount by viewModel.weeklyPickCount.collectAsState()
    val weeklyWatchingCount by viewModel.weeklyWatchingCount.collectAsState()
    val historyList by viewModel.historyList.collectAsState()
    val memberViewModel: MemberViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SystemNotificationCard(
            notificationText = "주간 딱걸림\n${weeklyPickCount}번",
            watchingCountText = "${weeklyWatchingCount}명의 친구가\n지켜보고 있어요"
        )

        // 빈 리스트일 때, 안내 문구와 이미지 표시
        if (historyList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "잘 하고 있습니다.",
                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.padding(8.dp)
                )

                Text(
                    text = "걸린 기록이 없군요.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
                // 이거 배포할 때 지우셈
//                AppButton(
//                    text = "로그아웃",
//                    onClick = {
//                        memberViewModel.logout()
//                    }
//                )
                // 웃는 아이콘 추가
//                 Image(
//                     painter = painterResource(id = R.drawable.emoticon_cool),
//                     contentDescription = "Empty History",
//                     colorFilter = ColorFilter.tint(Grey),
//                     modifier = Modifier
//                         .padding(8.dp)
//                         .fillMaxWidth(0.5f)
//                         .size(50.dp)
//                 )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                items(historyList) { historyInfo ->
                    HistoryMessageItem(data = historyInfo)
                }
            }
        }
    }
}