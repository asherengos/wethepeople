package com.example.partyofthepeople.ui.utils

import com.example.partyofthepeople.ui.components.Comment
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random
import kotlin.math.abs

/**
 * Utility class for generating sample patriotic comments to populate the discussion section
 * when a new proposal is created or when there are no comments yet.
 */
class PatrioticCommentGenerator {
    private val db = FirebaseFirestore.getInstance()
    private val random = Random.Default
    
    // Patriotic usernames
    private val patrioticUsernames = listOf(
        "1776Freedom",
        "ConstitutionDefender",
        "LibertyOrDeath",
        "WeTruePeople",
        "PatriotEagle",
        "AmericaFirst",
        "FreedomFighter1791",
        "BaldEagleSpirit",
        "LibertysCall",
        "ConstitutionalPatriot",
        "We_The_People",
        "AmericanDreamer",
        "IndependenceDay",
        "FreedomRinger",
        "PatriotAct",
        "LandOfLiberty",
        "NationUnited",
        "FoundingFather",
        "RevolutionarySpirit",
        "DemocracyDefender"
    )
    
    // Patriotic phrases for comments
    private val patrioticPhrases = listOf(
        "🇺🇸 For the Republic!",
        "🦅 Freedom prevails!",
        "🗽 Liberty and justice!",
        "🎆 United we stand!",
        "⭐ God bless America!",
        "🏛️ By the people, for the people!",
        "🗳️ Democracy in action!",
        "🎭 The voice of freedom!",
        "🌟 American dream lives on!",
        "🎪 The greatest show on Earth!"
    )
    
    // Criticisms with patriotic flavor
    private val patrioticCriticisms = listOf(
        "I have concerns about how this upholds our constitutional values.",
        "We must ensure this preserves our fundamental liberties.",
        "Let's carefully consider how this impacts our freedom.",
        "Our founding documents should guide us in this decision.",
        "Does this truly represent the will of the people?",
        "I question if this is what our founders envisioned.",
        "We should debate this further for the sake of democracy.",
        "Is this the best way to safeguard our liberties?",
        "Let's remember the costs our patriots paid for our freedom.",
        "In the spirit of healthy debate, I must respectfully disagree.",
        "A healthy republic requires thoughtful opposition to proposals.",
        "I stand for freedom, and I'm not sure this proposal does the same.",
        "Let's weigh this against the principles in our Bill of Rights.",
        "While I love this nation, I cannot support this proposal.",
        "The marketplace of ideas requires us to question this approach."
    )
    
    // Sample political affiliations for diversity of viewpoints
    private val politicalAffiliations = listOf(
        "Republican", "Democrat", "Libertarian", "Independent", "Constitution Party"
    )
    
    // Sample titles to show progression and achievement
    private val patrioticTitles = listOf(
        "Founding Voter",
        "Freedom Advocate",
        "Constitutional Scholar",
        "Dedicated Patriot",
        "Voice of Liberty",
        null // Some users don't have titles yet
    )
    
    // Generic supportive comments that could apply to many proposals
    private val supportiveCommentTemplates = listOf(
        "I fully support this proposal! It aligns perfectly with the principles our founding fathers envisioned for this great nation.",
        "This is exactly the kind of innovation that makes America exceptional. Proud to support!",
        "As a freedom-loving patriot, I believe this proposal will strengthen our democracy and preserve our liberties.",
        "The Constitution was designed for exactly this kind of citizen-led initiative. Count me in!",
        "Finally, a proposal that puts We The People first! This gets my enthusiastic support.",
        "This proposal represents the best of American ingenuity and problem-solving. Let's make it happen!",
        "Our founders would be proud to see citizens taking such an active role in governance. I support this 100%.",
        "This is a shining example of how our democratic republic should function. Full support from this patriot!",
        "I've carefully considered the implications, and this proposal deserves every freedom fighter's support.",
        "Liberty thrives when citizens are engaged. This proposal has my full backing!"
    )
    
    // Generic opposing comments that could apply to many proposals
    private val opposingCommentTemplates = listOf(
        "While I respect the initiative, I believe this proposal overreaches what our Constitution intended.",
        "I must respectfully oppose this proposal as it may infringe on individual liberties our founders fought to secure.",
        "As a defender of constitutional principles, I have concerns about the precedent this would set.",
        "Our founding fathers would caution against this approach. I must vote no while respecting the democratic process.",
        "This proposal, though well-intentioned, seems to expand government beyond its proper boundaries.",
        "With respect to my fellow patriots, I believe this proposal misinterprets our founding documents.",
        "Freedom requires vigilance. I oppose this proposal because it may limit liberty in subtle ways.",
        "I've studied the Constitution carefully, and I believe this proposal conflicts with its original intent.",
        "While I appreciate the patriotic spirit behind this, I must stand for limited government and oppose.",
        "The principles of 1776 guide my decision to respectfully oppose this particular measure."
    )
    
    // General neutral or questioning comments
    private val neutralCommentTemplates = listOf(
        "Has anyone considered the long-term constitutional implications of this proposal?",
        "I'm interested in hearing more about how this aligns with our founding documents.",
        "While I see merits on both sides, I wonder how George Washington would have viewed this?",
        "The debate around this proposal exemplifies the healthy discourse our founders envisioned.",
        "I'm carefully weighing both the liberty aspects and the practical implementation of this proposal.",
        "Have we fully examined how this proposal impacts the separation of powers?",
        "I appreciate the discussion this has generated. This is democracy in action!",
        "The Constitution provides us with guidance here, but reasonable patriots can interpret it differently.",
        "I'm curious how fellow freedom fighters in other districts are viewing this particular proposal?",
        "This is precisely the kind of issue that demonstrates why civic engagement matters!"
    )
    
    /**
     * Generates a random sample patriotic comment
     */
    private fun generateRandomComment(_postId: String, supportLevel: CommentSupportLevel): Comment {
        val username = patrioticUsernames.random()
        val userId = username.hashCode().toString() // Deterministic ID for consistency
        val politicalAffiliation = politicalAffiliations.random()
        val title = patrioticTitles.random()
        
        val content = when (supportLevel) {
            CommentSupportLevel.SUPPORTIVE -> supportiveCommentTemplates.random()
            CommentSupportLevel.OPPOSING -> opposingCommentTemplates.random()
            CommentSupportLevel.NEUTRAL -> neutralCommentTemplates.random()
        }
        
        // Use a timestamp within the last few days so they appear recent
        val daysAgo = Random.nextInt(0, 4)
        val hoursAgo = Random.nextInt(0, 24)
        val minutesAgo = Random.nextInt(0, 60)
        val timestamp = System.currentTimeMillis() - 
                        (daysAgo * 24 * 60 * 60 * 1000) - 
                        (hoursAgo * 60 * 60 * 1000) - 
                        (minutesAgo * 60 * 1000)
        
        // Generate some initial likes/dislikes to show engagement
        val likes = Random.nextInt(1, 15)
        val dislikes = Random.nextInt(0, 5)
        
        return Comment(
            id = "",
            postId = _postId,
            userId = userId,
            username = username,
            text = content,
            timestamp = timestamp,
            likes = likes,
            dislikes = dislikes
        )
    }
    
    /**
     * Adds sample patriotic comments to a post if it doesn't have any yet
     * @param postId The ID of the post to add comments to
     * @param count The number of sample comments to add
     */
    fun addSampleCommentsIfNeeded(postId: String, count: Int = 5) {
        // First check if there are already comments for this post
        db.collection("comments")
            .whereEqualTo("postId", postId)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    // No comments exist, add sample ones
                    addSampleComments(postId, count)
                }
            }
    }
    
    /**
     * Adds a specific number of sample comments to a post with a mix of supportive, opposing,
     * and neutral viewpoints to represent a healthy democratic debate.
     */
    private fun addSampleComments(postId: String, count: Int) {
        val comments = mutableListOf<Comment>()
        
        // Ensure a good mix of comment types - slightly biased toward supportive
        val supportiveCount = (count * 0.5).toInt().coerceAtLeast(1)
        val opposingCount = (count * 0.3).toInt().coerceAtLeast(1)
        val neutralCount = count - supportiveCount - opposingCount
        
        // Generate supportive comments
        repeat(supportiveCount) {
            comments.add(generateRandomComment(postId, CommentSupportLevel.SUPPORTIVE))
        }
        
        // Generate opposing comments
        repeat(opposingCount) {
            comments.add(generateRandomComment(postId, CommentSupportLevel.OPPOSING))
        }
        
        // Generate neutral comments
        repeat(neutralCount) {
            comments.add(generateRandomComment(postId, CommentSupportLevel.NEUTRAL))
        }
        
        // Randomize the order so it's not too predictable
        comments.shuffle()
        
        // Add all comments to Firestore
        val batch = db.batch()
        comments.forEach { comment ->
            val commentRef = db.collection("comments").document()
            batch.set(commentRef, comment)
        }
        
        batch.commit()
    }
    
    enum class CommentSupportLevel {
        SUPPORTIVE, OPPOSING, NEUTRAL
    }
    
    // Generate a realistic comment
    fun generateComment(username: String, text: String): String {
        val randomPhrase = patrioticPhrases[random.nextInt(patrioticPhrases.size)]
        return "$text\n\n$randomPhrase"
    }
    
    // Generate a list of comments with a mix of positive and negative
    fun generateComments(count: Int): List<Comment> {
        val usernames = listOf(
            "PatriotWarrior1776",
            "FreedomFighter2024",
            "ConstitutionDefender",
            "LibertyGuardian",
            "DemocracyChampion",
            "AmericanEagle",
            "RepublicDefender",
            "VoiceOfLiberty",
            "PatriotPride",
            "FreedomRinger"
        )
        
        val criticisms = listOf(
            "This meme perfectly captures the spirit of our great nation! 🇺🇸",
            "Finally, someone speaking truth to power! Keep it up! 👏",
            "This is what real patriotism looks like! Share if you agree! 🦅",
            "Couldn't have said it better myself! God bless America! 🙏",
            "This is why I love this app! Real patriots unite! ⭐",
            "The silent majority stands with you! Keep fighting! 💪",
            "This deserves to go viral! Wake up, America! 🔔",
            "Now this is what I call quality political discourse! 📢",
            "You've earned my super vote! True patriot right here! 🗳️",
            "This is the content we need! Spread the word! 📣"
        )
        
        return List(count) { index ->
            val username = usernames[random.nextInt(usernames.size)]
            val text = criticisms[random.nextInt(criticisms.size)]
            Comment(
                id = index.toString(),
                postId = "",
                userId = abs(username.hashCode()).toString(),
                username = username,
                text = generateComment(username, text),
                timestamp = System.currentTimeMillis() - random.nextLong(0, 86400000), // Random time within last 24 hours
                likes = random.nextInt(0, 1000),
                dislikes = random.nextInt(0, 100)
            )
        }
    }
} 