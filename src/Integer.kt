import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.InputStreamReader
import java.util.*
import kotlin.Comparator

class Integer @JvmOverloads constructor(private var value: Int = 0) : UserType {
    override fun typeName(): String {
        return "Integer"
    }

    override fun create(): Any? {
        return Integer(0)
    }

    override fun clone(): Any {
        return Integer(value)
    }

    override fun readValue(`in`: InputStreamReader?): Any? {
        val scanner = Scanner(`in`)
        value = scanner.nextInt()
        return this
    }

    override fun parseValue(ss: String?): Any? {
        return Integer(ss!!.toInt())
    }

    @JsonIgnore
     override fun getTypeComparator(): Comparator<Any> {
        return Comparator { o1: Any, o2: Any -> java.lang.Integer.compare((o1 as Integer).value, (o2 as Integer).value) }
    }

    override fun toString(): String {
        return java.lang.Integer.toString(value)
    }
}