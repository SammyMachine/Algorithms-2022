package lesson4

import java.util.*

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: SortedMap<Char, Node> = sortedMapOf()
    }

    private val root = Node()

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator(): MutableIterator<String> = TrieIterator()

    inner class TrieIterator internal constructor() : MutableIterator<String> {

        private val stack: ArrayDeque<MutableIterator<Map.Entry<Char, Node>>> = ArrayDeque()
        private val stringBuilder = StringBuilder()
        private var nextWord = ""
        private var counter = 0

        init {
            initStack()
        }

        private fun initStack() = stack.push(root.children.entries.iterator())

        // Ресурсоемкость O(1), трудоемкость O(1)
        override fun hasNext(): Boolean = size > counter

        // Ресурсоемкость O(h), трудоемкость O(n), где h - высота дерева
        override fun next(): String {
            if (!hasNext()) throw NoSuchElementException()
            findNext()
            return nextWord
        }

        private fun findNext() {
            var checkerForStack = stack.peek()
            while (checkerForStack != null) {
                while (checkerForStack.hasNext()) {
                    val entry = checkerForStack.next()
                    if (entry.key == 0.toChar()) {
                        nextWord = stringBuilder.toString()
                        counter++
                        return
                    }
                    checkerForStack = entry.value.children.entries.iterator()
                    stack.push(checkerForStack)
                    stringBuilder.append(entry.key)
                }
                stack.pop()
                if (stringBuilder.isNotEmpty())
                    stringBuilder.deleteCharAt(stringBuilder.length - 1)
                checkerForStack = stack.peek()
            }
        }

        // Ресурсоемкость O(1), трудоемкость O(1)
        override fun remove() {
            if (nextWord == "") throw IllegalStateException()
            if (stack.peek() != null) {
                stack.peek().remove()
                nextWord = ""
                size--
                counter--

            }
        }
    }

}