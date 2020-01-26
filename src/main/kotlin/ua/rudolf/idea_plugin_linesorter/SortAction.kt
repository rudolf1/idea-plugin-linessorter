package ua.rudolf.idea_plugin_linesorter

fun processSort(lines: List<String>): List<String> {
    return lines.sortedWith(Comparator { s1, s2 -> s1.compareTo(s2, ignoreCase = true) })
}

class SortAction : AbstractAction(::processSort)