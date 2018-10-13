package org.iconic.project;

import javafx.collections.ObservableList;
import javafx.scene.control.TablePosition;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import java.util.StringTokenizer;

public class Spreadsheet extends SpreadsheetView {
    @Override
    public void copyClipboard(){
        super.copyClipboard();
        copySelectionToClipboard();
    }

    @Override
    public void pasteClipboard(){
        super.pasteClipboard();
        pasteFromClipboard();

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

    private void pasteFromClipboard() {
        // abort if there's not cell selected to start with
        if(this.getSelectionModel().getSelectedCells().size() == 0) {
            return;
        }

        // get the cell position to start with
        TablePosition pasteCellPosition = this.getSelectionModel().getSelectedCells().get(0);

        String pasteString = Clipboard.getSystemClipboard().getString();

        int rowClipboard = -1;

        StringTokenizer rowTokenizer = new StringTokenizer( pasteString, "\n");
        while(rowTokenizer.hasMoreTokens()) {

            rowClipboard++;

            String rowString = rowTokenizer.nextToken();

            StringTokenizer columnTokenizer = new StringTokenizer(rowString, "\t");

            int colClipboard = -1;

            while(columnTokenizer.hasMoreTokens()) {

                colClipboard++;

                // get next cell data from clipboard
                String clipboardCellContent = columnTokenizer.nextToken();

                // calculate the position in the table cell
                int rowTable = pasteCellPosition.getRow() + rowClipboard;
                int colTable = pasteCellPosition.getColumn() + colClipboard;

                // skip if we reached the end of the table
                if(rowTable >= this.getItems().size()) {
                    continue;
                }
                if(colTable >= this.getColumns().size()) {
                    continue;
                }

                // get cell
                try{
                    double content = Double.parseDouble(clipboardCellContent);
                    this.getGrid().getRows().get(rowTable).get(colTable).setItem(String.valueOf(content));
                }catch (Exception ignored) {}
            }

        }

    }
}
