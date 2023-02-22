import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.InputStreamReader
import java.util.*
import kotlin.Comparator

class PolarPoint @JvmOverloads constructor(private var distance: Double = 0.0, angle: Double = 0.0) : UserType {
    private var angle: Double

    override fun typeName(): String? {
        return "Polar point"
    }

    override fun create(): Any? {
        return PolarPoint(1.0, 0.0)
    }

    override fun clone(): Any {
        return PolarPoint(distance, angle)
    }

    override fun readValue(`in`: InputStreamReader?): Any? {
        val scanner = Scanner(`in`)
        distance = scanner.nextDouble()
        angle = normalizeAngle(scanner.nextDouble())
        return this
    }

    override fun parseValue(ss: String?): Any? {
        val parts = ss!!.split(",".toRegex()).toTypedArray()
        val angle = parts[1].toDouble()
        return PolarPoint(parts[0].trim { it <= ' ' }.toDouble(), normalizeAngle(angle))
    }

    @JsonIgnore
    override fun getTypeComparator(): Comparator<Any> {
        return Comparator { o1: Any, o2: Any ->
            val p1 = o1 as PolarPoint
            val p2 = o2 as PolarPoint
            val diff = java.lang.Double.compare(p1.distance, p2.distance)
            if (diff != 0) {
                return@Comparator diff
            }
            java.lang.Double.compare(p1.angle, p2.angle)
        }
    }

    override fun toString(): String {
        return "(" + String.format("%.2f", distance) + "; " + String.format("%.2f", angle) + ")"
    }

    private fun normalizeAngle(angle: Double): Double {
        var angle = angle
        if (angle < 0) {
            val c = Math.floor(-angle / ANGLE_LIMIT)
            angle = (c + 1) * ANGLE_LIMIT
        }
        val c = Math.floor(angle / ANGLE_LIMIT)
        return angle - c * ANGLE_LIMIT
    }

    companion object {
        private const val ANGLE_LIMIT = 2 * Math.PI
    }

    init {
        this.angle = normalizeAngle(angle)
    }
}