package ua.rudolf.idea_plugin_linesorter

fun processUnique(lines: List<SelectedLine>): List<Pair<String, SelectedLine?>> {
    return lines.map { it.selectionText }.distinct().map { it to null }
}

class UniqueAction : AbstractAction(::processUnique)