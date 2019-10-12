package me.efraimgentil.seeker.controller

import me.efraimgentil.seeker.service.PollingCongressmanService
import me.efraimgentil.seeker.service.PollingExpenseService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/test"])
class TestController(var pollingDeputadoService: PollingCongressmanService, val pollingDespesaService: PollingExpenseService) {

    @GetMapping(value = ["/checkForNewDeputados"])
    fun checkForNewDeputados(){
        pollingDeputadoService.checkNewStateForCongressmans()
    }

    @GetMapping(value = ["/pullAndPublishAll"])
    fun pullAndPublishAll(){
        pollingDeputadoService.pullCongressmanAndPublish()
    }

    @GetMapping(value = ["/pullAndPublish/{deputadoId}"])
    fun checkForNewDeputados(@PathVariable("deputadoId") deputadoId : Long){
        pollingDeputadoService.pullAndPublishCongressman(deputadoId)
    }

    @GetMapping(value = ["/testStream"])
    fun testStream(){
        pollingDespesaService.importFile()
    }

    @GetMapping(value = ["/testFile"])
    fun testFile(){
        pollingDespesaService.test2()
    }
}