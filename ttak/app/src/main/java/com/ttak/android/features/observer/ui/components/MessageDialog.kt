import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ttak.android.domain.model.FriendStory

@Composable
fun MessageDialog(
    friendStory: FriendStory,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var message by remember { mutableStateOf("") }

    // 각 버튼을 위한 interactionSource 생성
    val cancelInteractionSource = remember { MutableInteractionSource() }
    val sendInteractionSource = remember { MutableInteractionSource() }

    // 호버 상태 추적
    val isCancelHovered by cancelInteractionSource.collectIsHoveredAsState()
    val isSendHovered by sendInteractionSource.collectIsHoveredAsState()

    AlertDialog(
        onDismissRequest = {
            Log.d("MessageDialog", "Dialog dismissed")
            onDismiss()
        },
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            ),
        containerColor = Color.Transparent,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = null,
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .offset(x = (-20).dp, y = (-50).dp)
                        .background(
                            Color(0x33A855F7),
                            RoundedCornerShape(100.dp)
                        )
                        .blur(radius = 50.dp)
                )

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .offset(x = 200.dp, y = 20.dp)
                        .background(
                            Color(0x333B82F6),
                            RoundedCornerShape(100.dp)
                        )
                        .blur(radius = 50.dp)
                )

                // Content Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Title Section
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = friendStory.friendName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                        )
                        Text(
                            text = "님에게",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                    }

                    // TextField Section
                    val gradientBrush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2E1065),
                            Color(0xFF1E1B4B),
                            Color(0xFF172554)
                        )
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = {
                            message = it
                            Log.d("MessageDialog", "Message input: $it")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(brush = gradientBrush),
                        placeholder = {
                            Text(
                                "메시지를 입력하세요",
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0x33A855F7),
                            unfocusedBorderColor = Color(0x333B82F6),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        )
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        Log.d("MessageDialog", "Cancel button clicked")
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x99A855F7),
                        disabledContainerColor = Color(0x99A855F7)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "취소하기",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
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
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    enabled = message.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x663B82F6),
                        disabledContainerColor = Color(0x663B82F6)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "보내기",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    )
}