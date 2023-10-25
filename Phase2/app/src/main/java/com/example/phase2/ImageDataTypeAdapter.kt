import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import android.net.Uri
import com.example.phase2.ImageData

class ImageDataTypeAdapter : TypeAdapter<ImageData>() {
    override fun write(out: JsonWriter, value: ImageData) {
        out.beginObject()
        out.name("src").value(value.src.toString())
        out.name("x").value(value.x)
        out.name("y").value(value.y)
        out.endObject()
    }

    override fun read(`in`: JsonReader): ImageData {
        var src: Uri? = null
        var x: Float = 0f
        var y: Float = 0f

        `in`.beginObject()
        while (`in`.hasNext()) {
            when (`in`.nextName()) {
                "src" -> src = Uri.parse(`in`.nextString())
                "x" -> x = `in`.nextDouble().toFloat()
                "y" -> y = `in`.nextDouble().toFloat()
                else -> `in`.skipValue()
            }
        }
        `in`.endObject()

        return ImageData(src ?: Uri.EMPTY, x, y)
    }
}
