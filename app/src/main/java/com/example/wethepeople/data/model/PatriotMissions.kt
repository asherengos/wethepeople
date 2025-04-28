package com.example.wethepeople.data.model

import androidx.annotation.DrawableRes
import com.example.wethepeople.R

enum class MissionCategory {
    VOTING,
    DEBATING,
    COMMUNITY,
    EDUCATION,
    PATRIOT_DUTY,
    SPECIAL_EVENT
}

enum class MissionFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    SPECIAL,
    ACHIEVEMENT
}

enum class MissionStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    CLAIMED
}

data class PatriotMission(
    val id: String,
    val title: String,
    val description: String,
    val category: MissionCategory,
    val frequency: MissionFrequency,
    val patriotPointsReward: Int,
    val freedomBucksReward: Int = 0,
    val requiredProgress: Int = 1,
    val currentProgress: Int = 0,
    val status: MissionStatus = MissionStatus.NOT_STARTED,
    @DrawableRes val iconResId: Int? = null,
    val chainedMissionId: String? = null // For missions that unlock after completing this one
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val category: MissionCategory,
    val tier: Int, // 1-5, representing Bronze, Silver, Gold, Platinum, Diamond
    val patriotPointsReward: Int,
    val requiredProgress: Int,
    val currentProgress: Int = 0,
    val isUnlocked: Boolean = false,
    @DrawableRes val iconResId: Int? = null
)

data class CollectionLevel(
    val level: Int,
    val requiredPoints: Int,
    val rewards: List<CollectionReward>
)

data class CollectionReward(
    val type: RewardType,
    val amount: Int,
    @DrawableRes val iconResId: Int? = null,
    val specialItemId: String? = null // For special items like hats, borders, etc.
)

enum class RewardType {
    PATRIOT_POINTS,
    FREEDOM_BUCKS,
    SPECIAL_ITEM,
    TITLE,
    BADGE
}

object PatriotMissionSystem {
    // Daily Missions
    val dailyMissions = listOf(
        PatriotMission(
            id = "daily_vote",
            title = "Daily Voter",
            description = "Cast your vote in 3 different polls",
            category = MissionCategory.VOTING,
            frequency = MissionFrequency.DAILY,
            patriotPointsReward = 30,
            requiredProgress = 3
        ),
        PatriotMission(
            id = "daily_debate",
            title = "Voice of Democracy",
            description = "Participate in 2 debate discussions",
            category = MissionCategory.DEBATING,
            frequency = MissionFrequency.DAILY,
            patriotPointsReward = 25,
            requiredProgress = 2
        ),
        PatriotMission(
            id = "daily_learn",
            title = "Informed Citizen",
            description = "Read 3 articles about current events",
            category = MissionCategory.EDUCATION,
            frequency = MissionFrequency.DAILY,
            patriotPointsReward = 20,
            requiredProgress = 3
        )
    )

    // Weekly Missions
    val weeklyMissions = listOf(
        PatriotMission(
            id = "weekly_debate_master",
            title = "Master Debater",
            description = "Win 5 debates with high approval ratings",
            category = MissionCategory.DEBATING,
            frequency = MissionFrequency.WEEKLY,
            patriotPointsReward = 100,
            freedomBucksReward = 50,
            requiredProgress = 5
        ),
        PatriotMission(
            id = "weekly_community_leader",
            title = "Community Leader",
            description = "Get 50 upvotes on your comments",
            category = MissionCategory.COMMUNITY,
            frequency = MissionFrequency.WEEKLY,
            patriotPointsReward = 150,
            requiredProgress = 50
        ),
        PatriotMission(
            id = "weekly_constitution",
            title = "Constitution Scholar",
            description = "Complete 3 Constitution quizzes with perfect scores",
            category = MissionCategory.EDUCATION,
            frequency = MissionFrequency.WEEKLY,
            patriotPointsReward = 200,
            freedomBucksReward = 100,
            requiredProgress = 3
        )
    )

    // Achievements
    val achievements = listOf(
        Achievement(
            id = "perfect_voter",
            title = "Perfect Voter",
            description = "Vote in 50 consecutive polls without missing any",
            category = MissionCategory.VOTING,
            tier = 5,
            patriotPointsReward = 500,
            requiredProgress = 50
        ),
        Achievement(
            id = "debate_champion",
            title = "Debate Champion",
            description = "Win 100 debates",
            category = MissionCategory.DEBATING,
            tier = 4,
            patriotPointsReward = 400,
            requiredProgress = 100
        ),
        Achievement(
            id = "constitution_master",
            title = "Constitution Master",
            description = "Answer 1000 constitution questions correctly",
            category = MissionCategory.EDUCATION,
            tier = 5,
            patriotPointsReward = 1000,
            requiredProgress = 1000
        ),
        Achievement(
            id = "community_pillar",
            title = "Community Pillar",
            description = "Receive 1000 upvotes on your contributions",
            category = MissionCategory.COMMUNITY,
            tier = 4,
            patriotPointsReward = 500,
            requiredProgress = 1000
        ),
        Achievement(
            id = "freedom_fighter",
            title = "Freedom Fighter",
            description = "Complete 100 missions of any type",
            category = MissionCategory.PATRIOT_DUTY,
            tier = 5,
            patriotPointsReward = 1000,
            requiredProgress = 100
        )
    )

    // Collection Levels (similar to Marvel Snap's collection level)
    val collectionLevels = (1..100).map { level ->
        CollectionLevel(
            level = level,
            requiredPoints = level * 100,
            rewards = when {
                level % 10 == 0 -> listOf( // Major milestone every 10 levels
                    CollectionReward(RewardType.PATRIOT_POINTS, 500),
                    CollectionReward(RewardType.FREEDOM_BUCKS, 200),
                    CollectionReward(RewardType.SPECIAL_ITEM, 1)
                )
                level % 5 == 0 -> listOf( // Medium milestone every 5 levels
                    CollectionReward(RewardType.PATRIOT_POINTS, 250),
                    CollectionReward(RewardType.FREEDOM_BUCKS, 100)
                )
                else -> listOf( // Regular level rewards
                    CollectionReward(RewardType.PATRIOT_POINTS, 100),
                    CollectionReward(RewardType.FREEDOM_BUCKS, 50)
                )
            }
        )
    }

    // Special Event Missions (can be updated with new events)
    var specialEventMissions = listOf(
        PatriotMission(
            id = "election_day_2024",
            title = "Election Day Champion",
            description = "Complete all voting-related tasks on Election Day",
            category = MissionCategory.SPECIAL_EVENT,
            frequency = MissionFrequency.SPECIAL,
            patriotPointsReward = 1000,
            freedomBucksReward = 500,
            requiredProgress = 1
        ),
        PatriotMission(
            id = "constitution_day",
            title = "Constitution Day Celebration",
            description = "Participate in all Constitution Day events",
            category = MissionCategory.SPECIAL_EVENT,
            frequency = MissionFrequency.SPECIAL,
            patriotPointsReward = 500,
            freedomBucksReward = 250,
            requiredProgress = 1
        )
    )

    // Mission Chains (missions that unlock after completing others)
    val missionChains = mapOf(
        "voting_chain" to listOf(
            PatriotMission(
                id = "vote_beginner",
                title = "Novice Voter",
                description = "Cast your first vote",
                category = MissionCategory.VOTING,
                frequency = MissionFrequency.ACHIEVEMENT,
                patriotPointsReward = 50,
                chainedMissionId = "vote_intermediate"
            ),
            PatriotMission(
                id = "vote_intermediate",
                title = "Regular Voter",
                description = "Cast 50 votes",
                category = MissionCategory.VOTING,
                frequency = MissionFrequency.ACHIEVEMENT,
                patriotPointsReward = 200,
                requiredProgress = 50,
                chainedMissionId = "vote_advanced"
            ),
            PatriotMission(
                id = "vote_advanced",
                title = "Master Voter",
                description = "Cast 500 votes",
                category = MissionCategory.VOTING,
                frequency = MissionFrequency.ACHIEVEMENT,
                patriotPointsReward = 1000,
                requiredProgress = 500
            )
        )
    )
} 