package me.efraimgentil.seeker.client

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = ["spring.profiles.active=test"])
class DadosAbertosClientIT {

    @Autowired
    lateinit var dadosAbertosClient : DadosAbertosClient

    @Ignore
    @Test
    fun shouldCallGetCongressman(){
        println(dadosAbertosClient.getDeputados())
    }


}