package com.agriindia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage

@Composable
fun KisanMitraDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    var queryText by remember { mutableStateOf("") }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                if (language == AppLanguage.HINDI)
                    "नमस्ते किसान भाई! मैं आपका एआई किसान मित्र हूँ। फसल सुरक्षा, मंडी भाव या सरकारी योजनाओं के बारे में कुछ भी पूछें।"
                else
                    "Namaste Kisanji! I am your AI Kisan Mitra. Ask me anything about crop protection, Mandi prices, or government schemes."
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.HINDI) "किसान मित्र AI सहायक" else "Kisan Mitra AI Helper",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Messages Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    chatMessages.forEach { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = msg,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input box
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        placeholder = {
                            Text(
                                if (language == AppLanguage.HINDI) "अपना प्रश्न पूछें..." else "Ask your question...",
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (queryText.isNotBlank()) {
                                val userQ = queryText
                                queryText = ""
                                val reply = if (language == AppLanguage.HINDI)
                                    "आपके प्रश्न ('$userQ') का उत्तर: मौसम अनुकूल है। फसल में कीटनाशक छिड़काव शाम 4 बजे के बाद करें।"
                                else
                                    "Reply to ('$userQ'): Weather conditions are suitable. Spray pesticide after 4:00 PM today."
                                chatMessages = chatMessages + ("Q: $userQ") + reply
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF059669), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}
