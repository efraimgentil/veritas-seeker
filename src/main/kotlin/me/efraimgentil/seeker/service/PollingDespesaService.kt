package me.efraimgentil.seeker.service

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.efraimgentil.seeker.client.dto.DespesaDTO
import me.efraimgentil.seeker.domain.Despesa
import me.efraimgentil.seeker.repository.DespesaRepository
import org.apache.commons.beanutils.BeanUtils
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.io.File
import java.lang.RuntimeException

@Service
class PollingDespesaService(val despesaRepository: DespesaRepository,
                            val rabbitTemplate: RabbitTemplate) {

    fun test2() {
        var objectMapper = ObjectMapper()

        var file = File("/home/efra/Enviroment/Workspaces/veritas/Ano-2019.json")
        var mapa : JsonNode = jacksonObjectMapper().readValue<JsonNode>(file)

        mapa.get("dados").asIterable().forEach {
            println(it)
        }
    }

    //TODO add download feature, to retrieve the file in the Dados Abertos unzip and read the stream
    fun importFile() {
        var parser = JsonFactory().createParser(File("/home/efra/Enviroment/Workspaces/veritas/Ano-2019.json"))
        parser.nextToken() // JsonToken.START_OBJECT;
        startReadingFile(parser)
        parser.close()
    }

    private fun startReadingFile(parser: JsonParser) {
        print("Starting")
        while(parser.nextToken() != JsonToken.END_ARRAY){
            var objectStart = parser.currentToken() // START OBJECT
            var lastFieldName : String = "null"
            val despesaDTO = DespesaDTO()
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                if( parser.currentToken == JsonToken.FIELD_NAME){
                    lastFieldName = parser.getText()
                }
                if(isValue(parser.currentToken)){
                    when(parser.currentToken){
                        JsonToken.VALUE_STRING -> BeanUtils.setProperty(despesaDTO, lastFieldName , parser.getText())
                        JsonToken.VALUE_FALSE, JsonToken.VALUE_TRUE -> BeanUtils.setProperty(despesaDTO, lastFieldName , parser.getValueAsBoolean())
                        JsonToken.VALUE_NUMBER_FLOAT ->  BeanUtils.setProperty(despesaDTO, lastFieldName , parser.getValueAsDouble())
                        JsonToken.VALUE_NUMBER_INT ->  BeanUtils.setProperty(despesaDTO, lastFieldName , parser.getValueAsInt())
                        else -> throw RuntimeException("Not supported")
                    }
                }
                publishIfNeeded(despesaDTO)
            }
        }
        print("Over")
    }

    private fun publishIfNeeded(despesaDTO: DespesaDTO) {
        if(!despesaRepository.findById(despesaDTO.idDocumento!!).isPresent){
            var despesa = Despesa(documentId = despesaDTO.idDocumento!!, ano = despesaDTO.ano!!, mes = despesaDTO.mes!!)
            despesaRepository.save(despesa)
            rabbitTemplate.convertAndSend("despesa", "", despesaDTO)
        }
    }

    private fun isValue(jsonToken : JsonToken) : Boolean {
        return hashSetOf(JsonToken.VALUE_STRING
                , JsonToken.VALUE_FALSE
                , JsonToken.VALUE_TRUE
                , JsonToken.VALUE_NUMBER_FLOAT
                , JsonToken.VALUE_NUMBER_INT).contains(jsonToken)
    }

}