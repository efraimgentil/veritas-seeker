package me.efraimgentil.seeker.client.dto

import me.efraimgentil.athena.domain.DeputadoDTO

data class GetDeputadoDTO (
        var dados: DeputadoDTO,
        var links: List<LinkDTO>
)