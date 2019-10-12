package me.efraimgentil.seeker.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct
import javax.sql.DataSource

@Configuration
class MigrationConfig {

//    @Autowired
//    lateinit var datasource : DataSource

//    @PostConstruct
//    fun init(){
//
//        var connection = datasource.connection
//        println(connection.isValid(250))
//    }
}