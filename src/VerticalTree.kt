import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import java.util.*

class VerticalTree<T : UserType>(private var sample: UserType?) {
    private var root: VerticalTreeNode<T>?

    init {
        root = null
    }

    @get:JsonIgnore
    val size: Int?
        get() = root?.subtreeSize

    fun add(v: T) {
        if(root == null){
            root = VerticalTreeNode(v);
        }
        root?.add(v, sample?.getTypeComparator()!!)
    }

    operator fun get(index: Int): T? {
        if (index < 0 || index >= root?.subtreeSize ?: 0) {
            throw IndexOutOfBoundsException()
        }
        var curr: VerticalTreeNode<T?>? = root?.copy()
        for (i in 0 until index) {
            curr = curr!!.upSift()
        }
        return curr!!.data
    }

    fun remove(index: Int): T? {
        if (index < 0 || index >= root!!.subtreeSize) {
            throw IndexOutOfBoundsException()
        }
        val size = root!!.subtreeSize
        var curr: VerticalTreeNode<T?>? = root!!.copy()
        root = null
        var result: T? = null
        for (i in 0 until size) {
            if (i == index) {
                result = curr!!.data
            } else {
                curr!!.data?.let { add(it) }
            }
            curr = curr.upSift()
        }
        return result
    }

    fun balance() {
        var curr: VerticalTreeNode<T?>? = root!!.copy()
        root = null
        while (curr != null) {
            curr.data?.let { addBalanced(it) }
            curr = curr.upSift()
        }
    }

    fun forEach(d: DoWith) {
        var curr: VerticalTreeNode<T?>? = root!!.copy()
        while (curr != null) {
            d.doWith(curr.data)
            curr = curr.upSift()
        }
    }

    override fun toString(): String {
        return if (root == null) {
            "[EMPTY]"
        } else {
            val builder = StringBuilder()
            root!!.toString(builder, 0)
            builder.toString()
        }
    }

    private fun addBalanced(v: T) {
        if (root == null) {
            root = VerticalTreeNode(v)
        } else {
            root!!.addBalanced(v, sample!!.getTypeComparator())
        }
    }

    fun serialize(): String {
        return try {
            val mapper = ObjectMapper()
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            mapper.writeValueAsString(this)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val factory = UserFactory()
            val sample = factory.getBuilderByName("Integer")
            val tree = VerticalTree<UserType>(sample)
            val random = Random(1)
            val n = 20
            val limit = 100
            for (i in 0 until n) {
                val value = Integer(random.nextInt(limit))
                println("Inserted: $value")
                tree.add(value)
            }
            println()
            println("Sorted:")
            tree.forEach(object : DoWith {
                override fun doWith(obj: Any?) {
                    println(obj.toString())
                }
            })
            println()
            println("Indexes:")
            for (i in 0 until n) {
                println("Element #" + i + ": " + tree[i])
            }
            println()
            println("Structure:")
            println(tree)
            tree.balance()
            println("Structure after balancing:")
            println(tree)
            println()
            val toRemove = 5
            println("Removing elements:")
            for (i in 0 until toRemove) {
                val index = tree.size?.let { random.nextInt(it) }
                println("Removing element at index " + index + " ... :" + index?.let { tree.remove(it) })
                println(tree)
            }
        }

        fun <T : UserType?> deserialize(s: String?, clazz: Class<T>?): VerticalTree<UserType> {
            return try {
                val mapper = ObjectMapper()
                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                mapper.registerModule(SimpleModule().addAbstractTypeMapping(UserType::class.java, clazz))
                mapper.readValue(s, object : TypeReference<VerticalTree<UserType>>() {})
            } catch (e: JsonProcessingException) {
                throw RuntimeException(e)
            }
        }
    }
}