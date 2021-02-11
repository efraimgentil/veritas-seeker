package me.efraimgentil.seeker.service

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import me.efraimgentil.seeker.domain.ExpenseDocument
import me.efraimgentil.seeker.domain.Expense
import me.efraimgentil.seeker.repository.ExpenseRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.zip.ZipInputStream
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.AbstractPlatformTransactionManager

import org.springframework.transaction.support.DefaultTransactionDefinition





@Service
class PollingExpenseService(val expenseRepository: ExpenseRepository,
                            val jsonHasher: JsonHasher,
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

    fun pullYear(year: Int) {
        readFileAndPublish(Paths.get(downloadJsonZip(year)).toFile())
    }

    private fun readFileAndPublish(fileToImport: File) {
        val parser = JsonFactory().createParser(fileToImport)
        parser.nextToken() // JsonToken.START_OBJECT;
        startReadingFile(parser)
        parser.close()
    }

    private fun downloadJsonZip(year : Int) : String {
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

    private fun startReadingFile(parser: JsonParser) {
        print("Starting") // TODO DEBUG
        val arrayList = ArrayList<Expense>(500)
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            var objectStart = parser.currentToken() // START OBJECT
            var node =  JsonNodeFactory(true).objectNode()
            var fieldName = "null"
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                if (parser.currentToken == JsonToken.FIELD_NAME) {
                    fieldName = parser.getText()
                }
                if (isValue(parser.currentToken)) {
                    when (parser.currentToken) {
                        JsonToken.VALUE_STRING ->  node.put(fieldName,  parser.getText())
                        JsonToken.VALUE_FALSE, JsonToken.VALUE_TRUE ->  node.put(fieldName,  parser.getValueAsBoolean())
                        JsonToken.VALUE_NUMBER_FLOAT -> node.put(fieldName, parser.getValueAsDouble())
                        JsonToken.VALUE_NUMBER_INT -> node.put(fieldName, parser.getValueAsInt())
                        else -> throw RuntimeException("Not supported")
                    }
                }
            }

            val publishIfNeeded = publishIfNeeded(node)
            if(publishIfNeeded != null) {
                arrayList.add(publishIfNeeded)
                if(arrayList.size >= 500) {
                    expenseRepository.saveAll(arrayList)
                    arrayList.clear()
                }
            }
        }
        if(!arrayList.isEmpty()){
            expenseRepository.saveAll(arrayList)
        }
        print("Over") // TODO DEBUG
    }

    private fun publishIfNeeded(rawExpenseBody : JsonNode) : Expense? {
        println("Expense ${rawExpenseBody}") // TODO DEBUG
        val documentHash = jsonHasher.generateHashFor(rawExpenseBody)
        val expense = expenseRepository.countByHash(documentHash)
        if(expense == 0L){
            return Expense(hash = documentHash, year = rawExpenseBody.get("ano").asInt()!!, month = rawExpenseBody.get("mes").asInt()!!, document = ExpenseDocument(body = rawExpenseBody.toString()))
            // SKIP this for now
            // rabbitTemplate.convertAndSend(EXPENSE_TOPIC, NO_ROUTING, despesaDTO)
        }
        return null
    }

    private fun isValue(jsonToken: JsonToken): Boolean {
        return hashSetOf(JsonToken.VALUE_STRING
                , JsonToken.VALUE_FALSE
                , JsonToken.VALUE_TRUE
                , JsonToken.VALUE_NUMBER_FLOAT
                , JsonToken.VALUE_NUMBER_INT).contains(jsonToken)
    }
}