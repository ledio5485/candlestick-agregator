package dao

import Quote
import org.apache.commons.collections4.MultiValuedMap
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap
import java.time.Instant

interface QuoteDAO {
    fun add(quote: Quote): Quote
    fun findByIsinAndTimestampBetween(isin: String, startTime: Instant, endTime: Instant): Collection<Quote>
    fun deleteAllByIsin(isin: String): Collection<Quote>
}

fun QuoteDAO(): QuoteDAO = InMemoryQuoteDAO()

private class InMemoryQuoteDAO : QuoteDAO {
    private val quotesByIsin: MultiValuedMap<String, Quote> = ArrayListValuedHashMap()

    override fun add(quote: Quote): Quote {
        quotesByIsin.put(quote.isin, quote)
        return quote
    }

    override fun findByIsinAndTimestampBetween(isin: String, startTime: Instant, endTime: Instant) =
        quotesByIsin.get(isin)
            .filter { it.timestamp in startTime..endTime }

    override fun deleteAllByIsin(isin: String): Collection<Quote> = quotesByIsin.remove(isin)
}
