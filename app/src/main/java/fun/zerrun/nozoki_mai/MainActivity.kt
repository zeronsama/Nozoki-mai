package `fun`.zerrun.nozoki_mai

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import `fun`.zerrun.nozoki_mai.data.*
import `fun`.zerrun.nozoki_mai.network.*
import `fun`.zerrun.nozoki_mai.ui.theme.NozokimaiTheme
import kotlinx.coroutines.launch

import coil.compose.AsyncImage
import coil.request.ImageRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NozokimaiTheme {
                MainUserInterface()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainUserInterface() {
        val rankName: Array<String> = arrayOf(
            "初学者", "初段", "二段", "三段", "四段", "五段", "六段", "七段", "八段", "九段", "十段",
            "真初段", "真二段", "真三段", "真四段", "真五段", "真六段", "真七段", "真八段", "真九段", "真十段",
            "真皆传", "里皆传"
        )
        var text by remember { mutableStateOf("") }
        var displayedText by remember { mutableStateOf("") }
        var playerInfo by remember { mutableStateOf<PlayerResponse?>(null) }
        var errorInfo by remember { mutableStateOf("") }
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Nozoki-mai") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(selectedTabIndex = selectedTabIndex) {
                            Tab(
                                text = { Text("主页") },
                                selected = selectedTabIndex == 0,
                                onClick = { selectedTabIndex = 0 }
                            )
                            Tab(
                                text = { Text("B50信息") },
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 }
                            )
                        }

                        when (selectedTabIndex) {
                            0 -> HomeContent(rankName, text, displayedText, playerInfo, errorInfo, onConfirmClick = {
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
                            }, onValueChange = { newText ->
                                if (newText.matches(Regex("[0-9]{0,13}"))) {
                                    text = newText
                                }
                            })
                            1 -> ChartsContent(playerInfo)
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun HomeContent(
        rankName: Array<String>,
        text: String,
        displayedText: String,
        playerInfo: PlayerResponse?,
        errorInfo: String,
        onConfirmClick: () -> Unit,
        onValueChange: (String) -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = (LocalConfiguration.current.screenHeightDp / 4).dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 输入框
                TextField(
                    value = text,
                    onValueChange = onValueChange,
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
                    onClick = onConfirmClick
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
                        text = "$errorInfo\n$additionalInfo",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                // 显示API返回的数据
                if (playerInfo != null) {
                    Column {
                        Text(
                            text = "昵称: ${playerInfo.nickname}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "Rating: ${playerInfo.rating}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = "段位: ${rankName[playerInfo.additional_rating]}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ChartsContent(playerInfo: PlayerResponse?) {
        if (playerInfo != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "当前版本B15:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                items(count = playerInfo.charts.dx.size) { index ->
                    val chart = playerInfo.charts.dx[index]
                    ChartItem(index, chart)
                }
                item {
                    Text(
                        text = "旧版本B35:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                items(count = playerInfo.charts.sd.size) { index ->
                    val chart = playerInfo.charts.sd[index]
                    ChartItem(index, chart)
                }
            }
        } else {
            Text(
                text = "请先查询玩家信息",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    @SuppressLint("DefaultLocale")
    @Composable
    fun ChartItem(index: Int, chart: Chart) {
        val backgroundColor = when (chart.level_index) {
            0 -> Color(0xff6fe163)
            1 -> Color(0xfff8df3a)
            2 -> Color(0xffff818d)
            3 -> Color(0xffc27ff4)
            4 -> Color(0xffe6e6e6)
            else -> Color.White
        }
        val textColor = when (chart.level_index) {
            0, 1, 2, 3, 4 -> Color.Black
            else -> Color.Black
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(6.dp)
        ) {
            Row(
                modifier = Modifier
                    //.fillMaxWidth()
                    .padding(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(
                                "https://www.diving-fish.com/covers/${
                                    String.format("%05d", chart.song_id)
                                }.png"
                            )
                            .error(R.drawable.fail)
                            .build(),
                        contentDescription = "${chart.song_id}的曲绘",
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(text = "${chart.song_id}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp))
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = chart.title,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = String.format("%.4f", chart.achievements),
                        style = TextStyle(fontSize = 24.sp)
                    )
                    Text(
                        text = "${chart.level}(${chart.ds})→${chart.ra}    ${chart.dxScore} ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    fun ChartItemPreview(){
        var chart = Chart(100.63,13.5,1693,"fv","sync","13",3,"Master",303,"sssp",11559,"魔法少女とチョコレゐト","DX")
        ChartItem(1,chart)
    }
}