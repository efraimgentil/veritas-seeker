package me.efraimgentil.seeker.client.dto

data class GetDeputadosDTO(
        var dados: List<SimplifiedDeputadoDTO>,
        var links: List<LinkDTO>
)