package com.github.kotlintelegrambot.echo

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.User
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


fun main() {

    val bot = bot {

        token = "2008340143:AAEkPpQGmDPYetn2m_WruS1crs9DMvhOp6I"

        dispatch {
            text {
                if (this.message.chat.id != spbChatId) {
                    val text = this.message.text?.toLowerCase() ?: ""
                    val messageId = this.message.chat.id
                    val user = this.message.from

                    if (expectLoud) {
                        if (text == expectedLoud) {
                            giveBucksToUser(user, bot, messageId)
                            expectedLoud = ""
                            expectLoud = false
                        }
                    } else if (text.containsPasha()) {
                        sendRandomMessage(bot, messageId)
                    } else {
                        val (checkResult, time) = checkIfUserLoudRecently(this.message.from, text)
                        if (checkResult) {
                            saveLoudUser(user)
                            sendLoud(bot, messageId, text)
                        } else {
                            if (time != null) {
//                                sendWaitMessage(bot, messageId, time)
                            }
                        }
                        startLoudSession(bot, messageId)
                    }
                }
            }
        }
    }
    bot.startPolling()
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
