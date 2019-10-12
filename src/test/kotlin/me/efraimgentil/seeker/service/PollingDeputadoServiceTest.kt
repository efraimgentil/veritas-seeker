package me.efraimgentil.seeker.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.efraimgentil.athena.domain.DeputadoDTO
import me.efraimgentil.seeker.client.DadosAbertosClient
import me.efraimgentil.seeker.client.dto.GetDeputadoDTO
import me.efraimgentil.seeker.client.dto.SimplifiedDeputadoDTO
import me.efraimgentil.seeker.client.dto.GetDeputadosDTO
import me.efraimgentil.seeker.client.dto.LinkDTO
import me.efraimgentil.seeker.domain.PollingDeputado
import me.efraimgentil.seeker.repository.PollingDeputadoRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.time.LocalDate
import java.util.*

class PollingDeputadoServiceTest  {

    lateinit var pollingDeputadoService: PollingDeputadoService
    lateinit var dadosAbertosClient: DadosAbertosClient
    lateinit var pollingDeputadoRepository: PollingDeputadoRepository
    lateinit var rabbitTemplate : RabbitTemplate

    @Before
    fun init(){
        dadosAbertosClient = mockk()
        pollingDeputadoRepository = mockk<PollingDeputadoRepository>()
        rabbitTemplate = mockk()
        pollingDeputadoService = PollingDeputadoService(dadosAbertosClient, pollingDeputadoRepository, rabbitTemplate)
    }

    @Test
    fun `should save a new pollingDeputado if one does not exists` (){
        every { dadosAbertosClient.getDeputados() } returns GetDeputadosDTO(dados = listOf(SimplifiedDeputadoDTO(1 , "fake@fake.com", 1 , "fake deputy")),
                links = listOf(
                        LinkDTO(rel = "self", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=1&itens=1000"),
                        LinkDTO(rel = "last", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=1&itens=1000")
                ))
        every { pollingDeputadoRepository.findByDeputadoId(1) } returns Optional.empty()
        every { pollingDeputadoRepository.save(ofType(PollingDeputado::class)) } returns PollingDeputado(id = 1, deputadoId = 1 , lastPull = LocalDate.now())

        pollingDeputadoService.checkNewForDeputados()

        verify(exactly = 1) { pollingDeputadoRepository.save(PollingDeputado(id = 0, deputadoId = 1 , lastPull = LocalDate.now())) }
    }

    @Test
    fun `should save a all pollingDeputado ` (){
        every { dadosAbertosClient.getDeputados() } returns GetDeputadosDTO(dados = listOf(
                SimplifiedDeputadoDTO(1 , "fake@fake.com", 1 , "fake deputy"),
                        SimplifiedDeputadoDTO(2 , "fake2@fake2.com", 12 , "fake deputy 2")),
                links = listOf(
                        LinkDTO(rel = "self", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=1&itens=1000"),
                        LinkDTO(rel = "last", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=1&itens=1000")
                ))
        every { pollingDeputadoRepository.findByDeputadoId(1) } returns Optional.empty()
        every { pollingDeputadoRepository.findByDeputadoId(2) } returns Optional.empty()
        every { pollingDeputadoRepository.save(ofType(PollingDeputado::class)) } returns PollingDeputado(id = 1, deputadoId = 1 , lastPull = LocalDate.now())
        every { pollingDeputadoRepository.save(ofType(PollingDeputado::class)) } returns PollingDeputado(id = 2, deputadoId = 2 , lastPull = LocalDate.now())

        pollingDeputadoService.checkNewForDeputados()

        verify { pollingDeputadoRepository.save(PollingDeputado(id = 0, deputadoId = 1 , lastPull = LocalDate.now())) }
        verify { pollingDeputadoRepository.save(PollingDeputado(id = 0, deputadoId = 2 , lastPull = LocalDate.now())) }
    }

    @Test fun `should save only the pollingDeputado that does not exists` () {
        every { dadosAbertosClient.getDeputados() } returns GetDeputadosDTO(dados = listOf(
                SimplifiedDeputadoDTO(1 , "fake@fake.com", 1 , "fake deputy"),
                SimplifiedDeputadoDTO(2 , "fake2@fake2.com", 12 , "fake deputy 2")),
                links = listOf(
                        LinkDTO(rel = "self", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=1&itens=1000"),
                        LinkDTO(rel = "last", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=1&itens=1000")
                ))
        every { pollingDeputadoRepository.findByDeputadoId(1) } returns Optional.of(PollingDeputado(id = 1, deputadoId = 1 , lastPull = LocalDate.now()))
        every { pollingDeputadoRepository.findByDeputadoId(2) } returns Optional.empty()
        every { pollingDeputadoRepository.save(ofType(PollingDeputado::class)) } returns PollingDeputado(id = 2, deputadoId = 2 , lastPull = LocalDate.now())

        pollingDeputadoService.checkNewForDeputados()

        verify (exactly = 0){ pollingDeputadoRepository.save(PollingDeputado(id = 0, deputadoId = 1 , lastPull = LocalDate.now())) }
        verify { pollingDeputadoRepository.save(PollingDeputado(id = 0, deputadoId = 2 , lastPull = LocalDate.now())) }
    }

    @Test fun `should keep pooling until last page equals self page` () {
        every { dadosAbertosClient.getDeputados(page = 1) } returns GetDeputadosDTO(dados = listOf(
                SimplifiedDeputadoDTO(1 , "fake@fake.com", 1 , "fake deputy")),
                links = listOf(
                        LinkDTO(rel = "self", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=1&itens=1000"),
                        LinkDTO(rel = "last", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=2&itens=1000")
                ))
        every { dadosAbertosClient.getDeputados(page = 2) } returns GetDeputadosDTO(dados = listOf(
                SimplifiedDeputadoDTO(2 , "fake2@fake2.com", 12 , "fake deputy 2")),
                links = listOf(
                        LinkDTO(rel = "self", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=2&itens=1000"),
                        LinkDTO(rel = "last", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=2&itens=1000")
                ))
        every { pollingDeputadoRepository.findByDeputadoId(1) } returns Optional.empty()
        every { pollingDeputadoRepository.findByDeputadoId(2) } returns Optional.empty()
        every { pollingDeputadoRepository.save(ofType(PollingDeputado::class)) } returns PollingDeputado(id = 1, deputadoId = 1 , lastPull = LocalDate.now())
        every { pollingDeputadoRepository.save(ofType(PollingDeputado::class)) } returns PollingDeputado(id = 2, deputadoId = 2 , lastPull = LocalDate.now())

        pollingDeputadoService.checkNewForDeputados()

        verify { dadosAbertosClient.getDeputados(page = 1, limit = 1000, order = "ASC" , orderBy = "nome") }
        verify { dadosAbertosClient.getDeputados(page = 2, limit = 1000, order = "ASC" , orderBy = "nome") }
        verify { pollingDeputadoRepository.save(PollingDeputado(id = 0, deputadoId = 1 , lastPull = LocalDate.now())) }
        verify { pollingDeputadoRepository.save(PollingDeputado(id = 0, deputadoId = 2 , lastPull = LocalDate.now())) }
    }

    @Test fun `should return the page whitin the href in the link`() {
        var link = LinkDTO(rel = "self", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=1&itens=1000")
        assertThat(pollingDeputadoService.getPageFromLink(link)).isEqualTo(1)

        link = LinkDTO(rel = "self", href = "https://dadosabertos.camara.leg.br/api/v2/deputados?ordem=ASC&ordenarPor=nome&pagina=20&itens=1000")
        assertThat(pollingDeputadoService.getPageFromLink(link)).isEqualTo(20)
    }

    @Test fun `should pull and publish deputado detail`(){
        var deputadoDTO = DeputadoDTO(
                id = 1,
                cpf = "00099900099",
                dataFalecimento = null,
                dataNascimento = LocalDate.of(1988, 2, 1),
                escolaridade = "Superior",
                municipioNascimento = "Fortaleza",
                ufNascimento = "CE",
                nomeCivil = "Efras",
                sexo = "Masculino",
                redeSocial = null,
                ultimoStatus = null,
                uri = null,
                urlWebsite = null)
        every { pollingDeputadoRepository.findAllWithLastPullBefore(LocalDate.now().minusDays(1)) } returns setOf(PollingDeputado(id = 1, deputadoId = 1 , lastPull = LocalDate.now().minusDays(1)))
        every { dadosAbertosClient.getDeputado(1) } returns GetDeputadoDTO( dados= deputadoDTO , links = listOf())
        every { rabbitTemplate.convertAndSend("deputadoStore","", eq(deputadoDTO)) } returns mockk()
        every { pollingDeputadoRepository.save(ofType(PollingDeputado::class)) } returns PollingDeputado(id = 1, deputadoId = 1 , lastPull = LocalDate.now())

        pollingDeputadoService.pullDeputadosAndPublish()

        verify { rabbitTemplate.convertAndSend("deputadoStore","", deputadoDTO) }
        verify { pollingDeputadoRepository.save(PollingDeputado(id = 1, deputadoId = 1 , lastPull = LocalDate.now())) }
    }
}