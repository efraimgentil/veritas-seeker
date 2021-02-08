package me.efraimgentil.seeker.service

import com.fasterxml.jackson.databind.JsonNode
import com.github.tomakehurst.wiremock.client.WireMock
import me.efraimgentil.seeker.AbstractIT
import me.efraimgentil.seeker.repository.ExpenseRepository
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.ResourceUtils
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class PollingExpenseServiceIT : AbstractIT() {

    @Autowired
    lateinit var pollingExpenseService: PollingExpenseService
    @Autowired
    lateinit var expenseRepository: ExpenseRepository

    @Before
    fun beforeEach() {
        // clean entire polling_expanse table (and related) before any test
        dataSource.connection.prepareStatement("truncate polling_expense cascade;").execute()
    }

    @Test
    fun shouldImportSuccessfullyAllContentFromDownloadedFile() {
        val year = 2018;
        val example1 = readExampleJson("classpath:json/expense-1.json")
        val example2 = readExampleJson("classpath:json/expense-2.json")
        val example3 = readExampleJson("classpath:json/expense-3.json")
        val mockedJsonFileContent = objectMapper.nodeFactory.objectNode()
        mockedJsonFileContent.putArray("dados").addAll(listOf(example1, example2, example3))
        stubDownloadFile(year, mockedJsonFileContent)

        pollingExpenseService.pullYear(year)

        val expenses = expenseRepository.findAll()
        Assertions.assertThat(expenses).hasSize(3)
        Assertions.assertThat(expenses.map { v -> v.document?.body }).containsExactlyInAnyOrder(
                example1.toString(),
                example2.toString(),
                example3.toString()
        )
    }

    private fun readExampleJson(file: String): JsonNode = objectMapper.readTree(ResourceUtils.getFile(file))

    private fun stubDownloadFile(year: Int, jsonNode: JsonNode) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val zipOutputStream = ZipOutputStream(byteArrayOutputStream)
        zipOutputStream.putNextEntry(ZipEntry("Ano-${year}.json"))
        zipOutputStream.write(objectMapper.writeValueAsBytes(jsonNode))
        zipOutputStream.closeEntry()
        zipOutputStream.close()
        // Fake zip response that will contain the file content to be imported from "/cotas" endpoint
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/cotas/Ano-${year}.json.zip"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/zip")
                        .withBody(byteArrayOutputStream.toByteArray()))
        )
    }

}