package converter

/**
 * Represents a collection of variables.
 *
 * @property variables A map of attribute names to their values.
 */
data class Variables(val variables: MutableMap<String, String> = mutableMapOf()) {
    /**
     * Gets the value of a variable by its name.
     *
     * @param key The name of the variable.
     * @return The value of the variable, or null if not found.
     */
    operator fun get(key: String) = variables[key]

    /**
     * Sets the value of a variable.
     *
     * @param key The name of the variable.
     * @param value The value of the variable.
     */
    operator fun set(key: String, value: String) {
        variables[key] = value
    }

    /**
     * Loop on each variable.
     *
     * @param action The function done on each variable.
     */
    fun forEach(action: (String, String) -> Unit) {
        variables.forEach { map -> action(map.key, map.value) }
    }

    /**
     * Clear all variables.
     */
    fun clear() {
        variables.clear()
    }

    /**
     * Check if the variable exists.
     *
     * @param key The name of the variable.
     * @return True if the variable exists, false otherwise.
     */
    fun contains(key: String) = variables.containsKey(key)
}