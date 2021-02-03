package me.efraimgentil.seeker.service

import me.efraimgentil.seeker.AbstractIT
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class PollingExpenseServiceIT : AbstractIT() {

    @Autowired lateinit var pollingExpenseService: PollingExpenseService

    @Test
    fun test(){
//        pollingExpenseService.downloadJsonZip(2018)
    }

}