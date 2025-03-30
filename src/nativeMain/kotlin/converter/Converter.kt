package converter

import errorSys.ErrorCodeType
import errorSys.ErrorSys
import okio.Path.Companion.toPath
import parser.Node
import parser.NodeType
import parser.Parser
import template.getAllTemplates

class BMLConverter {

    fun testConverterForTemplate() {
        println(
            convert(
                """<${"$"}notExport/>

<:Layout content="body">
    <!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title><#title/></title>
</head>
<body>
    <#body/>
</body>
</html>
</:Layout>"""
            )
        )
    }

    fun testConverterForSampleFile() {
        println(
            convert(
                """<${"$"}import src="script.js"/>
                    <script src="script.js"></script>
<:BigTitle content="BigTitle_content">
    <h1><#BigTitle_content/> - <#Name/></h1>
    <input type="<#typeInput/>" placeholder="Enter your name" />
    <hr />
</:BigTitle>

<Layout title="Test">
<${"$"}foreach range="7..10">
    <BigTitle Name="Title" typeInput="number"><#times/></BigTitle>
</${"$"}foreach>
</Layout>
<:Login content="Login_content">
    <input />
    <b><#Login_content/></b>
    <input />
</:Login>"""
            )
        )
    }

    private val parser = Parser()

    /* Config ErrorSys */
    var printOnAdding = true
    var breakOnError = true

    val error = ErrorSys("BML_Converter", printOnAdding, breakOnError)
    private var ui_components: MutableList<Node> = mutableListOf()
    private var variables: Variables = Variables()

    var importFileFun: (String) -> String? = { "hello" }

    fun convert(BMLcode: String, callbackImportFile: (String) -> String? = importFileFun): ConverterResult {
        importFileFun = callbackImportFile
        error.clearErrors()
        /* Parse the BML code */
        val content = parser.parse(BMLcode)

        /* Getting global information */
        val needToExport = needToExport(content)
        val otherSources = getOtherSources(content)

        /* Convert the BML code */
        val html_code = _convert(content)

        return ConverterResult(html_code, needToExport, otherSources, error.getErrors())
    }

    private fun _convert(content: List<Node>): String {
        var HTML = ""
        variables.clear()

        /* Get components */
        ui_components = getComponents(content).toMutableList()

        /* Get templates */
        val templates = getTemplates()
        ui_components.addAll(templates)

        /* Convert the BML code in HTML code*/
        content.forEach { node ->
            HTML += calculateHTMLCode(node)
        }

        return HTML
    }

    private fun needToExport(listN: List<Node>): Boolean {
        var needToExport = true
        listN.forEach { node ->
            var arr =
                parser.findNode(node) { it.type == NodeType.BML_FUNCTION && it.name == "${"$"}notExport" }
            if (arr.isNotEmpty())
                needToExport = false
        }
        return needToExport
    }

    private fun getOtherSources(listN: List<Node>): List<String> {
        var sources = mutableListOf<String?>()
        listN.forEach { node ->
            sources.addAll((parser.findNode(node) {
                it.type == NodeType.HTML_ELEMENT && it.name == "link" && it.attrs.contains(
                    "href"
                )
            }).map { it.attrs["href"] })
            sources.addAll((parser.findNode(node) {
                it.type == NodeType.HTML_ELEMENT && it.name == "script" && it.attrs.contains(
                    "src"
                )
            }).map { it.attrs["src"] })


        }
        return sources.filterNotNull()
    }

    private fun getComponents(contentParsed: List<Node>, components: List<Node> = listOf()): List<Node> {
        var listComponents = components.toMutableList()

        contentParsed.forEach { node ->
            if (node.type == NodeType.BML_COMPONENT) {
                listComponents.add(node)
            } else if (node.type == NodeType.BML_FUNCTION && node.name == "${"$"}import") {
                val fileSrc = getRealAttrValue(node.attrs["src"] ?: run {
                    error.addError(
                        errorSys.ErrorUnit(
                            ErrorCodeType.INVALID_ATTRIBUTE,
                            "The 'src' attribute is required for the import function at line ${node.line}"
                        )
                    )
                    return@forEach
                })
                val extension = fileSrc.substringAfterLast(".") // Get the extension of the file
                if (extension == "bml") {
                    val content = importFileFun(fileSrc)
                    if (content == null) {
                        error.addError(
                            errorSys.ErrorUnit(
                                ErrorCodeType.INVALID_FILE,
                                "The file '$fileSrc' is not found at line ${node.line}"
                            )
                        )
                        return@forEach
                    }
                    val parsed = parser.parse(content)
                    listComponents = getComponents(parsed, listComponents).toMutableList()
                }
            }

            if (node.children.isNotEmpty())
                listComponents = getComponents(node.children, listComponents).toMutableList()
        }
        return listComponents
    }

    private fun getTemplates(): List<Node> {
        val dirTemplates = "./templates".toPath()
        val templateList = getAllTemplates()
        /*val json = BMLConverter::class.java.getResource("${dirTemplates}/templates.json")?.readText()
        val type = object : TypeToken<Array<TemplateBML>>() {}.type
        val templateList = Gson().fromJson<Array<TemplateBML>>(json, type)
*/
        println(templateList)
        var components = listOf<Node>()

        templateList.forEach { content ->
            val parsed = parser.parse(content)
            components = getComponents(parsed, components)
        }

        return components
    }

    private fun calculateHTMLCode(
        node: Node
    ): String {
        var HTML_output = ""
        when (node.type) {
            NodeType.HTML_TEXT -> HTML_output += node.textContent
            NodeType.HTML_ELEMENT -> HTML_output += getHTML_Element(node)
            NodeType.BML_VARIABLE -> HTML_output += getBML_VARIABLE(node)
            NodeType.BML_FUNCTION -> HTML_output += getBML_FUNCTION(node)
            NodeType.BML_COMPONENT -> {}// Do nothing
            NodeType.UNKNOWN -> error.addError(
                errorSys.ErrorUnit(
                    ErrorCodeType.INVALID_NODE,
                    "The node is unknown at line ${node.line}"
                )
            )
        }
        return HTML_output
    }

    private fun getHTML_Element(node: Node): String {
        var HTML_output = ""

        // Check if the node call a component
        val maybeComponent = ui_components.find { it.name == ":" + node.name }

        if (maybeComponent != null) {
            // YES

            // Set the node's attributes in variables
            node.attrs.forEach { key, value -> variables[key] = getRealAttrValue(value) }

            // Calculate the HTML code of the node's children
            var HTMLChildrenNode = ""
            node.children.forEach { HTMLChildrenNode += calculateHTMLCode(it) }

            // Set the content of the component in variables
            maybeComponent.attrs["content"]?.let { content ->
                variables[content] = HTMLChildrenNode
            }

            // Calculate the HTML code of the component

            maybeComponent.children.forEach { HTML_output += calculateHTMLCode(it) }

        } else {
            // NO

            // Generate the HTML code of the node
            HTML_output = generateTag(node)
        }
        return HTML_output
    }

    private fun generateTag(node: Node): String {
        var HTML_output = "<${node.name}"
        node.attrs.forEach { key, value -> node.attrs[key] = getRealAttrValue(value) }
        HTML_output += node.attrs.print()
        if (node.isSelfClosing) {
            HTML_output += "/>\n"
        } else {
            HTML_output += ">"
            node.children.forEach { HTML_output += calculateHTMLCode(it) }
            HTML_output += "</${node.name}>\n"
        }
        return HTML_output
    }

    private fun getBML_VARIABLE(node: Node): String {
        val setAttrKey = "set"

        var isSet = node.attrs.contains(setAttrKey)
        var name_var = node.name.replace("#", "")

        if (isSet) {
            variables[name_var] = getRealAttrValue(node.attrs[setAttrKey]!!)
            return ""
        } else {
            return variables[name_var] ?: run {
                error.addError(
                    errorSys.ErrorUnit(
                        ErrorCodeType.UNDEFINED_VARIABLE,
                        "The variable '${name_var}' is not defined at line ${node.line}"
                    )
                )
                ""
            }
        }
    }

    private fun getBML_FUNCTION(node: Node): String {
        var HTML_output = ""

        when (node.name) {
            "${"$"}import" -> {
                // Import function
                val fileSrc = getRealAttrValue(node.attrs["src"] ?: run {
                    error.addError(
                        errorSys.ErrorUnit(
                            ErrorCodeType.INVALID_ATTRIBUTE,
                            "The 'src' attribute is required for the import function at line ${node.line}"
                        )
                    )
                    return ""
                })
                val content = importFileFun(fileSrc)?: run {
                    error.addError(
                        errorSys.ErrorUnit(
                            ErrorCodeType.INVALID_FILE,
                            "The file '$fileSrc' is not found at line ${node.line}"
                        )
                    )
                    return ""
                }
                val extension = fileSrc.substringAfterLast(".") // Get the extension of the file
                when (extension) {
                    "bml" -> {
                        /*val parsed = parser.parse(content)
                        HTML_output = _convert(parsed)*/
                    }

                    "html" -> HTML_output = content
                    "js" -> HTML_output = "<script>${content}</script>"
                    "css" -> HTML_output = "<style>${content}</style>"
                    else -> {
                        error.addError(
                            errorSys.ErrorUnit(
                                ErrorCodeType.INVALID_FILE_EXTENSION,
                                "The file extension '$extension' for '$fileSrc' is not supported at line ${node.line}"
                            )
                        )
                    }
                }
            }

            "${"$"}notExport" -> {
                // Not export function
            }

            "${"$"}foreach" -> {
                // ForEach function

                val rangeNb = (node.attrs["range"]?.split("..") ?: run {
                    error.addError(
                        errorSys.ErrorUnit(
                            ErrorCodeType.INVALID_ATTRIBUTE,
                            "The 'range' attribute is required for the foreach function at line ${node.line}"
                        )
                    )
                    return ""
                }).map { getRealAttrValue(it).toInt() }
                if (rangeNb.size != 2) {
                    error.addError(
                        errorSys.ErrorUnit(
                            ErrorCodeType.INVALID_ATTRIBUTE,
                            "The 'range' attribute must have 2 values separated by '..' at line ${node.line}\n" +
                                    "Example: range='0..10'"
                        )
                    )
                    return ""
                }
                for (n in rangeNb[0]..rangeNb[1]) {
                    variables["times"] = n.toString()
                    node.children.forEach { HTML_output += calculateHTMLCode(it) }
                }
            }

            else -> {
                error.addError(
                    errorSys.ErrorUnit(
                        ErrorCodeType.INVALID_FUNCTION,
                        "The function '${node.name}' is not defined at line ${node.line}"
                    )
                )
            }
        }


        return HTML_output
    }

    private fun getRealAttrValue(attr_value: String): String {
        // Check if the attribute value is a variable
        val regexVar = Regex("<#[a-zA-Z0-9_]+/>")
        if (regexVar.matches(attr_value)){
            val varName = regexVar.find(attr_value)!!.value.replace("<#", "").replace("/>", "")
            val varValue = variables[varName] ?: run {
                error.addError(
                    errorSys.ErrorUnit(
                        ErrorCodeType.UNDEFINED_VARIABLE,
                        "The variable '${varName}' is not defined"
                    )
                )
                ""
            }
            return attr_value.replace("<#${varName}/>", varValue)
        }
        return attr_value
    }
}