import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ttak.android.domain.model.FriendStory

@Composable
fun MessageDialog(
    friendStory: FriendStory,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var message by remember { mutableStateOf("") }

    Log.d("MessageDialog", "Dialog opened for friend: ${friendStory.name}")

    AlertDialog(
        onDismissRequest = {
            Log.d("MessageDialog", "Dialog dismissed")
            onDismiss()
        },
        modifier = Modifier.background(
            color = Color(0xFF2F2F32),
            shape = RoundedCornerShape(16.dp)
        ),
        containerColor = Color(0xFF2F2F32),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
//                IconButton(
//                    onClick = {
//                        Log.d("MessageDialog", "Back button clicked")
//                        onDismiss()
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBack,
//                        contentDescription = "뒤로가기",
//                        tint = Color.White
//                    )
//                }
//                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = friendStory.name,
                        style = MaterialTheme.typography.titleLarge,  // 더 큰 글씨체로 변경
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "님에게",
                        style = MaterialTheme.typography.bodySmall,  // 더 작은 글씨체로 변경
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

            }
        },
        text = {
            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                    Log.d("MessageDialog", "Message input: $it")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        color = Color(0xFFF5F378),
                        shape = RoundedCornerShape(8.dp)
                    ),
                placeholder = { Text("메시지를 입력하세요", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFF5F378),
                    unfocusedContainerColor = Color(0xFFF5F378)
                )
            )
        },
        confirmButton = {
            // 버튼 부분 수정
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        Log.d("MessageDialog", "Cancel button clicked")
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB2B2B4)
                    )
                ) {
                    Text(
                        text = "취소하기",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        if (message.isNotBlank()) {
                            Log.d("MessageDialog", "Send button clicked. Message: $message")
                            onSend(message)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = message.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7FEC93),
                        disabledContainerColor = Color(0xFF7FEC93).copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "보내기",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}