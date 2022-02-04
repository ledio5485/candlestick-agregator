package dao

import Instrument
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class InstrumentDAOTest {
    private val dao = InstrumentDAO()

    @Test
    internal fun `should return nothing when instrument is not found`() {
        assertNull(dao.get("non existent"))
    }

    @Test
    internal fun `should get instrument by isin`() {
        val expected = dao.add(Instrument("AL1234567890", "lorem ipsum"))

        val actual = dao.get("AL1234567890")

        assertEquals(expected, actual)
    }

    @Test
    internal fun `should add a new instrument`() {
        val isin = "AL1234567890"
        assertNull(dao.get(isin))
        val expected = Instrument(isin, "lorem ipsum")

        val actual = dao.add(expected)

        assertEquals(expected, actual)
    }

    @Test
    internal fun `should override an instrument with the same isin`() {
        val isin = "AL1234567890"
        val instrumentFormer = dao.add(Instrument(isin, "lorem ipsum"))

        var actual = dao.get(isin)

        assertEquals(instrumentFormer, actual)

        val instrumentLater = dao.add(Instrument(isin, "lorem ipsum2"))

        actual = dao.get(isin)

        assertNotEquals(instrumentFormer, actual)
        assertEquals(instrumentLater, actual)
    }

    @Test
    internal fun `should do nothing when trying to delete an instrument by non existent isin`() {
        val isin = "non existent"

        assertNull(dao.get(isin))
        assertNull(dao.delete(isin))
    }

    @Test
    internal fun `should delete an instrument by isin`() {
        val isin = "AL1234567890"
        val expected = dao.add(Instrument(isin, "lorem ipsum"))

        val actual = dao.delete(isin)

        assertEquals(expected, actual)
        assertNull(dao.get(isin))
    }
}