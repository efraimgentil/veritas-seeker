package me.efraimgentil.seeker.client

import me.efraimgentil.seeker.AbstractIT
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class DadosAbertosClientIT : AbstractIT() {

    @Autowired
    lateinit var dadosAbertosClient : DadosAbertosClient

    @Ignore
    @Test
    fun shouldCallGetCongressman(){
        println(dadosAbertosClient.getDeputados())
    }


}