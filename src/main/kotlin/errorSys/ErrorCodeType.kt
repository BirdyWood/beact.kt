package org.bw.beact.errorSys

/**
 * Represents an error code type.
 *  @property code The error code.
 *  @property message The error message.
 *
 *  @sample ErrorSample
 */
enum class ErrorCodeType(val code: Int, val message : String) {
    UNEXPECTED_CHAR(0, "Unexpected character"),
    UNEXPECTED_TOKEN(1, "Unexpected token"),
    UNEXPECTED_EOF(2, "Unexpected end of file"),
    INVALID_TOKEN(3, "Invalid token"),
    INVALID_SYNTAX(4, "Invalid syntax"),
    INVALID_TYPE(5, "Invalid type"),
    INVALID_VALUE(6, "Invalid value"),
    INVALID_ARGUMENT(7, "Invalid argument"),
    INVALID_STATEMENT(8, "Invalid statement"),
    INVALID_EXPRESSION(9, "Invalid expression"),
    INVALID_DECLARATION(10, "Invalid declaration"),
    INVALID_ASSIGNMENT(11, "Invalid assignment"),
    INVALID_OPERATION(12, "Invalid operation"),
    INVALID_CONDITION(13, "Invalid condition"),
    INVALID_ITERATION(14, "Invalid iteration"),
    INVALID_FUNCTION(15, "Invalid function"),
    INVALID_COMPONENT(16, "Invalid component"),
    INVALID_ATTRIBUTE(17, "Invalid attribute"),
    INVALID_ELEMENT(18, "Invalid element"),
    INVALID_NODE(19, "Invalid node"),
    INVALID_TREE(20, "Invalid tree"),
    INVALID_GRAPH(21, "Invalid graph"),
    INVALID_PATH(22, "Invalid path"),
    INVALID_FILE(23, "Invalid file"),
    UNDEFINED_VARIABLE(24, "Undefined variable"),
    INVALID_FILE_EXTENSION(25, "Invalid file extension"),
}

enum class ErrorSample (comment: String) {
    UNEXPECTED_CHAR("Unexpected character"),
    UNEXPECTED_TOKEN("Unexpected token"),
    UNEXPECTED_EOF("Unexpected end of file"),
    INVALID_TOKEN("Invalid token"),
    INVALID_SYNTAX("Invalid syntax"),
    INVALID_TYPE("Invalid type"),
    INVALID_VALUE("Invalid value"),
    INVALID_ARGUMENT("Invalid argument"),
    INVALID_STATEMENT("Invalid statement"),
    INVALID_EXPRESSION("Invalid expression"),
    INVALID_DECLARATION("Invalid declaration"),
    INVALID_ASSIGNMENT("Invalid assignment"),
    INVALID_OPERATION("Invalid operation"),
    INVALID_CONDITION("Invalid condition"),
    INVALID_ITERATION("Invalid iteration"),
    INVALID_FUNCTION("Invalid function"),
    INVALID_COMPONENT("Invalid component"),
    INVALID_ATTRIBUTE("Invalid attribute"),
    INVALID_ELEMENT("Invalid element"),
    INVALID_NODE("Invalid node"),
    INVALID_TREE("Invalid tree"),
    INVALID_GRAPH("Invalid graph"),
    INVALID_PATH("Invalid path"),
    INVALID_FILE("Invalid file"),
    UNDEFINED_VARIABLE("Undefined variable"),
    INVALID_FILE_EXTENSION("Invalid file extension"),
}
