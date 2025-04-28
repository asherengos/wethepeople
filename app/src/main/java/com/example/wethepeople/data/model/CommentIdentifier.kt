package com.example.wethepeople.data.model

import kotlin.random.Random

/**
 * Manages anonymous emoji-based identifiers for users in comment sections.
 * Each user gets a unique combination of three America-themed emojis as their identifier.
 */
class CommentIdentifier {
    companion object {
        // List of American-themed emojis for user identification
        private val patrioticEmojis = listOf(
            "🇺🇸", "🦅", "🗽", "🔔", "🏛️", "📜", "⭐", "🎆", "🗳️",
            "🎖️", "🎇", "🏆", "🚢", "🧢", "🪶", "🎪", "🎭", "🎯", "🎨"
        )

        /**
         * Generates a unique identifier consisting of three emojis
         * based on a hash of the device ID and current time.
         */
        fun generateUniqueEmojiId(seedValue: Int? = null): String {
            val seed = seedValue ?: Random.nextInt()
            val random = Random(seed)
            
            // Select three different emojis for the identifier
            val selectedEmojis = mutableListOf<String>()
            while (selectedEmojis.size < 3) {
                val emoji = patrioticEmojis[random.nextInt(patrioticEmojis.size)]
                if (!selectedEmojis.contains(emoji)) {
                    selectedEmojis.add(emoji)
                }
            }
            
            return selectedEmojis.joinToString(" ")
        }

        /**
         * Creates a full display name using emoji identifier and rank
         */
        fun createDisplayName(emojiId: String, rank: String, level: Int): String {
            return "$emojiId · $rank Lv.$level"
        }
        
        /**
         * Creates a shortened display name for more compact displays
         */
        fun createShortDisplayName(emojiId: String): String {
            return emojiId
        }
    }
} 