import java.io.InputStreamReader
import kotlin.Comparator

interface UserType : Cloneable {
    fun typeName(): String? // Имя типа
    fun create(): Any? // Создает объект ИЛИ
    public override fun clone(): Any // Клонирует текущий
    fun readValue(`in`: InputStreamReader?): Any? // Создает и читает объект
    fun parseValue(ss: String?): Any? // Создает и парсит содержимое из строки
    fun getTypeComparator(): Comparator<Any>

}