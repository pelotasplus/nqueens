package pl.pelotasplus.queens.domain

import androidx.annotation.DrawableRes

data class Avatar(
    val id: Int,
    val name: String,
    val bio: String,
    @DrawableRes val image: Int
)
