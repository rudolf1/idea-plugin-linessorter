package ua.rudolf.idea_plugin_linesorter

fun processUniqueWithDrop(lines: List<String>): List<String> {
    val p1 = processUnique(lines)
    val toRemove = p1.toMutableSet()

    val drop = lines.filter {
        val result = toRemove.contains(it)
        toRemove.remove(it)
        !result
    }
    return p1 + "" + drop
}

class UniqueWithDropAction : AbstractAction(::processUniqueWithDrop)