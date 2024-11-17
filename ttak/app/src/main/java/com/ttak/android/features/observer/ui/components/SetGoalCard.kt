package com.ttak.android.features.observer.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.ttak.android.domain.model.GoalState

@SuppressLint("RememberReturnType")
@Composable
fun SetGoalCard(
    goalState: GoalState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val colors = listOf(
                Color(0xFF2E1065), // 진한 보라
                Color(0xFF1E1B4B), // 진한 남색
                Color(0xFF172554)  // 진한 파랑
            )
            val brush = Brush.verticalGradient(colors)
            drawRect(brush = brush)
        }

        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 150.dp, y = (-50).dp)
                .background(
                    Color(0x33A855F7),
                    CircleShape
                )
                .blur(radius = 70.dp)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-50).dp, y = 150.dp)
                .background(
                    Color(0x333B82F6),
                    CircleShape
                )
                .blur(radius = 70.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentSize()
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(Color(0x33FFFFFF), Color(0x1AFFFFFF))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                color = Color(0x1AFFFFFF),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 0.8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ), label = ""
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                Color(0xFF4ADE80).copy(alpha = alpha),
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${goalState.observerCount}명이 지켜보고 있어요",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                goalState.selectedApps.forEach { app ->
                    item {
                        var isHovered by remember { mutableStateOf(false) }
                        var isSelected by remember { mutableStateOf(false) }

                        val animatedScale by animateFloatAsState(targetValue = if (isHovered) 1.1f else 1f)
                        val animatedBorderColor by animateColorAsState(
                            targetValue = if (isSelected) Color(0xFF66B2FF) else Color(0x1AFFFFFF)
                        )

                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .hoverable(
                                    interactionSource = remember { MutableInteractionSource() },
                                )
                                .scale(animatedScale)
                                .clickable { isSelected = !isSelected },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x33FFFFFF),
                            border = BorderStroke(
                                width = 1.dp,
                                color = animatedBorderColor
                            ),
                            shadowElevation = if (isHovered) 8.dp else 4.dp
                        ) {
                            Image(
                                painter = rememberDrawablePainter(drawable = app.icon),
                                contentDescription = app.appName,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TimeProgress(
                startTime = goalState.startTime,
                endTime = goalState.endTime,
            )
        }
    }
}