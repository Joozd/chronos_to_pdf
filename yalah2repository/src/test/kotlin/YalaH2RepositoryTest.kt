import db.TestDB
import db.dropAllTables
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class YalaH2RepositoryTest {
    private lateinit var _repo: YalaH2Repository
    private val repo get() = _repo
    @BeforeEach
    fun setup(){
        _repo = YalaH2Repository(TestDB)
    }

    @AfterEach
    fun cleanUp(){
        dropAllTables()
    }

    @Test
    fun `test if repository can be initialized with specified DB`(){
        println("repo: $repo")
    }

}