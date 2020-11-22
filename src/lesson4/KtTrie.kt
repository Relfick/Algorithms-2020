package lesson4

import java.util.*
import kotlin.collections.ArrayDeque

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
    }

    private var root = Node()

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
        private val stack = ArrayDeque<String>()
        private var current: String? = null

        init {
            if (root.children.isNotEmpty()) {
                findWords("", root)
            }
        }

        // Трудоемкость O(N)
        // Ресурсоемкость O(M)
        // N - суммарное количество букв в дереве, M - сумма букв во всех словах
        private fun findWords(word: String, currentNode: Node) {
            if (currentNode.children.containsKey(0.toChar()))
                stack.add(word)

            currentNode.children.forEach { if (it.key != 0.toChar()) findWords(word + it.key, it.value) }
        }

        // Трудоемкость O(1)
        override fun hasNext(): Boolean = stack.isNotEmpty()

        // Трудоемкость O(1)
        override fun next(): String {
            if (stack.isEmpty()) throw IllegalStateException()
            current = stack.removeLast()
            return current!!
        }

        // Трудоемкость O(N)
        override fun remove() {
            if (current == null || !this@KtTrie.remove(current)) throw IllegalStateException()
            current = null
        }
    }
}