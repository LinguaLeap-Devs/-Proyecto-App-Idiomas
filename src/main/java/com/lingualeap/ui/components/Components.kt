package com.lingualeap.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingualeap.data.model.Language
import com.lingualeap.data.model.Lesson
import com.lingualeap.data.model.LessonLevel
import com.lingualeap.ui.theme.LinguaColors

@Composable
fun LinguaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    containerColor: Color = LinguaColors.Primary
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = LinguaColors.Border
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        } else {
            Text(text = text, fontSize = 17.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
fun LinguaOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, LinguaColors.Primary),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = LinguaColors.Primary)
    ) {
        Text(text = text, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LinguaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = LinguaColors.TextPrimary, modifier = Modifier.padding(bottom = 6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = LinguaColors.TextHint) },
            shape = RoundedCornerShape(12.dp),
            isError = errorMessage != null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                    }
                }
            } else null
        )
    }
}

@Composable
fun DividerWithText(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = LinguaColors.Border)
        Text(text = text, modifier = Modifier.padding(horizontal = 12.dp), fontSize = 12.sp, color = LinguaColors.TextSecondary)
        HorizontalDivider(modifier = Modifier.weight(1f), color = LinguaColors.Border)
    }
}

@Composable
fun LessonNode(lesson: Lesson, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val nodeColor = when (lesson.level) {
        LessonLevel.BEGINNER -> Color(0xFF4CAF50)
        LessonLevel.ELEMENTARY -> Color(0xFF2196F3)
        else -> Color(0xFFFF9800)
    }
    Column(modifier = modifier.width(100.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(86.dp).shadow(if (lesson.isLocked) 0.dp else 8.dp, CircleShape).clip(CircleShape)
                .background(if (lesson.isLocked) Color(0xFFE0E0E0) else Color.White).clickable(enabled = !lesson.isLocked) { onClick() }
        ) {
            if (!lesson.isLocked) {
                Canvas(modifier = Modifier.size(76.dp)) {
                    drawArc(color = nodeColor.copy(alpha = 0.2f), startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(width = 6.dp.toPx()))
                    if (lesson.isCompleted) {
                        drawArc(color = nodeColor, startAngle = -90f, sweepAngle = 360f, useCenter = false, style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round))
                    }
                }
            }
            Surface(modifier = Modifier.size(60.dp), shape = CircleShape, color = if (lesson.isLocked) Color(0xFFBDBDBD) else nodeColor, tonalElevation = 4.dp) {
                Box(contentAlignment = Alignment.Center) {
                    if (lesson.isLocked) Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    else Text(text = lesson.emoji, fontSize = 28.sp)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(text = lesson.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (lesson.isLocked) Color.Gray else Color.Black, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 2, lineHeight = 16.sp)
    }
}

@Composable
fun UserAvatar(initials: String, size: Int = 48) {
    Box(modifier = Modifier.size(size.dp).clip(CircleShape).background(LinguaColors.PrimaryLight), contentAlignment = Alignment.Center) {
        Text(text = initials.take(2).uppercase(), fontSize = (size / 3).sp, fontWeight = FontWeight.Bold, color = LinguaColors.Primary)
    }
}

@Composable
fun LanguageCard(language: Language, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val borderColor = if (isSelected) Color(language.color) else Color(0xFFE5E5E5)
    val backgroundColor = if (isSelected) Color(language.color).copy(alpha = 0.1f) else Color.White
    Box(modifier = modifier.fillMaxWidth().padding(bottom = 4.dp).shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(16.dp)).background(backgroundColor, RoundedCornerShape(16.dp)).border(2.dp, borderColor, RoundedCornerShape(16.dp)).clickable { onClick() }.padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = language.flag, fontSize = 40.sp)
            Spacer(Modifier.height(8.dp))
            Text(text = language.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isSelected) Color(language.color) else Color.Black)
        }
    }
}
