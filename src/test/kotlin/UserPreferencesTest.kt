import data.PreferencesData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UserPreferencesTest {
    @Test
    fun testUserPreferencesEquals(){
        val preferences1 = PreferencesData.DEFAULT
        val preferences2 = PreferencesData(
            logLanding = false,
            guessSimType = false,
            removeSimTypes = true,
            function = PreferencesData.CAPTAIN
        )
        assertEquals(preferences1, preferences1)
        assertEquals(preferences1, PreferencesData.DEFAULT.copy())
        assert(!preferences1.equals(3))
        assertNotEquals(preferences1, preferences2)
    }
}