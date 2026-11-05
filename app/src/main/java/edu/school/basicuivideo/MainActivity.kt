package edu.school.basicuivideo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import edu.school.basicuivideo.ui.theme.BasicUIVideoTheme

private val PhotoBackground = Color(0xFFE0BBE4)
private const val PlaybackLog = "BasicUIVideo"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicUIVideoTheme {
                Scaffold(containerColor = PhotoBackground) { innerPadding ->
                    Main(
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                            .imePadding()
                    )
                }
            }
        }
    }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
    var selectedVideo by rememberSaveable { mutableIntStateOf(0) }
    val videoRes = if (selectedVideo == 0) R.raw.love else R.raw.handpan

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(PhotoBackground)) {
        val videoHeight = if (maxHeight < 500.dp) 120.dp else 200.dp

        // 直向影片左右留白時，可以看見底下的課名與 Android / Compose 圖片。
        Column(
            modifier = Modifier.fillMaxWidth().height(videoHeight).padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("行動應用軟體開發：基本元件", fontSize = 16.sp)
            Text("作者：蔡承叡", fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))
            RowExample()
        }

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            SimpleVideoPlayer(videoRes = videoRes, height = videoHeight)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { selectedVideo = 1 - selectedVideo }) {
                Text(if (selectedVideo == 0) "切換至手碟影片" else "切換至Love影片")
            }
            Text(
                "作者：蔡承叡",
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            LazyRowExample(modifier = Modifier.weight(1f).fillMaxWidth())
            TextFieldExample()
        }
    }

}

@Composable
fun RowExample() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.android),
            contentDescription = "Android 圖示",
            modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.Yellow),
            alpha = 0.6f
        )
        Spacer(Modifier.width(20.dp))
        Image(painterResource(R.drawable.compose), "Compose 圖示", modifier = Modifier.size(100.dp))
    }
}

data class Animal(val name: String, @DrawableRes val imageRes: Int)

@Composable
fun LazyRowExample(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val animals = remember {
        listOf(
            Animal("鴨子", R.drawable.animal0),
            Animal("企鵝", R.drawable.animal1),
            Animal("青蛙", R.drawable.animal2),
            Animal("貓頭鷹", R.drawable.animal3),
            Animal("海豚", R.drawable.animal4),
            Animal("牛", R.drawable.animal5),
            Animal("無尾熊", R.drawable.animal6),
            Animal("獅子", R.drawable.animal7),
            Animal("狐狸", R.drawable.animal8),
            Animal("小雞", R.drawable.animal9)
        )
    }
    LazyRow(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        itemsIndexed(animals, key = { _, animal -> animal.name }) { index, animal ->
            Card(
                modifier = Modifier.fillParentMaxWidth().heightIn(max = 260.dp).fillParentMaxHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD0BCFF))
            ) {
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    val artSize = minOf(150.dp, (maxHeight - 40.dp).coerceAtLeast(24.dp))
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painterResource(animal.imageRes),
                            contentDescription = animal.name,
                            modifier = Modifier.size(artSize).clickable {
                                Toast.makeText(context, "這是 ${animal.name}", Toast.LENGTH_SHORT).show()
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("$index ${animal.name}")
                    }
                }
            }
        }
    }
}

@Composable
fun TextFieldExample() {
    var inputText by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("請輸入文字") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(8.dp))
        Text(if (inputText.isBlank()) "尚未輸入內容" else "您輸入的是：$inputText")
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun SimpleVideoPlayer(@RawRes videoRes: Int, height: Dp) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var playbackError by remember { mutableStateOf<String?>(null) }
    val player = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    // videoRes 改變時，更新播放器的媒體並從頭播放。
    LaunchedEffect(videoRes, player) {
        playbackError = null
        val name = context.resources.getResourceEntryName(videoRes)
        val uri = "android.resource://${context.packageName}/$videoRes"
        player.setMediaItem(MediaItem.Builder().setUri(uri).setMediaId(name).build())
        player.prepare()
        player.play()
        Log.d(PlaybackLog, "Selected media=$name")
    }

    DisposableEffect(lifecycleOwner, player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(PlaybackLog, "Playing media=${player.currentMediaItem?.mediaId} active=$isPlaying")
            }
            override fun onRenderedFirstFrame() {
                Log.d(PlaybackLog, "Rendered media=${player.currentMediaItem?.mediaId}")
            }
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                Log.d(PlaybackLog, "Video size=${videoSize.width}x${videoSize.height}")
            }
            override fun onPlayerError(error: PlaybackException) {
                playbackError = "影片播放失敗：${error.errorCodeName}"
                Log.e(PlaybackLog, "Playback failed", error)
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> player.pause()
                Lifecycle.Event.ON_START -> player.play()
                else -> Unit
            }
        }
        player.addListener(listener)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.removeListener(listener)
            player.release()
        }
    }

    Box(Modifier.fillMaxWidth().height(height), contentAlignment = Alignment.Center) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            },
            update = { it.player = player },
            modifier = Modifier.fillMaxSize()
        )
        playbackError?.let { Text(it, modifier = Modifier.background(PhotoBackground).padding(8.dp)) }
    }
}
