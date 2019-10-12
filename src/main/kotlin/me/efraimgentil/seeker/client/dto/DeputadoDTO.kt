package me.efraimgentil.athena.domain

import java.time.LocalDate

data class DeputadoDTO(
        var id: Int,
        var cpf: String,
        var dataFalecimento: LocalDate?,
        var dataNascimento: LocalDate?,
        var escolaridade: String?,
        var municipioNascimento: String?,
        var nomeCivil: String?,
        var redeSocial: List<String>?,
        var sexo: String?,
        var ufNascimento: String?,
        var ultimoStatus: UltimoStatusDTO?,
        var uri: String?,
        var urlWebsite: String?
)
