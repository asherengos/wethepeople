package com.example.wethepeople.ui.screens.debate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wethepeople.data.model.Comment
import com.example.wethepeople.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for handling debate comments with emoji-based identification
 */
class DebateViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userRepository = UserRepository.getInstance(application)
    
    // Example comments for demonstration
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments
    
    init {
        // Add some mock comments for demonstration
        viewModelScope.launch {
            addMockComments()
        }
    }
    
    /**
     * Add a new comment to the debate
     */
    fun addComment(content: String, parentId: String? = null) {
        viewModelScope.launch {
            val userEmojiId = userRepository.getUserEmojiId()
            val userRank = userRepository.getUserRank()
            val userLevel = userRepository.getUserLevel()
            
            val newComment = Comment(
                content = content,
                userEmojiId = userEmojiId,
                userRank = userRank,
                userLevel = userLevel,
                parentId = parentId
            )
            
            val currentComments = _comments.value.toMutableList()
            currentComments.add(0, newComment)
            _comments.value = currentComments
        }
    }
    
    /**
     * Like a comment
     */
    fun likeComment(comment: Comment) {
        val updatedComments = _comments.value.toMutableList()
        val index = updatedComments.indexOfFirst { it.id == comment.id }
        
        if (index != -1) {
            // Simple toggle implementation
            val currentLikes = updatedComments[index].likes
            val updatedComment = updatedComments[index].copy(
                likes = if (currentLikes > 0) 0 else 1
            )
            updatedComments[index] = updatedComment
            _comments.value = updatedComments
        }
    }
    
    /**
     * Dislike a comment
     */
    fun dislikeComment(comment: Comment) {
        val updatedComments = _comments.value.toMutableList()
        val index = updatedComments.indexOfFirst { it.id == comment.id }
        
        if (index != -1) {
            // Simple toggle implementation
            val currentDislikes = updatedComments[index].dislikes
            val updatedComment = updatedComments[index].copy(
                dislikes = if (currentDislikes > 0) 0 else 1
            )
            updatedComments[index] = updatedComment
            _comments.value = updatedComments
        }
    }
    
    /**
     * Flag a comment
     */
    fun flagComment(comment: Comment) {
        val updatedComments = _comments.value.toMutableList()
        val index = updatedComments.indexOfFirst { it.id == comment.id }
        
        if (index != -1) {
            val updatedComment = updatedComments[index].copy(
                isFlagged = !updatedComments[index].isFlagged,
                flags = updatedComments[index].flags + 1
            )
            updatedComments[index] = updatedComment
            _comments.value = updatedComments
        }
    }
    
    /**
     * Add mock comments for demonstration
     */
    private fun addMockComments() {
        val mockComments = mutableListOf<Comment>()
        
        // Parent comments
        val comment1 = Comment(
            content = "I think this is a really important issue that affects all citizens. We should focus on solutions rather than partisan politics.",
            userEmojiId = "🦅 🗽 ⭐",
            userRank = "Patriot",
            userLevel = 15,
            likes = 5
        )
        
        val comment2 = Comment(
            content = "Has anyone read the proposal documents? I'm curious about the implementation details.",
            userEmojiId = "🎆 🏛️ 🇺🇸",
            userRank = "Citizen",
            userLevel = 3,
            likes = 2
        )
        
        val comment3 = Comment(
            content = "This reminds me of a similar initiative from 2018. Let's learn from what worked and what didn't.",
            userEmojiId = "📜 🔔 🎖️",
            userRank = "Veteran",
            userLevel = 28,
            likes = 12,
            dislikes = 1
        )
        
        mockComments.add(comment1)
        mockComments.add(comment2)
        mockComments.add(comment3)
        
        // Add replies
        mockComments.add(
            Comment(
                content = "I agree. I've been researching this topic and there are some promising bipartisan approaches.",
                userEmojiId = "🏆 🧢 🗳️",
                userRank = "Scholar",
                userLevel = 9,
                parentId = comment1.id,
                likes = 3
            )
        )
        
        mockComments.add(
            Comment(
                content = "Yes, check page 24-26 of the main document. The funding mechanism is explained there.",
                userEmojiId = "🎪 🎯 🎨",
                userRank = "Citizen",
                userLevel = 5,
                parentId = comment2.id,
                likes = 4
            )
        )
        
        _comments.value = mockComments
    }
} 