package me.efraimgentil.seeker.client

import me.efraimgentil.seeker.client.dto.GetDeputadoDTO
import me.efraimgentil.seeker.client.dto.GetDeputadosDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(value = "dadosAbertosClient", url = "\${feign.client.config.dadosAbertosClient.url}")
interface DadosAbertosClient {

    @GetMapping(value = "/deputados" , consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getDeputados(@RequestParam(value = "pagina", required = false) page : Int? = 1,
                     @RequestParam(value = "itens", required = false) limit : Int? = 1000,
                     @RequestParam(value = "ordem", required = false) order : String? = "ASC",
                     @RequestParam(value = "ordenarPor", required = false) orderBy : String? = "nome") : GetDeputadosDTO

    @GetMapping(value = "/deputados/{deputadoId}" , consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getDeputado(@PathVariable(value = "deputadoId") deputadoId : Int) : GetDeputadoDTO

    @GetMapping(value = "/deputados/{deputadoId}" , consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getFile(@PathVariable(value = "deputadoId") deputadoId : Int) : GetDeputadoDTO
}