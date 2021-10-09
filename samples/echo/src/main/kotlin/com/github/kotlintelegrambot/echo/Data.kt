package com.github.kotlintelegrambot.echo

import java.io.*

object Data {

    private var dataList = listOf<ReactedMessage>()
    private val file = "reactions"


    data class ReactedMessage(
        val messageId: Long,
        val reactions: List<ReactInfo>
    ) : Serializable

    data class ReactInfo(
        val emoji: String,
        val userIds: List<Long>
    ) : Serializable

    val stubMessage = ReactedMessage(0, emptyList())

    fun readReactions() {
        ObjectInputStream(FileInputStream(file)).use { it ->
            //Read the family back from the file
            val restedReactions = it.readObject()
            dataList = restedReactions as List<ReactedMessage>

            println("From Memory:")
            println("$restedReactions")
        }
    }

    private fun saveToFile() {
        val deleted = File(file).delete()
        println("previousDeleted $deleted")
        ObjectOutputStream(FileOutputStream(file)).use { it -> it.writeObject(dataList) }
        println("Wrote $file")
    }

    fun handle(emoji: String, messageId: Long, userId: Long): ReactedMessage {
        val foundedMessage = dataList.find { it.messageId == messageId }
        val messageExists = foundedMessage != null

        if (messageExists) {
            handleExistedMessage(emoji, messageId, userId)
        } else {
            val reactedMessage = ReactedMessage(
                messageId = messageId,
                reactions = listOf(ReactInfo(emoji, listOf(userId)))
            )
            dataList = dataList + listOf(reactedMessage)
        }
        garbageMessages()
        println("handled $dataList")

        saveToFile()
        return dataList.find { it.messageId == messageId } ?: stubMessage
    }

    fun changeMessageId(oldId: Long, newId: Long) {
        dataList = dataList.map { reactedMessage ->
            return@map if (reactedMessage.messageId == oldId) {
                val updatedMessage = reactedMessage.copy(messageId = newId)
                updatedMessage
            } else {
                reactedMessage
            }
        }
        println("updated $dataList")
    }

    private fun garbageMessages() {
        dataList = dataList.flatMap parent@{ reactedMessage ->
            val clearedReactions = reactedMessage.reactions.flatMap { reaction ->
                return@flatMap if (reaction.userIds.isNotEmpty()) {
                    listOf(reaction)
                } else {
                    emptyList()
                }
            }

            return@parent if (clearedReactions.isNotEmpty()) {
                listOf(reactedMessage.copy(reactions = clearedReactions))
            } else {
                emptyList()
            }
        }
        println("garbaged $dataList")
    }

    private fun handleExistedMessage(emoji: String, messageId: Long, userId: Long) {
        dataList = dataList.map { reactedMessage ->
            if (messageId == reactedMessage.messageId) {
                val foundedReaction = reactedMessage.reactions.find { it.emoji == emoji }
                val reactionExists = foundedReaction != null

                val clickedUsers = reactedMessage.reactions.flatMap { it.userIds }
                val foundedUser = clickedUsers.find { it == userId }
                val userClicked = foundedUser != null

                var updatedReactions: List<ReactInfo>

                if (reactionExists) {
                    if (userClicked) {
                        updatedReactions = reactedMessage.reactions.map react@{ reactInfo ->
                            return@react if (reactInfo.emoji == emoji) {
                                reactInfo.copy(userIds = clickedUsers - listOf(userId))
                            } else {
                                reactInfo
                            }
                        }
                    } else {
                        updatedReactions = reactedMessage.reactions.map react@{ reactInfo ->
                            return@react if (reactInfo.emoji == emoji) {
                                reactInfo.copy(userIds = clickedUsers + listOf(userId))
                            } else {
                                reactInfo
                            }
                        }
                    }
                } else {
                    val newReaction = ReactInfo(
                        emoji = emoji,
                        userIds = listOf(userId)
                    )
                    updatedReactions = reactedMessage.reactions + listOf(newReaction)
                }
                return@map ReactedMessage(
                    messageId = messageId,
                    reactions = updatedReactions
                )
            } else {
                reactedMessage
            }
        }
    }
}
