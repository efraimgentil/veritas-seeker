package me.efraimgentil.seeker.service

import io.micrometer.core.annotation.Timed
import me.efraimgentil.seeker.client.DadosAbertosClient
import me.efraimgentil.seeker.client.dto.LinkDTO
import me.efraimgentil.seeker.domain.PollingDeputado
import me.efraimgentil.seeker.repository.PollingDeputadoRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.regex.Pattern

@Service
class PollingDeputadoService (var dadosAbertosClient: DadosAbertosClient,
                              var pollingDeputadoRepository: PollingDeputadoRepository,
                              var rabbitTemplate : RabbitTemplate) {

    @Timed
    fun checkNewForDeputados(){
        var maxPages = 999
        for(page in 1 until maxPages) {
            var deputados = dadosAbertosClient.getDeputados(page = page)
            deputados.dados.forEach {
                if (!pollingDeputadoRepository.findByDeputadoId(it.id).isPresent) {
                    pollingDeputadoRepository.save(PollingDeputado(0, it.id, LocalDate.now()))
                }
            }
            var pageFromLink = getPageFromLink(deputados.links.filter { link -> link.rel == "last" }[0])
            if(page == pageFromLink) break;
        }
    }

    @Timed
    fun pullDeputadosAndPublish() {
        var deputadosDetails = pollingDeputadoRepository
                .findAllWithLastPullBefore(LocalDate.now().minusDays(1))
        pullAndPublish(deputadosDetails)
    }

    @Timed
    fun pullAndPublishDeputado(deputadoId : Long) {
        pullAndPublish(setOf(pollingDeputadoRepository.findByDeputadoId(deputadoId)
                .orElseThrow { RuntimeException("PollingDeputado ${deputadoId} not found ") }))
    }

    fun pullAndPublish(deputadosDetails : Set<PollingDeputado>){
        deputadosDetails.stream().forEach {
            it.deputadoId?.toInt()?.let { it1 ->
                rabbitTemplate.convertAndSend("deputado", "" , dadosAbertosClient.getDeputado(it1).dados)
            }
            pollingDeputadoRepository.save(it.copy(lastPull = LocalDate.now()))
        }
    }

    fun getPageFromLink(link : LinkDTO) : Int? {
        var matcher = Pattern.compile("(?<=pagina=)(\\d+)").matcher(link.href)
        if(matcher.find()) {
           return matcher.group().toInt()
        }
        throw RuntimeException("Page attribute not found in the ${link}")
    }

}