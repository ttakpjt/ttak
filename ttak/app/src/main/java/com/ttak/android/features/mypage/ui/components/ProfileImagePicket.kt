package com.ttak.android.features.mypage.ui.components

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ttak.android.R
import com.ttak.android.common.ui.theme.Grey
import com.ttak.android.common.ui.theme.Blue

@Composable
fun ProfileImagePicker(
    profileImageUri: MutableState<Uri?>,
    selectImageLauncher: ActivityResultLauncher<String>
) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(120.dp)
    ) {
        if (profileImageUri.value != null) {
            Image(
                painter = rememberAsyncImagePainter(profileImageUri.value),
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Grey),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Grey)
            )
        }

        IconButton(
            onClick = { selectImageLauncher.launch("image/*") },
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Blue)
        ) {
            Icon(
                painter = painterResource(R.drawable.image),
                contentDescription = "프로필 이미지 선택",
                tint = Color.Black,
            )
        }
    }
}