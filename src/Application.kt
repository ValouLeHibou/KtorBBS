package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.Message

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        /* DATE */
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = current.format(formatter)

        val userName1: String = "Valentin"
        val userName2: String = "Jacques"
        val message1: String = "Yo Jacques"
        val message2: String = "Yo Valentin"

        var messageArray : Array<Message> = arrayOf(
            Message(formatted, userName1, message1),
            Message(formatted, userName2, message2)
        )

        get("/messages/{userName?}/{message?}") {

            if (call.parameters["userName"] != null && call.parameters["message"] != null) {
                var oneMessage = Message(formatted, call.parameters["userName"]!!, call.parameters["message"]!!)
                messageArray += oneMessage
            }
            call.respondHtml {
                body {
                    h1 { +"Chat" }
                    form (action = "messages") {
                        textInput { name="userName" }
                        br {  }
                        textInput { name="message" }
                        br {  }
                        submitInput { value="send" }
                    }
                    ul {
                        for (theMessage in messageArray) {
                            p {
                                +"${theMessage.user} dit :"
                            }
                            div {
                                +"${theMessage.text}"
                            }
                            div {
                                +"Le : ${theMessage.date_message}"
                            }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }
    }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
