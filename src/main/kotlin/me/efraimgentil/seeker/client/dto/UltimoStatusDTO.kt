package me.efraimgentil.athena.domain

data class UltimoStatusDTO(
        var `data`: String,
        var condicaoEleitoral: String,
        var descricaoStatus: String?,
        var gabinete: GabineteDTO,
        var id: Int,
        var idLegislatura: Int,
        var nome: String,
        var nomeEleitoral: String,
        var siglaPartido: String,
        var siglaUf: String,
        var situacao: String,
        var uri: String,
        var uriPartido: String,
        var urlFoto: String
)