package ua.rudolf.idea_plugin_linesorter

fun processUnique(lines: List<String>): List<String> {
    return lines.distinct()
}

class UniqueAction : AbstractAction(::processUnique)