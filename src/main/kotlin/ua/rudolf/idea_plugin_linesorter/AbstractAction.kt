package ua.rudolf.idea_plugin_linesorter

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.CaretState
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler
import com.intellij.openapi.editor.actions.TextComponentEditorAction
import java.lang.Math.min

data class SelectedLine(
    val selectionRange: IntRange,
    val selectionText: String,
    val range: IntRange,
    val text: String
)

open class AbstractAction(f: (List<SelectedLine>) -> List<Pair<String, SelectedLine?>>) :
    TextComponentEditorAction(Handler(f)) {

    private class Handler(private val f: (List<SelectedLine>) -> List<Pair<String, SelectedLine?>>) :
        EditorWriteActionHandler() {
        override fun executeWriteAction(editor: Editor, dataContext: DataContext) {
            val lines = extractLines(editor)
            val processedLines = f(lines)
            val replaceRange = lines.first().range
            lines.filterIndexed { i, _ -> i != 0 }
                .reversed()
                .forEach {
                    editor.document.replaceString(
                        it.range.first,
                        min(editor.document.textLength, it.range.last + 1),
                        ""
                    )
                }
            editor.document.replaceString(
                replaceRange.first,
                replaceRange.last,
                processedLines.joinToString("\n")
            )
            editor.selectionModel.removeSelection(true)
            editor.caretModel.removeSecondaryCarets()
            val firstLineNumber = editor.document.getLineNumber(replaceRange.first)
            val caretState = processedLines.mapIndexed { i, x ->
                val selection = x.second
                if (selection != null) {
                    val start = LogicalPosition(firstLineNumber + i, selection.selectionRange.first)
                    val end = LogicalPosition(firstLineNumber + i, selection.selectionRange.last)
                    CaretState(
                        end,
                        start,
                        end
                    )
                } else null
            }.filterNotNull()
            editor.caretModel.caretsAndSelections = caretState
        }

        private fun extractLines(editor: Editor): List<SelectedLine> {
            val doc = editor.document
            val selectedRangesInDoc = if (editor.selectionModel.blockSelectionStarts.isNotEmpty()) {
                editor.selectionModel.blockSelectionStarts.zip(editor.selectionModel.blockSelectionEnds)
                    .map { it.first until it.second }
            } else {
                doc.text
                    .mapIndexed { i, v -> i to v }
                    .filter { it.second == '\n' }
                    .map { it.first }
                    .zipWithNext { a, b -> a..b }
                    .let {
                        if (it.last().first == it.last().last) {
                            it.subList(0, it.size - 1)
                        } else {
                            it
                        }
                    }
            }


            return selectedRangesInDoc.map {
                val lineNumbers = editor.document.getLineNumber(it.first)..editor.document.getLineNumber(it.last)
                val fullTextRange =
                    editor.document.getLineStartOffset(lineNumbers.first) until editor.document.getLineEndOffset(
                        lineNumbers.last
                    )
                val fullText = editor.document.text.substring(fullTextRange).removeSuffix("\n")
                val selection = editor.document.text.substring(it).removeSuffix("\n")
                val textRangeWithCR = if (fullTextRange.last > editor.document.text.length) fullTextRange else IntRange(
                    fullTextRange.start,
                    fullTextRange.last + 1
                )
                val selectionRangeInLine = (it.start - textRangeWithCR.first)..(it.last - textRangeWithCR.first)
                SelectedLine(selectionRangeInLine, selection, textRangeWithCR, fullText)
            }
        }
    }
}