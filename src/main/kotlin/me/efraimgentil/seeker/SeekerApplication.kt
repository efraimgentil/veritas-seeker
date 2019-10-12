package me.efraimgentil.seeker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class SeekerApplication

fun main(args: Array<String>) {
	runApplication<SeekerApplication>(*args)
}
