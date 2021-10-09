package com.github.kotlintelegrambot.echo

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.echo.Data.changeMessageId
import com.github.kotlintelegrambot.echo.Data.handle
import com.github.kotlintelegrambot.echo.Data.readReactions
import com.github.kotlintelegrambot.entities.*
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.extensions.filters.Filter
import java.lang.NumberFormatException
import java.util.regex.Pattern
import kotlin.math.absoluteValue
import kotlin.random.Random

const val LOUD_TIME = 30000L
const val LOUD_PERIOD_TIME = 1000L * 60L * 60L * 3L

val listLoudOfUsers = mutableMapOf<User, Long>()
val listOfUsersBucks = mutableMapOf<User, Int>()

val loudMessages = listOf("lifeloud", "loudsound")

val helloMessages = listOf(
    "здарова",
    "привет",
    "здаров",
    "здорова",
    "здоров",
    "ку",
    "hello",
    "йоу",
    "ебана",
    "пошел нахуй",
    "пошёл нахуй",
    "пшёл нах",
    "пшёл нахуй",
    "ебись",
    "оборванец",
    "ушлёпок",
    "гандон",
    "я ебу?",
    "я ебу",
    "сука",
    "блять",
    "ну, ага",
    "иди в жопу",
    "ебён сандаль",
    "в рот те ноги",
    "дай деняг",
    "го покурим",
    "го пиво",
    "чд",
    "да да",
    "да да нет нет",
    "...",
    "ссал на тебя",
    "убью",
    "собака",
    "я занят",
    "ну и хуле",
    "да ниче тут не поделаешь",
    "bruh",
    "скидыщ донъ делай донъ",
    "донъ",
    "как как",
    "ну типа",
    "чичи гага печенье курага",
    "включите ак-47",
    "дайте Саше эссу",
    "дайте Саше эссу",
    "ПИВО ХОЧУ БЛЯТЬ",
    "КАЛЬЯН ХОЧУ БЛЯТЬ",
    "норм перданули вчера",
    "в чем правда?",
    "а включите Suicide Boys",
    "я вам дам щяс",
    "ну да, ага",
    "а ещё че?",
    "бе ме",
    "му хрю",
    "порошок",
    "че надо?",
    "а может не надо?",
    "хватит",
    "бля иди поспи",
    "соси",
    "а поехалите на море",
    "кокрас жирный аванс придёт и го",
    "щя бы на карьер",
    "мдеее",
    "пиво где",
    "хейтеры ебаные",
    "как какать то",
    "в глаз дам",
    "уф",
    "отвалите",
    "я сру",
    "я ссу",
    "обожди",
    "попа",
    "нормально",
    "как дела?",
    "поал",
    "Джон лох",
    "Саня лох",
    "Горшок лох",
    "Леха лох",
    "Даша лох",
    "Лия лох",
    "Геля лох",
    "Степан лох",
    "Злата лох",
    "Уээээ чипан!",
    "...",
    " ",
)

val listOfPashaNames = listOf(
    "паша",
    "пашу",
    "пашы",
    "паши",
    "пиши",
    "пышы",
    "паш",
    "пашок",
    "пашек",
    "порошок",
    "павел",
    "пашенька",
    "pavel",
)


//val spbChatId = -1001208790558
val spbChatId = -1L

var pavelLoudMessageSendTime = System.currentTimeMillis()
var isPashaInitilized = false
var expectLoud = false
var expectedLoud = ""

val emojiPattern = Pattern.compile(
    "^[\\s\n\r]*(?:(?:[\u00a9\u00ae\u203c\u2049\u2122\u2139\u2194-\u2199\u21a9-\u21aa\u231a-\u231b\u2328\u23cf\u23e9-\u23f3\u23f8-\u23fa\u24c2\u25aa-\u25ab\u25b6\u25c0\u25fb-\u25fe\u2600-\u2604\u260e\u2611\u2614-\u2615\u2618\u261d\u2620\u2622-\u2623\u2626\u262a\u262e-\u262f\u2638-\u263a\u2648-\u2653\u2660\u2663\u2665-\u2666\u2668\u267b\u267f\u2692-\u2694\u2696-\u2697\u2699\u269b-\u269c\u26a0-\u26a1\u26aa-\u26ab\u26b0-\u26b1\u26bd-\u26be\u26c4-\u26c5\u26c8\u26ce-\u26cf\u26d1\u26d3-\u26d4\u26e9-\u26ea\u26f0-\u26f5\u26f7-\u26fa\u26fd\u2702\u2705\u2708-\u270d\u270f\u2712\u2714\u2716\u271d\u2721\u2728\u2733-\u2734\u2744\u2747\u274c\u274e\u2753-\u2755\u2757\u2763-\u2764\u2795-\u2797\u27a1\u27b0\u27bf\u2934-\u2935\u2b05-\u2b07\u2b1b-\u2b1c\u2b50\u2b55\u3030\u303d\u3297\u3299\ud83c\udc04\ud83c\udccf\ud83c\udd70-\ud83c\udd71\ud83c\udd7e-\ud83c\udd7f\ud83c\udd8e\ud83c\udd91-\ud83c\udd9a\ud83c\ude01-\ud83c\ude02\ud83c\ude1a\ud83c\ude2f\ud83c\ude32-\ud83c\ude3a\ud83c\ude50-\ud83c\ude51\u200d\ud83c\udf00-\ud83d\uddff\ud83d\ude00-\ud83d\ude4f\ud83d\ude80-\ud83d\udeff\ud83e\udd00-\ud83e\uddff\udb40\udc20-\udb40\udc7f]|\u200d[\u2640\u2642]|[\ud83c\udde6-\ud83c\uddff]{2}|.[\u20e0\u20e3\ufe0f]+)+[\\s\n\r]*)+$"
)

val RAW_ID = -1998L

fun main() {

    readReactions()

    val bot = bot {

        //pasha
        token = "2008340143:AAEkPpQGmDPYetn2m_WruS1crs9DMvhOp6I"
        //testBot
//        token = "2011547394:AAG7wbS_hpqLe5Mo_ma652EbS7pviDlqeuY"

        dispatch {
            message(Filter.Reply or Filter.Forward) {
                println(this.message)
                val replyToMessage = this.message.replyToMessage
                val emoji = this.message.text.toString()
                val containsEmoji = emojiPattern.matcher(emoji).find()
                val oneEmoji = emoji.length == 2

                if (containsEmoji && oneEmoji) {
                    if (this.message.replyToMessage?.replyMarkup?.inlineKeyboard.isNullOrEmpty()) {
                        val handledMessage = handle(
                            emoji = emoji,
                            messageId = RAW_ID,
                            userId = this.message.from?.id ?: 0
                        )

                        val inlineKeyboardMarkup = handledMessage.toInlineButtons()

                        bot.deleteMessage(
                            chatId = ChatId.fromId(this.message.chat.id),
                            messageId = this.message.messageId
                        )

                        val (response, _) = bot.sendMessage(
                            chatId = ChatId.fromId(this.message.chat.id),
                            replyMarkup = inlineKeyboardMarkup,
                            replyToMessageId = replyToMessage?.messageId ?: 0,
                            text = "⤷",
                        )

                        val newId = response?.body()?.result?.messageId ?: 0
                        changeMessageId(RAW_ID, newId)
                    } else {
                        val handledMessage = handle(
                            emoji = emoji,
                            messageId = replyToMessage?.messageId ?: 0,
                            userId = this.message.from?.id ?: 0
                        )

                        val inlineKeyboardMarkup = handledMessage.toInlineButtons()

                        bot.deleteMessage(
                            chatId = ChatId.fromId(this.message.chat.id),
                            messageId = this.message.messageId
                        )

                        val (response, _) = bot.editMessageText(
                            chatId = ChatId.fromId(this.message.chat.id),
                            messageId = this.message.replyToMessage?.messageId,
                            text = "⤷",
                            replyMarkup = inlineKeyboardMarkup
                        )
                        val responseMessage = response?.body()?.result
                        checkEdited(bot, responseMessage)
                    }
                }
            }

            callbackQuery {
                val messageId = this.callbackQuery.message?.messageId ?: 0
                val emoji = this.callbackQuery.data
                val userId = this.callbackQuery.from.id

                val handledMessage = handle(
                    emoji = emoji,
                    messageId = messageId,
                    userId = userId
                )

                val inlineKeyboardMarkup = handledMessage.toInlineButtons()

                val (response, _) = bot.editMessageText(
                    chatId = ChatId.fromId(this.callbackQuery.message!!.chat.id),
                    messageId = this.callbackQuery.message!!.messageId,
                    text = "⤷",
                    replyMarkup = inlineKeyboardMarkup
                )
                val responseMessage = response?.body()?.result
                checkEdited(bot, responseMessage)
            }

            text {
                if (this.message.chat.id != spbChatId) {
                    val text = this.message.text?.toLowerCase() ?: ""
                    val messageId = this.message.chat.id
                    val user = this.message.from

                    if (text.containsPasha()) {
                        sendRandomMessage(bot, messageId)
                    } else {
                        val (checkResult, time) = checkIfUserLoudRecently(this.message.from, text)
                        if (checkResult) {
                            saveLoudUser(user)
                            sendLoud(bot, messageId, text)
                        }
                    }
                }
            }
        }
    }
    bot.startPolling()
}

private fun checkEdited(bot: Bot, message: Message?) {
    if (message?.replyMarkup?.inlineKeyboard.isNullOrEmpty()) {
        bot.deleteMessage(
            chatId = ChatId.fromId(message!!.chat.id),
            messageId = message.messageId
        )
    }
}

fun Data.ReactedMessage.toInlineButtons(): InlineKeyboardMarkup {
    val buttons = this.reactions.map { reaction ->
        val counter = reaction.userIds.count()
        return@map if (counter == 1) {
            InlineKeyboardButton.CallbackData(
                text = reaction.emoji,
                callbackData = reaction.emoji
            )
        } else {
            InlineKeyboardButton.CallbackData(
                text = "${reaction.emoji} $counter",
                callbackData = reaction.emoji
            )
        }
    }
    return InlineKeyboardMarkup.create(buttons)
}

fun getClearedReacts(buttons: List<InlineKeyboardButton>): List<InlineKeyboardButton> {
    return buttons.filter { button ->
        val counter: Int = try {
            button.text.substringAfter(' ').toInt()
        } catch (e: NumberFormatException) {
            1
        }
        return@filter counter > 0
    }
}

fun String.containsPasha(): Boolean {
    val firstWord = this
        .toLowerCase()
        .takeWhile { ch -> ch != ' ' || ch == '.' || ch == ',' || ch == '!' || ch == '?' }
    return firstWord in listOfPashaNames
}

fun giveBucksToUser(
    user: User?,
    bot: Bot,
    messageId: Long,
) {
    if (user != null) {
        val usersBucks = listOfUsersBucks.get(user) ?: 0
        val giveBucks = Random.nextInt(1, 300)
        val newBucks = usersBucks + giveBucks
        listOfUsersBucks.put(user, newBucks)
        bot.sendMessage(
            chatId = ChatId.fromId(messageId),
            text = "Йопта, держи $giveBucks$. Теперь у @${user.username} $newBucks$"
        )
    }
}

fun startLoudSession(
    bot: Bot,
    messageId: Long,
) {
    if (!isPashaInitilized) {
        bot.startPolling()
        isPashaInitilized = true
        while (true) {
            if ((System.currentTimeMillis() - pavelLoudMessageSendTime) > LOUD_PERIOD_TIME) {
                val loudSound = "LoudSound"
                val lifeLoud = "LifeLoud"
                if (Random.nextBoolean()) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(messageId),
                        text = "Э нах"
                    )
                    bot.sendMessage(
                        chatId = ChatId.fromId(messageId),
                        text = loudSound
                    )
                    expectedLoud = lifeLoud.toLowerCase()
                } else {
                    bot.sendMessage(
                        chatId = ChatId.fromId(messageId),
                        text = lifeLoud
                    )
                    expectedLoud = loudSound.toLowerCase()
                }
                expectLoud = true
                pavelLoudMessageSendTime = System.currentTimeMillis()
            }
        }
    }
}

fun sendRandomMessage(
    bot: Bot,
    messageId: Long,
) {
    val randomId = Random.nextInt(0, helloMessages.size - 1)
    bot.sendMessage(
        chatId = ChatId.fromId(messageId),
        text = helloMessages[randomId]
    )
}

fun sendLoud(
    bot: Bot,
    messageId: Long,
    loudMessage: String
) {
    when (loudMessage) {
        "LifeLoud".toLowerCase() -> bot.sendMessage(
            chatId = ChatId.fromId(messageId),
            text = "LoudSound"
        )
        "LoudSound".toLowerCase() -> bot.sendMessage(
            chatId = ChatId.fromId(messageId),
            text = "LifeLoud"
        )
    }
}

fun sendWaitMessage(
    bot: Bot,
    messageId: Long,
    waitSeconds: Int
) {
    bot.sendMessage(
        chatId = ChatId.fromId(messageId),
        text = "Fuck! Wait $waitSeconds second!"
    )
}

fun saveLoudUser(user: User?) {
    user?.let { listLoudOfUsers.put(user, System.currentTimeMillis()) }
}

fun checkIfUserLoudRecently(
    user: User?,
    message: String
): Pair<Boolean, Int?> {
    return if (user != null && message.toLowerCase() in loudMessages) {
        val loudTime = listLoudOfUsers.get(user)
        return if (loudTime != null) {
            val passedTime = System.currentTimeMillis() - loudTime
            val remainingTime = ((LOUD_TIME - passedTime.absoluteValue) / 1000).toInt()
            if (passedTime < LOUD_TIME) {
                false to remainingTime
            } else {
                true to remainingTime
            }
        } else {
            true to null
        }
    } else {
        false to null
    }
}
