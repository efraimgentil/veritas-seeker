package me.efraimgentil.seeker.controller

import me.efraimgentil.seeker.service.PollingDeputadoService
import me.efraimgentil.seeker.service.PollingDespesaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = "/test")
class TestController(var pollingDeputadoService: PollingDeputadoService, val pollingDespesaService: PollingDespesaService) {

    @GetMapping(value = "/checkForNewDeputados")
    fun checkForNewDeputados(){
        pollingDeputadoService.checkNewForDeputados()
    }

    @GetMapping(value = "/pullAndPublishAll")
    fun pullAndPublishAll(){
        pollingDeputadoService.pullDeputadosAndPublish()
    }

    @GetMapping(value = "/pullAndPublish/{deputadoId}")
    fun checkForNewDeputados(@PathVariable("deputadoId") deputadoId : Long){
        pollingDeputadoService.pullAndPublishDeputado(deputadoId)
    }

    @GetMapping(value = "/testStream")
    fun testStream(){
        pollingDespesaService.importFile()
    }

    @GetMapping(value = "/testFile")
    fun testFile(){
        pollingDespesaService.test2()
    }
}