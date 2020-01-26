package ua.rudolf.idea_plugin_linesorter

fun processGroup(list: List<SelectedLine>) =
    list
        .groupBy { it.selectionText }
        .flatMap {
            listOf(it.key to null)
                .plus(it.value.map { it.text to it })
        }

class GroupBySelectionAction : AbstractAction(::processGroup)