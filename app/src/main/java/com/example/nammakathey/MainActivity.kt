package com.example.nammakathey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.AutoStories
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.net.toUri
import com.example.nammakathey.ui.theme.NammaKatheyTheme
import java.util.Locale
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.HorizontalPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// --- Premium Heritage Palette ---
val HeritageCream = Color(0xFFFAF9F6)
val HeritageSandalwood = Color(0xFFF5E6CA)
val HeritageMaroon = Color(0xFF800000)
val HeritageMaroonDark = Color(0xFF4A0000)
val HeritageGold = Color(0xFFC5A059)
val HeritageBrown = Color(0xFF4E342E)
val HeritageText = Color(0xFF2C1B18)
val HeritageOffWhite = Color(0xFFFCFBF9)

enum class AppLanguage { English, Kannada }
enum class NavScreen { Map, Stories, Badges, Statues }

data class LocalizedText(val english: String, val kannada: String) {
    fun value(language: AppLanguage) = if (language == AppLanguage.English) english else kannada
}

data class Quiz(val question: LocalizedText, val options: List<LocalizedText>, val answerIndex: Int)

data class HeroStory(
    val id: String,
    val district: LocalizedText,
    val hero: LocalizedText,
    val theme: LocalizedText,
    val accent: String,
    val markerX: Float,
    val markerY: Float,
    val memorial: String,
    val distanceKm: Int,
    val storyPages: List<LocalizedText>,
    val quizzes: List<Quiz>,
) {
    val accentColor: Color get() = try { Color(android.graphics.Color.parseColor(accent)) } catch(e: Exception) { HeritageMaroon }
}

class MainActivity : ComponentActivity() {
    private var textToSpeech: TextToSpeech? = null
    private var onUtteranceFinish: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textToSpeech = TextToSpeech(this) {
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    runOnUiThread { onUtteranceFinish?.invoke() }
                }
                override fun onError(utteranceId: String?) {}
            })
        }
        setContent {
            NammaKatheyTheme {
                val stories = remember { loadStories(this) }
                if (stories.isNotEmpty()) {
                    NammaKatheyApp(
                        allStories = stories,
                        onSpeak = { text, lang, onFinish ->
                            val locale = if (lang == AppLanguage.Kannada) Locale.forLanguageTag("kn-IN") else Locale.forLanguageTag("en-IN")
                            textToSpeech?.language = locale
                            onUtteranceFinish = onFinish
                            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "hero-story")
                        },
                        onStopSpeaking = { textToSpeech?.stop() }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = HeritageMaroon)
                    }
                }
            }
        }
    }

    private fun loadStories(context: Context): List<HeroStory> {
        return try {
            val json = context.assets.open("stories.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<HeroStory>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onDestroy() {
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NammaKatheyApp(
    allStories: List<HeroStory>,
    onSpeak: (String, AppLanguage, () -> Unit) -> Unit,
    onStopSpeaking: () -> Unit
) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(NavScreen.Map) }
    var language by remember { mutableStateOf(AppLanguage.English) }
    var selectedStory by remember { mutableStateOf(allStories.first()) }
    val badges = remember { mutableStateListOf<String>().apply { addAll(loadBadges(context)) } }

    Scaffold(
        containerColor = HeritageCream,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "NAMMA KATHEY",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = HeritageMaroon
                            )
                        )
                        Text(
                            "31 Districts | Heritage Chronicles",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = HeritageGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Menu, contentDescription = "Menu", tint = HeritageMaroon) } },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = HeritageMaroon) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = HeritageCream)
            )
        },
        bottomBar = {
            BottomNavigationBar(currentScreen) { currentScreen = it }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .drawBehind { drawHeritageBackground() }) {
            when (currentScreen) {
                NavScreen.Map -> MapScreen(allStories, language, selectedStory, { language = it }) {
                    selectedStory = it
                    currentScreen = NavScreen.Stories
                }
                NavScreen.Stories -> StoriesScreen(language, selectedStory, onSpeak, badges) {
                    currentScreen = NavScreen.Statues
                }
                NavScreen.Badges -> BadgesScreen(allStories, language, badges)
                NavScreen.Statues -> StatuesScreen(selectedStory)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentScreen: NavScreen, onNavigate: (NavScreen) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 20.dp), contentAlignment = Alignment.BottomCenter) {
        Surface(
            modifier = Modifier
                .height(72.dp)
                .fillMaxWidth(),
            color = HeritageBrown,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 12.dp
        ) {
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                val items = listOf(
                    Triple(NavScreen.Map, Icons.Default.Public, "Map"),
                    Triple(NavScreen.Stories, Icons.AutoMirrored.Filled.AutoStories, "Story"),
                    Triple(NavScreen.Badges, Icons.Default.AutoAwesome, "Badge"),
                    Triple(NavScreen.Statues, Icons.Default.LocationOn, "Statue")
                )
                items.forEach { (screen, icon, label) ->
                    val selected = currentScreen == screen
                    val scale by animateFloatAsState(if (selected) 1.2f else 1.0f, label = "iconScale")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigate(screen) }
                            .padding(8.dp)
                    ) {
                        Icon(
                            icon,
                            contentDescription = label,
                            tint = if (selected) HeritageGold else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp * scale)
                        )
                        Text(
                            label,
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) HeritageGold else Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MapScreen(allStories: List<HeroStory>, language: AppLanguage, selectedStory: HeroStory, onLangToggle: (AppLanguage) -> Unit, onSelect: (HeroStory) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        item {
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "EN",
                    modifier = Modifier.clickable { onLangToggle(AppLanguage.English) }.padding(4.dp),
                    fontWeight = if (language == AppLanguage.English) FontWeight.Bold else FontWeight.Normal,
                    color = if (language == AppLanguage.English) HeritageMaroon else HeritageGold
                )
                Text("|", modifier = Modifier.padding(horizontal = 4.dp), color = HeritageGold)
                Text(
                    "ಕನ್ನಡ",
                    modifier = Modifier.clickable { onLangToggle(AppLanguage.Kannada) }.padding(4.dp),
                    fontWeight = if (language == AppLanguage.Kannada) FontWeight.Bold else FontWeight.Normal,
                    color = if (language == AppLanguage.Kannada) HeritageMaroon else HeritageGold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Heritage\nAtlas",
                fontSize = 48.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Black,
                color = HeritageMaroon
            )
            Text(
                "Explore all 31 districts. Tap a legend to start.",
                fontSize = 16.sp,
                color = HeritageBrown.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).shadow(8.dp, RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(containerColor = HeritageSandalwood),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.karnataka_map),
                        contentDescription = "Map",
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                    Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        allStories.forEach { story ->
                            val isSelected = story.id == selectedStory.id
                            drawCircle(
                                color = if (isSelected) HeritageMaroon else story.accentColor,
                                radius = if (isSelected) 8.dp.toPx() else 4.dp.toPx(),
                                center = Offset(size.width * story.markerX, size.height * story.markerY)
                            )
                            if (isSelected) {
                                drawCircle(HeritageGold, radius = 3.dp.toPx(), center = Offset(size.width * story.markerX, size.height * story.markerY))
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("HERITAGE LEGENDS", color = HeritageGold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(12.dp))
        }
        items(allStories) { story ->
            val isSelected = selectedStory.id == story.id
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onSelect(story) },
                colors = CardDefaults.cardColors(containerColor = if (isSelected) HeritageSandalwood else HeritageOffWhite),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).clip(CircleShape).background(story.accentColor.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.History, contentDescription = null, tint = story.accentColor, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(story.hero.value(language), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = HeritageBrown)
                        Text(story.district.value(language), color = HeritageGold, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    if (isSelected) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = HeritageMaroon, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
fun StoriesScreen(
    language: AppLanguage,
    story: HeroStory,
    onSpeak: (String, AppLanguage, () -> Unit) -> Unit,
    badges: MutableList<String>,
    onNavigateToStatues: () -> Unit
) {
    var showingQuiz by remember { mutableStateOf(false) }
    var isReading by remember { mutableStateOf(false) }
    
    // reset pager state when story changes
    val pagerState = rememberPagerState(key = story.id, pageCount = { story.storyPages.size })
    
    LaunchedEffect(story.id) {
        pagerState.scrollToPage(0)
    }

    val context = LocalContext.current
    val labels = listOf("THE LEGEND", "THE JOURNEY", "THE LEGACY")

    LaunchedEffect(isReading, pagerState.currentPage) {
        if (isReading) {
            onSpeak(story.storyPages[pagerState.currentPage].value(language), language) {
                isReading = false
            }
        }
    }

    if (showingQuiz) {
        QuizSection(language, story, story.id in badges,
            onEarn = {
                if (story.id !in badges) {
                    badges.add(story.id)
                    saveBadges(context, badges)
                }
            },
            onComplete = onNavigateToStatues,
            onCancel = { showingQuiz = false }
        )
    } else {
        Column(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .shadow(16.dp)
            ) {
                Image(
                    painter = painterResource(id = resolveHeroImage(story.id)),
                    contentDescription = "Hero Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))
                ))
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                    Text(
                        story.hero.value(language),
                        style = MaterialTheme.typography.headlineLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        story.district.value(language),
                        style = MaterialTheme.typography.titleMedium.copy(color = HeritageGold, fontWeight = FontWeight.Bold)
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).padding(24.dp)
            ) { pageIdx ->
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = HeritageOffWhite),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, HeritageSandalwood)
                ) {
                    Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                        Text(
                            labels.getOrElse(pageIdx) { "STORY" },
                            style = MaterialTheme.typography.labelLarge,
                            color = HeritageGold,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            story.storyPages[pageIdx].value(language),
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 32.sp, color = HeritageText, fontSize = 20.sp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isReading = !isReading },
                    modifier = Modifier.size(56.dp).background(HeritageMaroon, CircleShape)
                ) {
                    Icon(if (isReading) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.White)
                }

                Button(
                    onClick = { showingQuiz = true },
                    modifier = Modifier.height(56.dp).weight(1f).padding(start = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HeritageBrown),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("START LEGACY QUIZ", fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
fun QuizSection(language: AppLanguage, story: HeroStory, earned: Boolean, onEarn: () -> Unit, onComplete: () -> Unit, onCancel: () -> Unit) {
    var qIdx by remember(story.id) { mutableIntStateOf(0) }
    var selected by remember(story.id, qIdx) { mutableStateOf<Int?>(null) }
    var correctCount by remember(story.id) { mutableIntStateOf(0) }
    var showResult by remember(story.id) { mutableStateOf(false) }

    val quiz = story.quizzes.getOrNull(qIdx) ?: return

    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("LEGACY CHALLENGE", style = MaterialTheme.typography.labelLarge, color = HeritageGold, letterSpacing = 2.sp)
        Text(story.hero.value(language), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = HeritageMaroon)
        Spacer(Modifier.height(48.dp))

        if (showResult) {
            Card(colors = CardDefaults.cardColors(containerColor = HeritageOffWhite), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (correctCount == 3) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = HeritageGold, modifier = Modifier.size(80.dp))
                        Text("3/3 - Perfect Score!", fontWeight = FontWeight.Bold, color = HeritageMaroon)
                        Text("You've mastered the story of ${story.hero.value(language)}.")
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { onEarn(); onComplete() }, colors = ButtonDefaults.buttonColors(containerColor = HeritageMaroon)) {
                            Text("EARN BADGE")
                        }
                    } else {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(80.dp))
                        Text("$correctCount/3 Correct", fontWeight = FontWeight.Bold, color = HeritageMaroon)
                        Text("You need all 3 correct to earn the badge.")
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = {
                            qIdx = 0
                            selected = null
                            correctCount = 0
                            showResult = false
                        }) { Text("RETRY QUIZ") }
                        TextButton(onClick = onCancel) { Text("BACK TO STORY") }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HeritageOffWhite),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Question ${qIdx + 1} of 3", style = MaterialTheme.typography.labelMedium, color = HeritageGold)
                    Text(quiz.question.value(language), style = MaterialTheme.typography.titleLarge, color = HeritageBrown)
                    Spacer(Modifier.height(24.dp))
                    quiz.options.forEachIndexed { i, opt ->
                        val isSelected = selected == i
                        OutlinedButton(
                            onClick = { if (selected == null) selected = i },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) HeritageSandalwood else Color.Transparent,
                                contentColor = HeritageBrown
                            )
                        ) {
                            Text(opt.value(language))
                        }
                    }
                    if (selected != null) {
                        Button(
                            onClick = {
                                if (selected == quiz.answerIndex) correctCount++
                                if (qIdx < story.quizzes.size - 1) {
                                    qIdx++
                                    selected = null
                                } else {
                                    showResult = true
                                }
                            },
                            modifier = Modifier.align(Alignment.End).padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HeritageMaroon),
                            shape = CircleShape
                        ) {
                            Text(if (qIdx < 2) "CONTINUE" else "SEE RESULT")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgesScreen(allStories: List<HeroStory>, language: AppLanguage, badges: List<String>) {
    LazyColumn(Modifier.fillMaxSize().padding(24.dp)) {
        item {
            Text("Achievements", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = HeritageMaroon)
            Text("Mastery earned through perfect scores.", style = MaterialTheme.typography.bodyMedium, color = HeritageGold)
            Spacer(Modifier.height(32.dp))
        }
        if (badges.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Text("Your legacy awaits. Complete a quiz perfectly to earn a badge.", textAlign = TextAlign.Center, color = HeritageBrown.copy(alpha = 0.5f))
                }
            }
        } else {
            items(badges) { id ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = HeritageOffWhite)
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(Modifier.size(56.dp), shape = CircleShape, color = HeritageSandalwood) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = HeritageGold, modifier = Modifier.padding(12.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        val name = allStories.find { it.id == id }?.hero?.value(language) ?: id
                        Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = HeritageBrown)
                    }
                }
            }
        }
    }
}

@Composable
fun StatuesScreen(story: HeroStory) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Sacred Sites", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = HeritageMaroon)
        Spacer(Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(28.dp)),
            colors = CardDefaults.cardColors(containerColor = HeritageOffWhite),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(Modifier.size(80.dp), shape = CircleShape, color = HeritageSandalwood) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.Red, modifier = Modifier.padding(20.dp))
                }
                Spacer(Modifier.height(24.dp))
                Text(story.memorial, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = HeritageBrown)
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, "geo:0,0?q=${story.memorial} ${story.district.english}".toUri())) },
                    colors = ButtonDefaults.buttonColors(containerColor = HeritageMaroon),
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("VIEW ON GOOGLE MAPS", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun resolveHeroImage(id: String): Int = when (id) {
    "bagalkot" -> R.drawable.ranna
    "ballari" -> R.drawable.allama_prabhu
    "belagavi" -> R.drawable.kittur_rani_chennamma
    "b_rural" -> R.drawable.kengal_hanumanthaiah
    "b_urban" -> R.drawable.nadaprabhu_kempegowda
    "bidar" -> R.drawable.gudleppa_hallikeri
    "chamarajanagar" -> R.drawable.dr_rajkumar
    "chikkaballapura" -> R.drawable.sir_m_visvesvaraya
    "chikkamagaluru" -> R.drawable.krishnaraja_wadiyar
    "chitradurga" -> R.drawable.onake_obavva
    "d_kannada" -> R.drawable.rani_abbakka_chowta
    "davangere" -> R.drawable.s_nijalingappa
    "dharwad" -> R.drawable.sangoli_rayanna
    "gadag" -> R.drawable.da_ra_bendre
    "hassan" -> R.drawable.madakari_nayaka
    "haveri" -> R.drawable.shishunala_sharif
    "kalaburagi" -> R.drawable.mailara_mahadevappa
    "kodagu" -> R.drawable.general_thimayya
    "kolar" -> R.drawable.karnad_sadashiva
    "koppal" -> R.drawable.umabai_kundapur
    "mandya" -> R.drawable.shivakumara
    "mysuru" -> R.drawable.tipu_sultan
    "raichur" -> R.drawable.hardekar_manjappa
    "ramanagara" -> R.drawable.k_m_cariappa
    "shivamogga" -> R.drawable.kuvempu
    "tumakuru" -> R.drawable.shivakumara
    "udupi" -> R.drawable.kanakadasa
    "u_kannada" -> R.drawable.mayurasharma
    "vijayapura" -> R.drawable.basavanna
    "yadgir" -> R.drawable.akka_mahadevi
    "vijayanagara" -> R.drawable.adi_kavi_pampa
    else -> R.drawable.kittur_rani_chennamma
}

fun DrawScope.drawHeritageBackground() {
    val step = 40.dp.toPx()
    for (x in 0..size.width.toInt() step step.toInt()) {
        drawLine(HeritageGold.copy(alpha = 0.05f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height))
    }
    for (y in 0..size.height.toInt() step step.toInt()) {
        drawLine(HeritageGold.copy(alpha = 0.05f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()))
    }
}

private fun loadBadges(context: Context): List<String> = context.getSharedPreferences("badges", Context.MODE_PRIVATE).getStringSet("earned", emptySet())?.toList().orEmpty()
private fun saveBadges(context: Context, badges: List<String>) { context.getSharedPreferences("badges", Context.MODE_PRIVATE).edit { putStringSet("earned", badges.toSet()) } }
