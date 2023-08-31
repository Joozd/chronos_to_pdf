import data.Config
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConfigTest {
    @Test
    fun testConfig(){
        val testName1 = "test"
        val testValue1 = "12345"

        val testName2 = "test2"
        val testValue2 = ""

        val testName3 = "aub"
        val testValue3 = " hah! "

        val testConfig = Config.getMockInstance(
            ("$testName1=\"$testValue1\"" + "\n" +
             "$testName2=\"$testValue2\"" + "\n" +
             "$testName3=\"$testValue3\""
            ).lines()
        )
        assertEquals(testValue1, testConfig[testName1])
        assertEquals(testValue2, testConfig[testName2])
        assertEquals(testValue3, testConfig[testName3])
    }
}