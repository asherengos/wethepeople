package com.example.wethepeople.ui.screens.voting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wethepeople.data.model.VoteProposal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlinx.coroutines.delay

class ProposeVoteViewModel : ViewModel() {
    
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting
    
    private val _submissionSuccess = MutableStateFlow(false)
    val submissionSuccess: StateFlow<Boolean> = _submissionSuccess

    private val _moderationResult = MutableStateFlow<ModerationResult?>(null)
    val moderationResult: StateFlow<ModerationResult?> = _moderationResult
    
    private val _showModeration = MutableStateFlow(false)
    val showModeration: StateFlow<Boolean> = _showModeration
    
    // Add new state to track if user acknowledged moderation warnings
    private val _userAcknowledgedWarnings = MutableStateFlow(false)
    val userAcknowledgedWarnings: StateFlow<Boolean> = _userAcknowledgedWarnings

    // In a real app, you would inject these repositories
    // private val proposalRepository: ProposalRepository,
    // private val userRepository: UserRepository
    
    // List of banned words/terms for simple content moderation
    private val bannedTerms = listOf(
        "hate", "racist", "discrimination", "offensive slur", 
        "explicit content", "violence", "threat", "terrorism",
        "nazi", "kill", "bomb", "attack", "murder", "assault",
        "slur", "abuse", "harassment", "illegal", "drug",
        "obscene", "profanity", "explicit", "fraud", "scam",
        // Additional terms from comment filter list
        "arse", "ass", "asshole", "homophobic", "jew", "jewish", "anti-semitic", "chink", 
        "muslims", "muslim", "isis", "islamophobe", "homophobe", "bombing", "sexyhot", 
        "bastard", "bitch", "fucker", "cunt", "damn", "fuck", "goddamn", "shit", 
        "motherfucker", "nigga", "nigger", "prick", "whore", "thot", "slut", "faggot", 
        "dick", "pussy", "penis", "vagina", "negro", "coon", "sexist", "freaking", 
        "cock", "rape", "molest", "anal", "cancer", "sex", "retard", "suicide", "die", 
        "death", "shooting", "shooter", "terrorism", "terror", "pornography", "porn", 
        "pedophile", "pedophilia", "predator"
    )
    
    // Categorized terms for better moderation feedback
    private val termCategories = mapOf(
        "Hate Speech" to listOf("hate", "racist", "discrimination", "homophobic", "anti-semitic", "islamophobe", "homophobe", "sexist", "chink", "nigger", "faggot", "negro", "coon"),
        "Violence" to listOf("violence", "kill", "bomb", "attack", "murder", "assault", "bombing", "terrorism", "terror", "suicide", "die", "death", "shooting", "shooter"),
        "Profanity" to listOf("ass", "asshole", "bastard", "bitch", "fucker", "cunt", "damn", "fuck", "goddamn", "shit", "motherfucker", "prick", "whore", "thot", "slut", "dick", "freaking", "cock"),
        "Sexual Content" to listOf("sexyhot", "pussy", "penis", "vagina", "anal", "sex", "rape", "molest", "pornography", "porn", "explicit content"),
        "Harmful Content" to listOf("obscene", "profanity", "explicit", "isis", "nazi", "predator", "pedophile", "pedophilia")
    )
    
    fun submitProposal(
        title: String,
        description: String,
        category: String,
        options: List<String>,
        supportingEvidence: String = ""
    ) {
        if (title.isBlank() || description.isBlank() || category.isBlank() || options.isEmpty()) {
            return
        }
        
        viewModelScope.launch {
            _isSubmitting.value = true
            
            try {
                // Perform content moderation check
                val moderationResult = performContentModeration(title, description, supportingEvidence)
                
                // Create proposal with moderation info
                val proposal = VoteProposal(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    category = category,
                    options = options,
                    supportingEvidence = supportingEvidence,
                    authorId = "current_user_id", // In reality: userRepository.getCurrentUserId()
                    authorName = "Citizen Soldier", // In reality: userRepository.getCurrentUser().name
                    authorRank = "Patriot", // In reality: userRepository.getCurrentUser().citizenRank.title
                    status = if (moderationResult.automaticReviewTriggered) "pending_moderation" else "approved",
                    createdAt = System.currentTimeMillis(),
                    moderationScore = moderationResult.riskScore,
                    automaticReviewTriggered = moderationResult.automaticReviewTriggered
                )
                
                // proposalRepository.submitProposal(proposal)
                
                // Award patriot points to the user
                // userRepository.awardPatriotPoints(50, "Submitted vote proposal")
                
                // For demo purposes, simulate a delay
                kotlinx.coroutines.delay(1000)
                
                // If moderation flagged the content, store the result but don't show submission success yet
                if (moderationResult.automaticReviewTriggered) {
                    _moderationResult.value = moderationResult
                } else {
                    _submissionSuccess.value = true
                }
            } catch (e: Exception) {
                // Handle error
                _submissionSuccess.value = false
            } finally {
                _isSubmitting.value = false
            }
        }
    }
    
    private fun performContentModeration(
        title: String,
        description: String,
        supportingEvidence: String
    ): ModerationResult {
        val combinedText = "$title $description $supportingEvidence".lowercase()
        
        // Check for banned terms (simple keyword filter)
        val detectedTerms = bannedTerms.filter { term -> 
            combinedText.contains(term.lowercase()) 
        }
        
        // Calculate a risk score (0-1) - in a real app, this would use ML/AI
        val riskScore = when {
            detectedTerms.isNotEmpty() -> 0.9f  // High risk if banned terms found
            description.length < 20 -> 0.6f     // Medium risk if very short description
            else -> calculateRiskScore(combinedText) // Otherwise use more complex analysis
        }
        
        // Determine if automatic review is needed
        val needsReview = riskScore >= 0.7f || detectedTerms.isNotEmpty()
        
        // Find categories for flagged terms to provide better feedback
        val categorizedIssues = if (detectedTerms.isNotEmpty()) {
            getCategorizedIssues(detectedTerms)
        } else {
            emptyMap()
        }
        
        return ModerationResult(
            riskScore = riskScore,
            flaggedTerms = detectedTerms,
            automaticReviewTriggered = needsReview,
            categorizedIssues = categorizedIssues,
            message = if (needsReview) {
                if (detectedTerms.isNotEmpty()) {
                    buildModerationMessage(categorizedIssues, detectedTerms)
                } else {
                    "Your proposal has been flagged for review by our moderation team before publishing."
                }
            } else ""
        )
    }
    
    private fun getCategorizedIssues(detectedTerms: List<String>): Map<String, List<String>> {
        val result = mutableMapOf<String, MutableList<String>>()
        
        for (term in detectedTerms) {
            for ((category, terms) in termCategories) {
                if (terms.contains(term.lowercase())) {
                    result.getOrPut(category) { mutableListOf() }.add(term)
                    break  // Term is categorized, move to next term
                }
            }
        }
        
        // Add "Other" category for any uncategorized terms
        val categorizedTerms = result.values.flatten()
        val uncategorizedTerms = detectedTerms.filter { !categorizedTerms.contains(it) }
        if (uncategorizedTerms.isNotEmpty()) {
            result["Other"] = uncategorizedTerms.toMutableList()
        }
        
        return result
    }
    
    private fun buildModerationMessage(categorizedIssues: Map<String, List<String>>, allTerms: List<String>): String {
        val sb = StringBuilder("Your proposal contains potentially inappropriate content")
        
        if (categorizedIssues.size > 1) {
            sb.append(" in multiple categories: ")
            sb.append(categorizedIssues.keys.joinToString(", "))
        } else if (categorizedIssues.size == 1) {
            sb.append(" related to ${categorizedIssues.keys.first()}")
        }
        
        sb.append(". It will need to be reviewed by our moderation team before publishing.")
        
        return sb.toString()
    }
    
    // Simplified risk score calculation - would be more sophisticated in a real app
    private fun calculateRiskScore(text: String): Float {
        // Implement heuristics or ML model inference
        // This is a placeholder implementation
        var score = 0f
        
        // Length check (extremely short or long)
        if (text.length < 50) score += 0.2f
        if (text.length > 5000) score += 0.3f
        
        // ALL CAPS check
        val uppercaseRatio = text.count { it.isUpperCase() }.toFloat() / text.count { it.isLetter() }.coerceAtLeast(1)
        if (uppercaseRatio > 0.5f) score += 0.3f
        
        // Excessive punctuation
        val punctuationRatio = text.count { it in "!?." }.toFloat() / text.length.coerceAtLeast(1)
        if (punctuationRatio > 0.1f) score += 0.2f
        
        // URL count (many links might indicate spam)
        val urlCount = "https?://\\S+".toRegex().findAll(text).count()
        if (urlCount > 3) score += 0.2f
        
        // Repetition check (repeated characters might indicate spam)
        val repeatedChars = "(.)\\1{4,}".toRegex().findAll(text).count()
        if (repeatedChars > 0) score += 0.2f
        
        // Check for suspicious patterns
        if ("\\$\\d+".toRegex().containsMatchIn(text)) score += 0.2f // Money amounts
        if ("\\d{3}-\\d{2}-\\d{4}".toRegex().containsMatchIn(text)) score += 0.5f // SSN pattern
        
        return score.coerceIn(0f, 1f)
    }
    
    fun dismissModeration() {
        _showModeration.value = false
    }
    
    fun acknowledgeModeration() {
        _userAcknowledgedWarnings.value = true
        _showModeration.value = false
    }
    
    fun resetSubmissionState() {
        _submissionSuccess.value = false
        _moderationResult.value = null
    }
    
    // Data class for moderation results
    data class ModerationResult(
        val riskScore: Float,
        val flaggedTerms: List<String>,
        val automaticReviewTriggered: Boolean,
        val categorizedIssues: Map<String, List<String>> = emptyMap(),
        val message: String
    )
    
    // Factory for creating the ViewModel with dependencies
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProposeVoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProposeVoteViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 