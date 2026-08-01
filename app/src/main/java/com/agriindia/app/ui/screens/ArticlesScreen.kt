package com.agriindia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.Article

@Composable
fun ArticlesScreen(
    language: AppLanguage,
    articles: List<Article>,
    selectedArticle: Article?,
    onSelectArticle: (Article?) -> Unit,
    onToggleBookmark: (String) -> Unit
) {
    val isHi = language == AppLanguage.HINDI

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHi) "कृषि ज्ञान एवं तकनीकी लेख" else "Krishi Gyan & Technology Articles",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(articles) { article ->
                ArticleCard(
                    article = article,
                    isHi = isHi,
                    onClick = { onSelectArticle(article) },
                    onToggleBookmark = { onToggleBookmark(article.id) }
                )
            }
        }
    }

    if (selectedArticle != null) {
        ArticleDetailDialog(
            article = selectedArticle,
            isHi = isHi,
            onDismiss = { onSelectArticle(null) }
        )
    }
}

@Composable
fun ArticleCard(article: Article, isHi: Boolean, onClick: () -> Unit, onToggleBookmark: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = article.category,
                        fontSize = 10.sp,
                        color = Color(0xFF0369A1),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (article.isBookmarked) Color(0xFFD97706) else Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isHi) article.titleHi else article.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isHi) article.excerptHi else article.excerpt,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = article.readTime, fontSize = 10.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "• By ${article.author}", fontSize = 10.sp, color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
fun ArticleDetailDialog(article: Article, isHi: Boolean, onDismiss: () -> Unit) {
    var isPlayingAudio by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isHi) article.titleHi else article.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Audio Narration Player Bar
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { isPlayingAudio = !isPlayingAudio }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Audio",
                                tint = Color(0xFF059669)
                            )
                        }
                        Text(
                            text = if (isPlayingAudio) "Playing Audio Narration..." else "Listen to Audio (हिंदी / English)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isHi) article.fullContentHi else article.fullContent,
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
