package parser

import errorSys.ErrorCodeType
import errorSys.ErrorSys
import errorSys.ErrorUnit

class Parser {
    fun test() {
        println("Parser test")
        val testValue = """
            <${'$'}import src="script.js"/>
<:BigTitle content="BigTitle_content">
    <h1><#BigTitle_content/> - <#Name/></h1>
    <hr />
</:BigTitle>

<Layout title="Test">
<${'$'}foreach times="10">
    <BigTitle Name="Title"><#times/></BigTitle>
</${'$'}foreach>
</Layout>
<:Login content="Login_content">
    <input />
    <b><#Login_content/></b>
    <input />
</:Login>
        """.trimIndent()

        val returnvalue = parse(testValue)
        returnvalue.forEach {
            it.print(3)
        }
    }

    var line = 1
    var charOnLine = 0
    var current = 0
    var bmlString = ""

    private val error = ErrorSys("BML_Parser", true, true)

    fun parse(bmlToParse: String): List<Node> {
        error.clearErrors()

        line = 1
        charOnLine = 0
        current = 0
        bmlString = bmlToParse
        return _parse()
    }

    private fun _parse(): List<Node> {
        var nodes = mutableListOf<Node>()
        var stack = mutableListOf<Node>()

        while (current < bmlString.length) {
            skipWhitespace()

            val c = advance()
            val nextC = peek()
            if (c == '<') {
                if (nextC == '/') { // Closing tag
                    advance(); // skip the '/'

                    val tagName = closingTag();
                    if (stack.isEmpty()) {
                        error.addError(
                            ErrorUnit(
                                ErrorCodeType.INVALID_SYNTAX, "ending tag found without an opening tag at",
                                "$line:$charOnLine"
                            )
                        )
                    }
                    val openingTag = stack[stack.size - 1].name;
                    if (openingTag != tagName) {
                        error.addError(
                            ErrorUnit(
                                ErrorCodeType.INVALID_SYNTAX, "Missing closing tag for:", openingTag, "at",
                                "$line:$charOnLine"
                            )
                        )
                    }
                    stack.removeAt(stack.size - 1);

                } else if (nextC == '?') { // like <?xml version="1.0" encoding="iso-8859-1"?>
                    skipDeclaration();
                } else if (nextC == '!') { // <!-- Time-stamp: "bibliography.xml   3 Mar 2008 16:24:04" -->
                    skipDeclaration();
                }/* else if (nextC == '$') { // New special command for advanced HTML
                skipDeclaration(); // **Obsolete**
            }*/ /*else if (nextC == '#') { // Variable iteration
                    advance(); // skip the '#'

                    skipWhitespace()


                    val node = this.variableNode();
                    if (stack.isNotEmpty()) {
                        val parent = stack[stack.size - 1];
                        parent.addChild(node);
                    }

                }*/ else { // Opening tag
                    val node = this.tag();

                    var parent: Node? = null
                    if (stack.isNotEmpty()) {
                        parent = stack[stack.size - 1];



                        parent.addChild(node);
                    }

                    // Push the node on to the stack so that its children can be parsed
                    if (!node.isSelfClosing) {
                        stack.add(node);
                    }

                    // Also, add the node to the 'nodes' array if it's a top level node
                    if (parent == null) {
                        nodes.add(node);
                    }
                }

            } else { // Text node

                var parent: Node? = null;
                if (stack.size > 0) {
                    parent = stack[stack.size - 1];
                }
                val textNode = this.textNode();
                if (textNode.textContent != "") {
                    parent?.addChild(textNode)
                }
            }
        }


        return nodes
    }

    private fun peek(): Char {
        return bmlString[current]
    }

    private fun advance(): Char {
        if (peek() == '\n') {
            line += 1;
            charOnLine = 0;
        }
        charOnLine++
        current++;

        return bmlString[current - 1];
    }

    private fun skipWhitespace() {
        while (true) {
            val c = peek();
            when (c) {
                ' ', '\r', '\t', '\n' -> {
                    advance();
                }

                else -> {
                    return;
                }
            }
        }
    }

    private fun skipDeclaration() {
        while (true) {
            val c = peek();
            if (c == '>') {
                advance();
                return;
            }
            advance();
        }
    }

    private fun closingTag(): String {
        this.skipWhitespace()
        val start = current;

        while (true) {
            val c = peek()
            if (c == '>') {
                advance() // skip the ending '>'
                return bmlString.slice(start until current - 1)
            }
            advance()
        }
    }

    private fun tag(): Node {
        val name = nodeName()
        var isSelfClosing = false;
        val attrs_list = attrs()

        val closerCh = advance()
        if (closerCh == '/') { // self closing tag
            isSelfClosing = true;
            advance()
            if (current < bmlString.length) {
                advance()
            }
        } else if (closerCh == '>') {
            advance()
        } else {
            error.addError(
                ErrorUnit(
                    ErrorCodeType.UNEXPECTED_CHAR, "Invalid closing character '${closerCh}' at",
                    "$line:$charOnLine"
                )
            )
        }

        return Node(
            name = name,
            attrs = attrs_list,
            isSelfClosing = isSelfClosing,
            line = line
        )
    }

    private fun nodeName(): String {
        this.skipWhitespace()
        val start = current;

        val breakChars = arrayOf(' ', '\n', '\r', '\t', '>', '/')

        while (true) {
            val c = peek()
            if (breakChars.contains(c)) {
                break;
            }
            advance()
        }

        return bmlString.slice(start until this.current)
    }

    private fun attrs(prevAttrs: Attrs = Attrs()): Attrs {
        var currentAttrs = prevAttrs
        this.skipWhitespace()
        val c = this.peek()
        if (c == '/' || c == '>') { // end of attrs
            return currentAttrs;
        }

        var key = ""
        val breakChars = arrayOf(' ', '\r', '\n', '\t')
        val keyStart = current

        while (true) { // This loop parses the key
            val c = peek()
            if (breakChars.contains(c)) { // boolean attribute
                key = bmlString.slice(keyStart until current)
                currentAttrs[key] = "true"
                return attrs(currentAttrs)
            } else if (c == '=') { // Attribute with value
                key = bmlString.slice(keyStart until current)
                break;
            } else {
                advance()
            }
        }

        skipWhitespace()
        advance() // skip the '='

        val openingChar = advance() // skip the opening char '"' OR "'"
        var value = ""
        val valueStart = current

        while (true) { // this loop parses the value
            val c = this.peek()
            if (c == openingChar && bmlString[current - 1].code != 92) {
                // Check for ending '"', ignore '"' if preceded by '\' as that means that it has been escaped
                value = bmlString.slice(valueStart until current)
                advance() // skip the ending '"' Or "'"
                break;
            } else {
                advance()
            }
        }
        skipWhitespace()

        currentAttrs[key] = value
        return attrs(currentAttrs)
    }

    private fun textNode(): Node {
        val tempLine = line
        rewindTo('>') // Rewind to the '>' character in order to preserve whitespace characters
        val start = this.current
        while (true) {
            val c = this.peek()
            if (c == '<' || current >= bmlString.length - 1) {
                val tNode = Node(
                    name = "#text",
                    children = mutableListOf(),
                    attrs = Attrs(),
                    textContent = bmlString.slice(start until current),
                    isSelfClosing = false,
                    line = line
                )
                return tNode
            }
            this.advance()
        }
    }

    private fun rewindTo(ch: Char) {
        while (true) {
            if (this.current == 0) {
                return;
            }
            val c = this.peek()
            if (c == ch) {
                this.advance()
                return;
            }
            this.current -= 1;
        }
    }

    private fun variableNode(): Node {

        val name = this.nodeName()
        var isSelfClosing = false;

        val closerCh = this.advance()
        if (closerCh == '/') { // self closing tag
            isSelfClosing = true;
            this.advance()
            this.advance()
        } else if (closerCh == '>') {
            this.advance()
        } else {
            error.addError(
                ErrorUnit(
                    ErrorCodeType.UNEXPECTED_CHAR, "Invalid closing character at",
                    "$line:$charOnLine"
                )
            )
        }

        val tNode = Node(
            name = "#$name",
            children = mutableListOf(),
            attrs = Attrs(),
            isSelfClosing = false,
            line = line
        )
        return tNode
    }

    public fun findNode(node: Node, predicate: (Node) -> Boolean): List<Node> {
        if (predicate(node)) {
            return listOf(node)
        }
        var matches = mutableListOf<Node>()

        fun throughChildren (node: Node) {
            if (predicate(node)) {
                matches.add(node)
            }

            for (child in node.children) {
                throughChildren(child)
            }
        }

        return matches
    }
}