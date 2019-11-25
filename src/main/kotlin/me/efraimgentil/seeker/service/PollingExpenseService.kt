package me.efraimgentil.seeker.service

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import me.efraimgentil.seeker.client.dto.DespesaDTO
import me.efraimgentil.seeker.config.RabbitMQConstants.EXPENSE_TOPIC
import me.efraimgentil.seeker.config.RabbitMQConstants.NO_ROUTING
import me.efraimgentil.seeker.domain.PollingExpense
import me.efraimgentil.seeker.repository.ExpenseRepository
import org.apache.commons.beanutils.BeanUtils
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream


@Service
class PollingExpenseService(val expenseRepository: ExpenseRepository,
                            val rabbitTemplate: RabbitTemplate,
                            @Value("\${dadosAbertos.cotas.downloadUrl}") val cotasDownloadUrl: String) {

    // comparing with stream reading
    // the consumption of memory is mutch higher using this method
//    fun importFileWithoutStream() {
//        var file = File("/home/efra/Enviroment/Workspaces/veritas/Ano-2019.json")
//        var mapa : JsonNode = jacksonObjectMapper().readValue<JsonNode>(file)
//
//        mapa.get("dados").asIterable().forEach {
//            println(it)
//        }
//    }

    fun downloadJsonZip(year : Int) : String {
        // Streams the response instead of loading it all in memory
        val responseExtractor = ResponseExtractor { response ->
            // Here I write the response to a file but do what you like
            val path = Paths.get("fileToImport-${System.currentTimeMillis()}.zip")
            Files.copy(response.body, path, StandardCopyOption.REPLACE_EXISTING)
            val zipInputStream = ZipInputStream(FileInputStream(path.toFile()))
            var nextEntry = zipInputStream.nextEntry
            while (nextEntry != null) {
                Files.copy(zipInputStream, Paths.get(nextEntry.name), StandardCopyOption.REPLACE_EXISTING)
                nextEntry = zipInputStream.nextEntry
            }
            zipInputStream.closeEntry()
            zipInputStream.close()
            null //
        }
        RestTemplate().execute("${cotasDownloadUrl}/Ano-$year.json.zip", HttpMethod.GET, RequestCallback { }, responseExtractor)
        return "Ano-$year.json"
    }

    //TODO add download feature, to retrieve the file in the Dados Abertos unzip and read the stream
    fun readFileAndPublish(fileToImport: File) {
        val parser = JsonFactory().createParser(fileToImport)
        parser.nextToken() // JsonToken.START_OBJECT;
        startReadingFile(parser)
        parser.close()
    }

    private fun startReadingFile(parser: JsonParser) {
        print("Starting")
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            var objectStart = parser.currentToken() // START OBJECT
            var lastFieldName = "null"
            val despesaDTO = DespesaDTO()
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                if (parser.currentToken == JsonToken.FIELD_NAME) {
                    lastFieldName = parser.getText()
                }
                if (isValue(parser.currentToken)) {
                    when (parser.currentToken) {
                        JsonToken.VALUE_STRING -> BeanUtils.setProperty(despesaDTO, lastFieldName, parser.getText())
                        JsonToken.VALUE_FALSE, JsonToken.VALUE_TRUE -> BeanUtils.setProperty(despesaDTO, lastFieldName, parser.getValueAsBoolean())
                        JsonToken.VALUE_NUMBER_FLOAT -> BeanUtils.setProperty(despesaDTO, lastFieldName, parser.getValueAsDouble())
                        JsonToken.VALUE_NUMBER_INT -> BeanUtils.setProperty(despesaDTO, lastFieldName, parser.getValueAsInt())
                        else -> throw RuntimeException("Not supported")
                    }
                }
            }
            publishIfNeeded(despesaDTO)
        }
        print("Over")
    }

    private fun publishIfNeeded(despesaDTO: DespesaDTO) {
        println("Expense ${despesaDTO}")
        val expense = expenseRepository.findById(despesaDTO.idDocumento!!)
        println(expense)
        if (!expense.isPresent) {
            var despesa = PollingExpense(documentId = despesaDTO.idDocumento!!, year = despesaDTO.ano!!, month = despesaDTO.mes!!)
            expenseRepository.save(despesa)
            rabbitTemplate.convertAndSend(EXPENSE_TOPIC, NO_ROUTING, despesaDTO)
        }
    }

    private fun isValue(jsonToken: JsonToken): Boolean {
        return hashSetOf(JsonToken.VALUE_STRING
                , JsonToken.VALUE_FALSE
                , JsonToken.VALUE_TRUE
                , JsonToken.VALUE_NUMBER_FLOAT
                , JsonToken.VALUE_NUMBER_INT).contains(jsonToken)
    }

    fun pullYear(year: Int) {
        readFileAndPublish(Paths.get(downloadJsonZip(year)).toFile())
    }
}