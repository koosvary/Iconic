package org.iconic.project;

import javafx.collections.ObservableList;
import javafx.scene.control.TablePosition;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class Spreadsheet extends SpreadsheetView {
    @Override
    public void copyClipboard(){
        super.copyClipboard();
        copySelectionToClipboard();
    }

    private void copySelectionToClipboard() {

        StringBuilder clipboardString = new StringBuilder();

        ObservableList<TablePosition> positionList = this.getSelectionModel().getSelectedCells();

        int prevRow = -1;

        for (TablePosition position : positionList) {

            int row = position.getRow();
            int col = position.getColumn();

            // determine whether we advance in a row (tab) or a column
            // (newline).
            if (prevRow == row) {
                clipboardString.append('\t');
            }
            else if (prevRow != -1) {
                clipboardString.append('\n');
            }

            Object observableValue = this.getGrid().getRows().get(row).get(col).getItem();

            // add new item to clipboard
            clipboardString.append(String.valueOf(observableValue));

            // remember previous
            prevRow = row;
        }

        // create clipboard content
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(clipboardString.toString());

        // set clipboard content
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
}
