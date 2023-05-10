package fr.groupbees.domain_ptransform

import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets

object ResourceUtil {

    /**
     * Gets the string content of the given resource file path.
     */
    fun toStringContent(resourceFilePath: String): String {
        val inputStream = ResourceUtil::class.java.classLoader.getResourceAsStream(resourceFilePath)

        return IOUtils.toString(inputStream, StandardCharsets.UTF_8)
    }
}