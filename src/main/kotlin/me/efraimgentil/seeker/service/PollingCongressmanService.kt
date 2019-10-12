package me.efraimgentil.seeker.service

import io.micrometer.core.annotation.Timed
import me.efraimgentil.seeker.client.DadosAbertosClient
import me.efraimgentil.seeker.client.dto.LinkDTO
import me.efraimgentil.seeker.config.RabbitMQConstants.CONGRESSMAN_TOPIC
import me.efraimgentil.seeker.config.RabbitMQConstants.NO_ROUTING
import me.efraimgentil.seeker.domain.PollingCongressman
import me.efraimgentil.seeker.repository.PollingCongressmanRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.util.regex.Pattern

@Service
class PollingCongressmanService (var dadosAbertosClient: DadosAbertosClient,
                                 var pollingDeputadoRepository: PollingCongressmanRepository,
                                 var rabbitTemplate : RabbitTemplate) {

    @Timed
    fun checkNewStateForCongressmans(){
        var maxPages = 999
        for(page in 1 until maxPages) {
            var deputados = dadosAbertosClient.getDeputados(page = page)
            deputados.dados.forEach {
                if (!pollingDeputadoRepository.findByCongressmanId(it.id).isPresent) {
                    pollingDeputadoRepository.save(PollingCongressman(0, it.id, LocalDate.now()))
                }
            }
            var pageFromLink = getPageFromLink(deputados.links.filter { link -> link.rel == "last" }[0])
            if(page == pageFromLink) break;
        }
    }

    @Timed
    fun pullCongressmanAndPublish() {
        var congressmanDetails = pollingDeputadoRepository
                .findAllWithLastPullBefore(LocalDate.now().minusDays(1))
        pullAndPublish(congressmanDetails)
    }

    @Timed
    fun pullAndPublishCongressman(congressmanId : Long) {
        pullAndPublish(setOf(pollingDeputadoRepository.findByCongressmanId(congressmanId)
                .orElseThrow { RuntimeException("PollingCongressman ${congressmanId} not found ") }))
    }

    fun pullAndPublish(deputadosDetails : Set<PollingCongressman>){
        deputadosDetails.stream().forEach {
            it.deputadoId?.toInt()?.let { it1 ->
                rabbitTemplate.convertAndSend(CONGRESSMAN_TOPIC, NO_ROUTING , dadosAbertosClient.getDeputado(it1).dados)
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