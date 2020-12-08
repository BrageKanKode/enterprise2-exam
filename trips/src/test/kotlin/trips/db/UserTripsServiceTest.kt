package trips.db

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("FakeData,test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class UserTripsServiceTest{

    @Autowired
    private lateinit var service: UserTripsService

    @Autowired
    private lateinit var repository: UserTripsRepository

    @Test
    fun testInit(){
        assertTrue(repository.count() > 10)
    }

    @Test
    fun testCreateUser(){
        val n = repository.count()
        service.registerNewUser("Bar001")
        assertEquals(n+1, repository.count())
    }

    @Test
    fun testPage(){

        val n = 5
        val page = service.getNextPage(n)
        assertEquals(n, page.size)

        for(i in 0 until n-1){
            assertTrue(page[i].score >= page[i+1].score)
        }
    }

}
