package fr.groupbees.domain_ptransform

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.IOException
import java.io.InputStream

object JsonUtil {
    private var OBJECT_MAPPER: ObjectMapper

    fun <T> deserialize(json: String, clazz: Class<T>): T {
        return try {
            OBJECT_MAPPER.readValue(json, clazz)
        } catch (e: JsonProcessingException) {
            throw IllegalStateException("Error Json deserialization")
        }
    }

    fun <T> deserializeFromResourcePath(resourcePath: String, reference: TypeReference<List<T>>): List<T> {
        val stream = JsonUtil::class.java
            .classLoader
            .getResourceAsStream(resourcePath)
        return deserializeToList(stream!!, reference)
    }

    fun <T, R> deserializeMapFromResourcePath(
        resourcePath: String,
        reference: TypeReference<Map<T, R>>
    ): Map<T, R> {
        val stream = JsonUtil::class.java
            .classLoader
            .getResourceAsStream(resourcePath)
        return deserializeToMap(stream!!, reference)
    }

    fun <T> deserializeToList(json: String, reference: TypeReference<List<T>>): List<T> {
        return try {
            OBJECT_MAPPER.readValue(json, reference)
        } catch (e: JsonProcessingException) {
            throw IllegalStateException("Error Json deserialization")
        }
    }

    fun <T, R> deserializeToMap(json: String, reference: TypeReference<Map<T, R>>): Map<T, R> {
        return try {
            OBJECT_MAPPER.readValue(json, reference)
        } catch (e: IOException) {
            throw IllegalStateException("Error Json deserialization")
        }
    }

    fun <T, R> deserializeToMap(stream: InputStream, reference: TypeReference<Map<T, R>>): Map<T, R> {
        return try {
            OBJECT_MAPPER.readValue(stream, reference)
        } catch (e: IOException) {
            throw IllegalStateException("Error Json deserialization")
        }
    }

    fun <T> deserializeToList(stream: InputStream, reference: TypeReference<List<T>>): List<T> {
        return try {
            OBJECT_MAPPER.readValue(stream, reference)
        } catch (e: IOException) {
            throw IllegalStateException("Error Json deserialization")
        }
    }

    fun <T> serialize(obj: T): String {
        return try {
            OBJECT_MAPPER.writeValueAsString(obj)
        } catch (e: JsonProcessingException) {
            throw IllegalStateException("Error Json serialization")
        }
    }

    init {
        val mapper = ObjectMapper()
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)

        OBJECT_MAPPER = mapper
    }
}