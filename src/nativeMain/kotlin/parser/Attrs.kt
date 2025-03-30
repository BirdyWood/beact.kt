package parser

/**
 * Represents a collection of attributes.
 *
 * @property attrs A map of attribute names to their values.
 */
data class Attrs(val attrs: MutableMap<String, String> = mutableMapOf()) {
    /**
     * Gets the value of an attribute by its name.
     *
     * @param key The name of the attribute.
     * @return The value of the attribute, or null if not found.
     */
    operator fun get(key: String) = attrs[key]

    /**
     * Sets the value of an attribute.
     *
     * @param key The name of the attribute.
     * @param value The value of the attribute.
     */
    operator fun set(key: String, value: String) {
        attrs[key] = value
    }

    /**
     * Loop on each attribute.
     *
     * @param action The function done on each attribute.
     */
    fun forEach(action: (String, String) -> Unit) {
        attrs.forEach { action(it.key, it.value) }
    }

    /**
     * Clear all attributes.
     */
    fun clear() {
        attrs.clear()
    }

    /**
     * Check if the attribute exists.
     *
     * @param key The name of the attribute.
     * @return True if the attribute exists, false otherwise.
     */
    fun contains(key: String) = attrs.containsKey(key)

    /**
     * Print attributes correctly for an HTML element.
     *
     * @return The string representation of the attributes.
     */
    fun print(): String {
        var str = ""
        attrs.forEach { if(it.value == "true") {str += " ${it.key}"} else {str += " ${it.key}=\"${it.value}\""} }
        return str
    }
}