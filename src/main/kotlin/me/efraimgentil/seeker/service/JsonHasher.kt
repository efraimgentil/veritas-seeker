package me.efraimgentil.seeker.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import me.efraimgentil.seeker.client.dto.DespesaDTO
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import java.util.TreeMap

@Service
class JsonHasher() {
    val objectMapper = ObjectMapper()

    init {
        objectMapper.setNodeFactory(SortingNodeFactory())
    }

    fun generateHashFor(jsonNode : JsonNode) : String {
        return DigestUtils.md5DigestAsHex(orderedJsonByteArray(jsonNode))
    }

    private fun <T> orderedJsonByteArray(target : T) : ByteArray =
            objectMapper.valueToTree<JsonNode>(target).toString().toByteArray()

    internal class SortingNodeFactory : JsonNodeFactory() {
        override fun objectNode(): ObjectNode {
            return ObjectNode(this, TreeMap())
        }
    }

}