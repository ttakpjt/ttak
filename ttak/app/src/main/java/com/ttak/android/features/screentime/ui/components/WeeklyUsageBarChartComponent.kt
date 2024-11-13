package com.ttak.android.features.screentime.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ttak.android.common.ui.theme.Grey
import java.util.*

@Composable
fun WeeklyUsageBarChartComponent(dailyUsageList: List<Int>) {
    // 현재 요일 가져오기 (일요일 = 0, 월요일 = 1, ..., 토요일 = 6)
    val todayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 // Calendar는 일요일이 1부터 시작
    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
    // 최대 사용 시간에 비례하여 막대의 상대적 높이를 계산
    val maxUsage = dailyUsageList.maxOrNull() ?: 1
    val maxHeight = 100.dp // 막대의 최대 높이를 200dp로 제한

    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("이번 주에는...", style = MaterialTheme.typography.labelSmall)

        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                dailyUsageList.forEachIndexed { index, usage ->
                    val barColor = if (index == todayIndex) Color.Red else Grey
                    // 요일 가로축 위에 선 그리기
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Bottom // 아래쪽 축을 기준으로 정렬
                    ) {
                        Box(
                            modifier = Modifier
                                .height(if (maxUsage > 0) (usage * maxHeight.value / maxUsage).dp else 0.dp)
                                .width(30.dp)
                                .background(barColor)
//                            모서리 추가 좀 해주세요.
                        )
//                      가로 선
                        Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                            drawLine(
                                color = Grey,
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                                strokeWidth = 2f
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = daysOfWeek[index],
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}
