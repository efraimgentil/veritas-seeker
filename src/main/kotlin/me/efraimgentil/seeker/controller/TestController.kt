package me.efraimgentil.seeker.controller

import me.efraimgentil.seeker.service.PollingCongressmanService
import me.efraimgentil.seeker.service.PollingExpenseService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/test"])
class TestController(var pollingCongressmanService: PollingCongressmanService, var pollingExpenseService: PollingExpenseService) {

    @GetMapping(value = ["/checkForNewCongressman"])
    fun checkForNewCongressman(){
        pollingCongressmanService.checkNewStateForCongressmans()
    }

    @GetMapping(value = ["/pullAndPublishAll"])
    fun pullAndPublishAll(){
        pollingCongressmanService.pullCongressmanAndPublish()
    }

    @GetMapping(value = ["/pullAndPublish/{congressmanId}"])
    fun checkForNewCongressman(@PathVariable("congressmanId") congressmanId : Long){
        pollingCongressmanService.pullAndPublishCongressman(congressmanId)
    }

    @GetMapping(value = ["/pullAndPublishExpense/{year}"])
    fun pullAndPublishExpense(@PathVariable("year") year : Int){
        pollingExpenseService.pullYear(year)
    }
}