package org.bw.beact.errorSys

class ErrorUnit(code: ErrorCodeType, vararg message: String){

    val content = "[${code.code.toString().padStart(4, '0')}] ERR: ${code.message}: ${message.joinToString(" ")}"
    override fun toString(): String = content
}
