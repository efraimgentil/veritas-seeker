package me.efraimgentil.seeker.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.postgresql.util.PGobject
import javax.persistence.AttributeConverter


class JsonConverter : AttributeConverter<String, PGobject> {
    private val jacksonObjectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(entityValue: String?): PGobject {
        var out : PGobject = PGobject()
        out.type = "json"
        out.value = entityValue
        return out
    }

    override fun convertToEntityAttribute(dataBaseValue: PGobject?): String {
        return jacksonObjectMapper.readValue<String>(dataBaseValue!!.value)
    }
}