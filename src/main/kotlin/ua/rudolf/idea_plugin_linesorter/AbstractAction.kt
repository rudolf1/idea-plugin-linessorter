package ua.rudolf.idea_plugin_linesorter

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler
import com.intellij.openapi.editor.actions.TextComponentEditorAction
import java.util.*
import java.util.function.Function

open class AbstractAction(f: (List<String>) -> List<String>) : TextComponentEditorAction(Handler(f)) {

    private class Handler(private val f: (List<String>) -> List<String>) : EditorWriteActionHandler() {
        override fun executeWriteAction(editor: Editor, dataContext: DataContext) {
            val doc = editor.document
            val (lines, start, end) =
                if (editor.selectionModel.hasSelection()) {
                    Triple(
                        (editor.selectionModel.selectedText ?: "").split("\n"),
                        editor.selectionModel.selectionStart,
                        editor.selectionModel.selectionEnd
                    )
                } else {
                    val text = doc.text.split("\n")
                    if (text.last() == "")
                        Triple(
                            text.subList(0, text.size - 1),
                            0,
                            doc.textLength - 1
                        )
                    else
                        Triple(
                            text,
                            0,
                            doc.textLength
                        )
                }
            val processedLines = f(lines)
            editor.document.replaceString(start, end, processedLines.joinToString("\n"))
        }

        private fun ignoreLastEmptyLines(doc: Document, endLine: Int): Int {
            var endLine = endLine
            while (endLine >= 0) {
                if (doc.getLineEndOffset(endLine) > doc.getLineStartOffset(endLine)) {
                    return endLine
                }
                endLine--
            }
            return -1
        }

        private fun extractLines(doc: Document, startLine: Int, endLine: Int): List<String> {
            val lines: MutableList<String> =
                ArrayList(endLine - startLine)
            for (i in startLine..endLine) {
                val line = extractLine(doc, i)
                lines.add(line)
            }
            return lines
        }

        private fun extractLine(doc: Document, lineNumber: Int): String {
            val lineSeparatorLength = doc.getLineSeparatorLength(lineNumber)
            val startOffset = doc.getLineStartOffset(lineNumber)
            val endOffset = doc.getLineEndOffset(lineNumber) + lineSeparatorLength
            var line = doc.charsSequence.subSequence(startOffset, endOffset).toString()
            // If last line has no \n, add it one
// This causes adding a \n at the end of file when sort is applied on whole file and the file does not end
// with \n... This is fixed after.
            if (lineSeparatorLength == 0) {
                line += "\n"
            }
            return line
        }

        private fun joinLines(lines: List<String>): StringBuilder {
            val builder = StringBuilder()
            for (line in lines) {
                builder.append(line)
            }
            return builder
        }

    }
}