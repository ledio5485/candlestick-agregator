package event

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.client.WebsocketClient
import org.http4k.core.Uri
import org.http4k.websocket.Websocket

class InstrumentStream(uriString: String, objectMapper: ObjectMapper) :
    Stream<InstrumentEvent>(uriString.plus("/instruments"), InstrumentEvent::class.java, objectMapper)

class QuoteStream(uriString: String, objectMapper: ObjectMapper) :
    Stream<QuoteEvent>(uriString.plus("/quotes"), QuoteEvent::class.java, objectMapper)

open class Stream<T>(
    private val uriString: String,
    private val classType: Class<T>,
    private val objectMapper: ObjectMapper
) {
    private lateinit var websocket: Websocket

    fun connect(onEvent: (T) -> Unit) {
        websocket = WebsocketClient.nonBlocking(Uri.of(uriString)) { println("Connected to $uriString") }

        websocket.onMessage {
            val event = objectMapper.readValue(it.body.stream, classType)
            onEvent.invoke(event)
        }

        websocket.onError {
            println("An error occurred: $it")
        }
    }
}
