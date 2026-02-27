package me.yasharya.peregerine.core.util

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object Ids {
    @OptIn(ExperimentalUuidApi::class)
    fun newId(): String = Uuid.random().toString()
}