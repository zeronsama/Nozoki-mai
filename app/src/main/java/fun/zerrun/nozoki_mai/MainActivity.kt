package `fun`.zerrun.nozoki_mai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import `fun`.zerrun.nozoki_mai.data.*
import `fun`.zerrun.nozoki_mai.network.*
import `fun`.zerrun.nozoki_mai.ui.theme.NozokimaiTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NozokimaiTheme {
                MainUserInterface()
            }
        }
    }

    @Composable
    fun MainUserInterface() {
        val rankName: Array<String> = arrayOf(
            "初学者", "初段", "二段", "三段", "四段", "五段", "六段", "七段", "八段", "九段", "十段",
            "真初段", "真二段", "真三段", "真四段", "真五段", "真六段", "真七段", "真八段", "真九段", "真十段",
            "真皆传", "里皆传")
        var text by remember { mutableStateOf("") }
        var displayedText by remember { mutableStateOf("") }
        var playerInfo by remember { mutableStateOf<PlayerResponse?>(null) }
        var errorInfo by remember { mutableStateOf("") }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 输入框
                    TextField(
                        value = text,
                        onValueChange = { newText ->
                            // 只允许输入13位以内数字
                            if (newText.matches(Regex("[0-9]{0,13}"))) {
                                text = newText
                            }
                        },
                        label = { Text("请输入QQ号") },
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.qq),
                                contentDescription = "QQ Icon",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    // 按钮
                    Button(
                        onClick = {
                            // 更新显示的值
                            displayedText = text
                            lifecycleScope.launch {
                                try {
                                    val response = RetrofitClient.apiService.getPlayerInfo(PlayerRequest(qq = text))
                                    playerInfo = response
                                    errorInfo = ""
                                } catch (e: Exception) {
                                    playerInfo = null
                                    errorInfo = "${e.message}"
                                }
                            }
                        }
                    ) {
                        Text("确认")
                    }
                    // 显示输入的值
                    if (displayedText.isNotEmpty()) {
                        Text(
                            text = "你输入的QQ号: $displayedText",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    // 错误信息
                    if (errorInfo.isNotEmpty()) {
                        println(errorInfo)
                        var additionalInfo: String = when (errorInfo) {
                            "HTTP 403 " -> "该用户禁止了其他人获取数据"
                            "HTTP 400 " -> "该用户未绑定水鱼数据库"
                            else -> "未知的错误"
                        }
                        Text(
                            text = "$errorInfo\n\n$additionalInfo\n",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    // 显示API返回的数据
                    if (playerInfo != null) {
                        Column {
                            Text(
                                text = "昵称: ${playerInfo?.nickname}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = "Rating: ${playerInfo?.rating}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            playerInfo?.additional_rating?.let {
                                Text(
                                    text = "段位: ${rankName[it]}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }

                }
            }
        )
    }
}