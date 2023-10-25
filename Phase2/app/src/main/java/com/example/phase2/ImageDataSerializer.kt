import com.example.phase2.ImageData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class ImageDataSerializer : JsonSerializer<ImageData> {
    override fun serialize(
        src: ImageData,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        val jsonObject = JsonObject()
        jsonObject.addProperty("src", src.src.toString())
        jsonObject.addProperty("x", src.x)
        jsonObject.addProperty("y", src.y)
        return jsonObject
    }
}
