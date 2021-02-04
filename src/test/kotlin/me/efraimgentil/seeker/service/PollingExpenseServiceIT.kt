package me.efraimgentil.seeker.service

import com.github.tomakehurst.wiremock.client.WireMock
import me.efraimgentil.seeker.AbstractIT
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.core.io.ClassPathResource
import org.springframework.util.ResourceUtils
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class PollingExpenseServiceIT : AbstractIT() {

    @Autowired lateinit var pollingExpenseService: PollingExpenseService

    @Test
    fun test(){
        val file = ResourceUtils.getFile("json/expenses-example.json")
        val byteArrayOutputStream = ByteArrayOutputStream()
        val zipOutputStream = ZipOutputStream(byteArrayOutputStream)
        zipOutputStream.putNextEntry(ZipEntry("Ano-2018.json"))
        zipOutputStream.write(Files.readAllBytes(file.absolutePath()))
        zipOutputStream.closeEntry()
        zipOutputStream.close()
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/cotas/Ano-2018.json.zip"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type" , "application/zip")
                        .withBody(byteArrayOutputStream.toByteArray()))
        )

        pollingExpenseService.pullYear(2018)



//        pollingExpenseService.downloadJsonZip(2018)
    }

}