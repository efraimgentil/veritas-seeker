package me.efraimgentil.seeker.repository

import me.efraimgentil.seeker.AbstractIT
import me.efraimgentil.seeker.domain.PollingDeputado
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import java.time.LocalDate

class PollingDeputadoRepositoryIT : AbstractIT() {

    @Autowired
    lateinit var repository : PollingDeputadoRepository

    @Test
    fun test(){

//        repository.save(PollingDeputado(id=0, deputadoId = 1, lastPull = LocalDate.now()))
//
//        print(repository)

        //var findAll = repository.findAll(Sort(Sort.Direction.ASC, "lastPull"))
//        var a = repository.findAllWithLastPullBefore( LocalDate.of(2019-0, 9 ,1))
//
//        print(a)
    }

}