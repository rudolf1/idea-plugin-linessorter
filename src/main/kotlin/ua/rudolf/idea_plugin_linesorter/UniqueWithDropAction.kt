package ua.rudolf.idea_plugin_linesorter

fun processUniqueWithDrop(lines: List<SelectedLine>): List<Pair<String, SelectedLine?>> {
    val p1 = processUnique(lines)
    val toRemove = p1.toMap().toMutableMap()

    val drop = lines
        .filter {
            val result = toRemove.containsKey(it.selectionText)
            toRemove.remove(it.selectionText)
            !result
        }
        .map { it.selectionText to it }

    return p1 + ("" to null) + drop
}

class UniqueWithDropAction : AbstractAction(::processUniqueWithDrop)