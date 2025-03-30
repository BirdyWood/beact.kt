package converter

import errorSys.ErrorUnit

data class ConverterResult(val htmlCode: String, val needToExport: Boolean, val otherSourceToImport:List<String>, val error: List<ErrorUnit>) {
    val hasError = error.isNotEmpty()
}
