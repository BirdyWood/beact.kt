package org.bw.beact.parser

/**
 * Represents a node in a tree structure.
 *
 * @property name The name of the node.
 * @property type The type of the node, default is NodeType.HTML_ELEMENT.
 * @property children The list of child nodes.
 * @property attrs The list of attributes associated with the node.
 * @property textContent The text content of the node.
 * @property isSelfClosing Indicates if the node is self-closing.
 * @property line The line number where the node is defined, default is -1.
 */
data class Node(
    val name: String,
    var type: NodeType = NodeType.HTML_ELEMENT,
    val children: MutableList<Node> = mutableListOf(),
    val attrs: Attrs = Attrs(),
    val textContent: String = "",
    val isSelfClosing: Boolean = false,
    val line: Int = -1,
) {
    init {
        /* Validate the node */
        if (isSelfClosing && children.isNotEmpty()) {
            throw IllegalArgumentException("Self closing tag cannot have children")
        }
        if (isSelfClosing && textContent.isNotEmpty()) {
            throw IllegalArgumentException("Self closing tag cannot have text content")
        }
        if (textContent.isNotEmpty() && children.isNotEmpty()) {
            throw IllegalArgumentException("Tag cannot have both text content and children")
        }

        /* Determine the type of node */
        if (name == "#text") {
            type = NodeType.HTML_TEXT
        }
        else if (name.startsWith("#")) {
            type = NodeType.BML_VARIABLE
        }
        else if (name.startsWith("$")) {
            type = NodeType.BML_FUNCTION
        }
        else if (name.startsWith(":")) {
            type = NodeType.BML_COMPONENT
        }
    }
    /**
     * Adds a child node to this node.
     *
     * @param node The child node to be added.
     */
    fun addChild(node: Node) {
        this.children.add(node)
    }

    /**
     * Prints the node and its children to the console.
     */
    fun print(indentSize: Int = 2) {
        print(0, indentSize)
    }

    /**
     * Prints the node and its children to the console with indentation.
     *
     * @param level The current level of indentation.
     */
    private fun print(level: Int, indentSize: Int) {
        if (type == NodeType.HTML_TEXT) {
            println(" ".repeat(level * indentSize) + textContent)
            return
        }
        println(" ".repeat(level * indentSize) +name)
        children.forEach { it.print(level + 1, indentSize) }
        if (!isSelfClosing){
            println(" ".repeat(level * indentSize) + "/$name")
        }
    }
}