package me.efraimgentil.seeker.repository

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.efraimgentil.seeker.AbstractIT
import me.efraimgentil.seeker.client.dto.DespesaDTO
import me.efraimgentil.seeker.domain.ExpenseDocument
import me.efraimgentil.seeker.domain.PollingExpense
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import java.nio.file.Files

class PollingCongressmanRepositoryIT : AbstractIT() {
    @Autowired
    lateinit var expenseRepository: ExpenseRepository

    @Test
    fun t(){
        val readValue = jacksonObjectMapper().readValue<DespesaDTO>(ClassPathResource("/json/congressmanExpenseExample.json").file)

        val pollingExpense = PollingExpense(documentId =  readValue.idDocumento!!
                , month = readValue.mes!!
                , year = readValue.ano!!)
        pollingExpense.documents = listOf(ExpenseDocument(hash = "hash"
                , body = jacksonObjectMapper().writeValueAsString(readValue)
                , pollingExpense = pollingExpense ))

        expenseRepository.save(pollingExpense)
        println(readValue)
    }

}