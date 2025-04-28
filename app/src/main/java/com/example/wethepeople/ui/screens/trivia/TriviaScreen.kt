package com.example.wethepeople.ui.screens.trivia

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.ui.components.*
import com.example.wethepeople.ui.theme.*

data class TriviaQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

data class VotingFact(
    val id: String,
    val title: String,
    val content: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriviaScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("TRIVIA", "FACTS", "RESOURCES")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LEARN & EARN") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = patriot_dark_blue,
                    titleContentColor = patriot_white
                )
            )
        },
        containerColor = patriot_dark_blue
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = patriot_medium_blue,
                contentColor = patriot_white
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = font_sm.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            // Content
            when (selectedTab) {
                0 -> TriviaContent()
                1 -> FactsContent()
                2 -> ResourcesContent()
            }
        }
    }
}

@Composable
private fun TriviaContent(
    modifier: Modifier = Modifier
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showExplanation by remember { mutableStateOf(false) }

    val questions = remember {
        listOf(
            TriviaQuestion(
                id = "1",
                question = "Which amendment gave women the right to vote?",
                options = listOf(
                    "15th Amendment",
                    "19th Amendment",
                    "21st Amendment",
                    "26th Amendment"
                ),
                correctAnswer = "19th Amendment",
                explanation = "The 19th Amendment, ratified in 1920, prohibits denying or abridging the right to vote on the basis of sex."
            ),
            // Add more questions here
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = (currentQuestionIndex + 1).toFloat() / questions.size,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = patriot_red_bright,
            trackColor = patriot_blue_light
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question
        Text(
            text = questions[currentQuestionIndex].question,
            color = patriot_white,
            fontSize = font_lg.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Options
        questions[currentQuestionIndex].options.forEach { option ->
            VoteOption(
                text = option,
                isSelected = option == selectedAnswer,
                onSelect = {
                    selectedAnswer = option
                    showExplanation = true
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Explanation
        AnimatedVisibility(
            visible = showExplanation,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedAnswer == questions[currentQuestionIndex].correctAnswer)
                        success_green.copy(alpha = 0.1f)
                    else
                        error_red.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (selectedAnswer == questions[currentQuestionIndex].correctAnswer)
                            "Correct!"
                        else
                            "Incorrect",
                        color = if (selectedAnswer == questions[currentQuestionIndex].correctAnswer)
                            success_green
                        else
                            error_red,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = questions[currentQuestionIndex].explanation,
                        color = patriot_white
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PatriotButton(
                        text = if (currentQuestionIndex < questions.size - 1)
                            "NEXT QUESTION"
                        else
                            "FINISH",
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                selectedAnswer = null
                                showExplanation = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun FactsContent(
    modifier: Modifier = Modifier
) {
    val facts = remember {
        listOf(
            VotingFact(
                id = "1",
                title = "First Presidential Election",
                content = "George Washington was elected unanimously by the Electoral College in the first presidential election in 1789.",
                category = "Historical"
            ),
            // Add more facts here
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "DID YOU KNOW?",
                color = patriot_gold,
                fontSize = font_xl.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(facts) { fact ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = patriot_medium_blue
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = fact.title,
                        color = patriot_white,
                        fontSize = font_lg.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = fact.content,
                        color = patriot_white.copy(alpha = 0.8f),
                        fontSize = font_md.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = fact.category,
                        color = patriot_gold,
                        fontSize = font_sm.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ResourcesContent(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "EDUCATIONAL RESOURCES",
                color = patriot_white,
                fontSize = font_xl.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = patriot_medium_blue
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Voting Rights History",
                        color = patriot_white,
                        fontSize = font_lg.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Learn about the history of voting rights in America",
                        color = patriot_white.copy(alpha = 0.8f),
                        fontSize = font_md.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    PatriotButton(
                        text = "START LEARNING",
                        onClick = { /* Navigate to history section */ }
                    )
                }
            }
        }

        // Add more educational resources here
    }
} 