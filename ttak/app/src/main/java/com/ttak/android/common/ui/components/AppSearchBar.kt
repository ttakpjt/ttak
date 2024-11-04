package com.ttak.android.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttak.android.R
import com.ttak.android.common.ui.theme.Grey
import com.ttak.android.common.ui.theme.Black
import com.ttak.android.common.ui.theme.White

@Composable
fun AppSearchBar(
    modifier: Modifier = Modifier,
    icon: Int = R.drawable.ic_magnify,  // 기본 아이콘 설정
    errorMessage: String = "사용자가 존재하지 않습니다.",  // 기본 에러 메시지
    onIconClick: () -> Unit = {}  // 아이콘 클릭 시 동작
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isError by remember { mutableStateOf(false) }  // 경고 문구 표시 여부

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(White, shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 검색 입력 필드
            BasicTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    isError = false  // 텍스트 입력 시 경고 문구 초기화
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black),
                decorationBox = { innerTextField ->
                    if (searchText.text.isEmpty()) {
                        Text(
                            text = "닉네임을 입력해 주세요.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Grey
                        )
                    }
                    innerTextField()
                }
            )

            // 검색 아이콘 버튼
            IconButton(
                onClick = {
                    onIconClick()  // 아이콘 클릭 시 동작 매번 다르니까 이거 수정도 해야 할 듯
                    // 예시 로직: False 반환 시 에러 표시
                    if (/* 검색 결과가 없는 경우 */ false) {
                        isError = true
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "검색",
                    tint = Black
                )
            }
        }

        // 경고 문구
        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}
