package errorSys

import kotlin.system.exitProcess

/**
 * Represents an error system for a specific component.
 *
 * @property component The name of the component associated with this error system.
 * @property printOnAdding Indicates if errors should be printed when they are added.
 * @property breakOnError Indicates if the program should break when an error is added.
 */
class ErrorSys(val component: String, val printOnAdding: Boolean = true,  val breakOnError: Boolean = false) {
    private val errors = mutableListOf<ErrorUnit>()

    /**
     * Adds an error to the error system.
     *
     * @param error The error to be added.
     */
    fun addError(error: ErrorUnit) {
        errors.add(error)
        if (printOnAdding) {
            printError(error)
        }
    }

    /**
     * Checks if there are any errors in the error system.
     *
     * @return True if there are errors, false otherwise.
     */
    fun hasErrors(): Boolean {
        return errors.isNotEmpty()
    }

    /**
     * Retrieves the list of errors in the error system.
     *
     * @return A list of errors.
     */
    fun getErrors(): List<ErrorUnit> {
        return errors
    }

    /**
     * Clears all errors from the error system.
     */
    fun clearErrors() {
        errors.clear()
    }

    /**
     * Prints all errors in the error system to the console.
     */
    fun printErrors() {
        errors.forEach {
            println("[$component] $it")
        }
    }

    /**
     * Prints a single error to the console.
     *
     * @param error The error to be printed.
     */
    fun printError(error: ErrorUnit){
        if (breakOnError) {
            throw Exception("[$component] $error")
        }else{
            println("[$component] $error")
        }
    }

    /**
     * Prints a single error to the console (alias for [printError]).
     *
     * @param error The error to be printed.
     */
    fun pE(error: ErrorUnit) {
        printError(error)
    }
    companion object {
        /**
         * Prints a single error to the console.
         *
         * @param error The error to be printed.
         */
        fun printError(error: ErrorUnit) {
            println(error)
        }

        /**
         * Prints a single error to the console (alias for [printError]).
         *
         * @param error The error to be printed.
         */
        fun pE(error: ErrorUnit) {
            printError(error)
        }
    }
}