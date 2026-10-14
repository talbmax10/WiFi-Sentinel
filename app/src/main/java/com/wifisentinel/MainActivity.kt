package com.wifisentinel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF080D18)
private val Panel = Color(0xFF111B2C)
private val Cyan = Color(0xFF42E8D2)
private val Muted = Color(0xFF9AAAC0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SentinelApp() }
    }
}

@Composable
private fun SentinelApp() {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }
    val score = strengthScore(password)
    val verdict = when {
        password.isEmpty() -> "أدخل كلمة مرور لتقييمها"
        score < 2 -> "ضعيفة — يُنصح بتغييرها"
        score < 4 -> "متوسطة — يمكن تحسينها"
        else -> "جيدة — حافظ عليها بسرية"
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Cyan, background = Bg, surface = Panel,
            onBackground = Color.White, onSurface = Color.White
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(shape = RoundedCornerShape(16.dp), color = Panel, modifier = Modifier.size(54.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Cyan, modifier = Modifier.size(30.dp))
                        }
                    }
                    Column {
                        Text("WIFI SENTINEL", color = Cyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("مختبر أمان الشبكات • نسخة محلية", color = Muted, fontSize = 12.sp)
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = Cyan)
                            Text("مختبر كلمة المرور", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "قيّم قوة كلمة مرور الراوتر محلياً على الهاتف. لا يتصل التطبيق بالشبكات ولا يرسل كلمة المرور إلى أي مكان.",
                            color = Muted, fontSize = 13.sp
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; checked = false },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("كلمة مرور شبكتك") },
                            singleLine = true,
                            visualTransformation = if (showPassword) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                TextButton(onClick = { showPassword = !showPassword }) {
                                    Text(if (showPassword) "إخفاء" else "إظهار")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan, focusedLabelColor = Cyan,
                                unfocusedBorderColor = Muted, cursorColor = Cyan
                            )
                        )
                        Button(
                            onClick = { checked = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Bg)
                        ) { Text("تحليل القوة", fontWeight = FontWeight.Bold) }

                        if (checked && password.isNotEmpty()) {
                            LinearProgressIndicator(
                                progress = { score / 5f },
                                modifier = Modifier.fillMaxWidth().height(8.dp),
                                color = when { score < 2 -> Color(0xFFFF5D73); score < 4 -> Color(0xFFFFC857); else -> Cyan },
                                trackColor = Color(0xFF26354B)
                            )
                            Text(verdict, fontWeight = FontWeight.Bold)
                            Text("مؤشر تقريبي: $score / 5", color = Muted, fontSize = 12.sp)
                            Text(strengthAdvice(password), color = Muted, fontSize = 13.sp)
                        }
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Cyan)
                            Text("قائمة تدقيق الراوتر", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("استخدمها عند تسجيل الدخول إلى لوحة تحكم الراوتر الذي تملكه:", color = Muted, fontSize = 13.sp)
                        listOf(
                            "فعّل WPA3-Personal أو WPA2-AES.",
                            "عطّل WPS إذا لم تكن تحتاجه.",
                            "غيّر كلمة مرور الإدارة الافتراضية.",
                            "حدّث البرنامج الثابت من الموقع الرسمي.",
                            "استخدم عبارة مرور طويلة وفريدة."
                        ).forEach { item ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                                Text("✓", color = Cyan, fontWeight = FontWeight.Bold)
                                Text(item, color = Color(0xFFE3EAF5), fontSize = 14.sp)
                            }
                        }
                    }
                }
                Text(
                    "ملاحظة: هذا الإصدار لا يخمّن كلمات المرور ولا يختبر الدخول إلى الراوتر. تحليل القوة تقديري ويعمل على الجهاز فقط.",
                    color = Muted, fontSize = 11.sp
                )
            }
        }
    }
}

private fun strengthScore(value: String): Int {
    if (value.isEmpty()) return 0
    var score = 0
    if (value.length >= 12) score++
    if (value.length >= 16) score++
    if (value.any { it.isLowerCase() } && value.any { it.isUpperCase() }) score++
    if (value.any { it.isDigit() }) score++
    if (value.any { !it.isLetterOrDigit() }) score++
    if (value.toSet().size <= 3 || value.lowercase() in setOf("password", "12345678", "qwerty123")) {
        score = (score - 2).coerceAtLeast(0)
    }
    return score.coerceIn(0, 5)
}

private fun strengthAdvice(value: String): String {
    val tips = mutableListOf<String>()
    if (value.length < 16) tips += "اجعلها طويلة (يفضّل 16 حرفاً أو أكثر)."
    if (!value.any(Char::isDigit)) tips += "أضف أرقاماً إن كان ذلك يسهل تذكّرها."
    if (!value.any { !it.isLetterOrDigit() }) tips += "يمكنك إضافة رموز."
    if (tips.isEmpty()) tips += "لا تعِد استخدام كلمة المرور في خدمات أخرى، وفكّر بعبارة طويلة وفريدة."
    return tips.joinToString(" ")
}
