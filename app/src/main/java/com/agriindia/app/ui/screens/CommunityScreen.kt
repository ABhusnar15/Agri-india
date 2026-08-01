package com.agriindia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.CommunityPost

@Composable
fun CommunityScreen(
    language: AppLanguage,
    posts: List<CommunityPost>,
    onToggleLike: (String) -> Unit,
    onAddPost: (String, String) -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    var showCreatePostDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isHi) "किसान चौपाल (सोशल नेटवर्क)" else "Kisan Chopal (Social Network)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Button(
                    onClick = { showCreatePostDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHi) "पोस्ट लिखें" else "New Post", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts) { post ->
                    CommunityPostCard(post = post, isHi = isHi, onToggleLike = { onToggleLike(post.id) })
                }
            }
        }

        if (showCreatePostDialog) {
            CreatePostDialog(
                isHi = isHi,
                onDismiss = { showCreatePostDialog = false },
                onSubmit = { content, category ->
                    onAddPost(content, category)
                    showCreatePostDialog = false
                }
            )
        }
    }
}

@Composable
fun CommunityPostCard(post: CommunityPost, isHi: Boolean, onToggleLike: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF059669), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = post.authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (post.isVerifiedExpert) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(text = "${post.authorState} • ${post.timeAgo}", fontSize = 10.sp, color = Color(0xFF64748B))
                }

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = post.category,
                        fontSize = 10.sp,
                        color = Color(0xFF92400E),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isHi) post.contentHi else post.content,
                fontSize = 13.sp,
                color = Color(0xFF1E293B)
            )

            // Expert Answer highlight
            if (post.comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = post.comments[0].author, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = post.comments[0].text, fontSize = 11.sp, color = Color(0xFF15803D))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleLike) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color.Red else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(text = "${post.likesCount}", fontSize = 12.sp, color = Color(0xFF64748B))

                Spacer(modifier = Modifier.width(16.dp))

                Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = "Comment", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${post.commentsCount}", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun CreatePostDialog(isHi: Boolean, onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {
    var contentText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Pest Control") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isHi) "चौपाल पर नया प्रश्न/पोस्ट साझा करें" else "Create New Post on Chopal") },
        text = {
            Column {
                OutlinedTextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    placeholder = { Text(if (isHi) "अपनी समस्या या अनुभव लिखें..." else "Write your problem or advice...", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Pest Control", "Organic Farming", "Machinery").forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (contentText.isNotBlank()) {
                        onSubmit(contentText, selectedCategory)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
            ) {
                Text(if (isHi) "प्रकाशित करें" else "Publish Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isHi) "रद्द करें" else "Cancel")
            }
        }
    )
}
