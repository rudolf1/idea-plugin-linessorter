package ua.rudolf.idea_plugin_linesorter

fun processSort(lines: List<SelectedLine>): List<Pair<String, SelectedLine?>> {
    return lines.map { it.selectionText }.sortedWith(Comparator { s1, s2 -> s1.compareTo(s2, ignoreCase = true) })
        .map { it to null }
}

class SortAction : AbstractAction(::processSort)