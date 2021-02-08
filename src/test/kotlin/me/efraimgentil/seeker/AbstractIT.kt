package me.efraimgentil.seeker

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.junit4.SpringRunner
import javax.sql.DataSource

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = ["spring.profiles.active=local,test"])
@AutoConfigureWireMock(port = 9999)
abstract class AbstractIT{

    @Autowired
    lateinit var dataSource : DataSource

    @Autowired
    lateinit var objectMapper: ObjectMapper
}