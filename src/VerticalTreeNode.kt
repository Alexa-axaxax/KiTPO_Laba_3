import java.util.*
import kotlin.Comparator

class VerticalTreeNode<T : UserType?>(data: T) {
    var data: T? = data
        private set
    var subtreeSize = 0
        private set
    private var children: MutableList<VerticalTreeNode<T>>? = null

    init {
        subtreeSize = 1
        children = ArrayList<VerticalTreeNode<T>>()
    }

    fun add(v: T, comparator: Comparator<Any>) {
        var v: T = v
        if (comparator.compare(data, v) > 0) {
            val sw = data
            data = v
            if (sw != null) {
                v = sw
            }
        }
        val childIndex = random.nextInt(children!!.size + 1)
        if (childIndex == children!!.size) {
            children!!.add(VerticalTreeNode(v))
        } else {
            children!![childIndex].add(v, comparator)
        }
        subtreeSize++
    }

    fun addBalanced(v: T, comparator: Comparator<Any>) {
        var v: T = v
        if (comparator.compare(data, v) > 0) {
            val sw = data
            data = v
            if (sw != null) {
                v = sw
            }
        }
        if (children!!.size < 2) {
            children!!.add(VerticalTreeNode(v))
        } else if (children!![0].subtreeSize > children!![1].subtreeSize) {
            children!![1].addBalanced(v, comparator)
        } else {
            children!![0].addBalanced(v, comparator)
        }
        subtreeSize++
    }

    fun copy(): VerticalTreeNode<T?> {
        val result = VerticalTreeNode(data)
        result.subtreeSize = subtreeSize
        for (child in children!!) {
            result.children!!.add(child.copy())
        }
        return result
    }

    fun upSift(): VerticalTreeNode<T>? {
        if (children!!.isEmpty()) {
            return null
        }
        var minIndex = -1
        for (i in children!!.indices) {
            val child = children!![i]
            if (minIndex == -1 || data?.getTypeComparator()!!.compare(children!![minIndex].data, child.data) > 0) {
                minIndex = i
            }
        }
        data = children!![minIndex].data
        val siftedChild = children!![minIndex].upSift()
        if (siftedChild == null) {
            children!!.removeAt(minIndex)
        }
        return this
    }

    fun toString(builder: StringBuilder, depth: Int) {
        if (depth > 0) {
            for (i in 0 until depth - 1) {
                builder.append("\t")
            }
            builder.append("|-- ")
        }
        builder.append(data.toString()).append(System.lineSeparator())
        for (child in children!!) {
            child.toString(builder, depth + 1)
        }
    }

    companion object {
        private val random = Random()
    }
}