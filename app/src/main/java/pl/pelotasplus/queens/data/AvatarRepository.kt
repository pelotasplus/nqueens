package pl.pelotasplus.queens.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.domain.Avatar
import javax.inject.Inject

class AvatarRepository @Inject constructor() {

    fun getAvatars(): Flow<List<Avatar>> {
        return flowOf(
            listOf(
                Avatar(
                    id = 1,
                    name = "Rita",
                    bio = "Rita is the wise queen of the garden kingdom. She spends her days watching butterflies and giving advice to lost bugs.\nLikes: Belly rubs, Sunny naps.\nDislikes: Loud thunder, Soggy grass.",
                    image = R.drawable.avatar1
                ),
                Avatar(
                    id = 2,
                    name = "Lola",
                    bio = "Lola is the fastest tail-wagger in the land! She's always ready for an adventure, especially if it involves snacks.\nLikes: Cheese cubes, Chasing her tail.\nDislikes: Vacuums, Bedtime.",
                    image = R.drawable.avatar2
                ),
                Avatar(
                    id = 3,
                    name = "Jack",
                    bio = "Jack is the brave protector of the backyard realm. He barks at anything suspicious and dreams of heroic squirrel chases.\nLikes: Squirrels, Barking.\nDislikes: Mailman, Snakes.",
                    image = R.drawable.avatar3
                ),
                Avatar(
                    id = 4,
                    name = "Pablo",
                    bio = "Pablo is the royal artist, known for decorating his doghouse with leaves and sticks. He's got a big heart and a goofy grin.\nLikes: Mud puddles, Music.\nDislikes: Baths, Closed doors.",
                    image = R.drawable.avatar4
                )
            )
        )
    }

    fun getAvatar(avatarId: Int): Flow<Avatar> =
        getAvatars()
            .map { avatars -> avatars.first { it.id == avatarId } }
}
