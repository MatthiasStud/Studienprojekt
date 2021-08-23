package main.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import main.json.JsonSolution;
import main.json.JsonVerifiedFile;
import main.solution.SolutionFile;

public class GuiSolutionFileTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 9212261038614761890L;

	public static final String[] TABLE_COLUMNS = { "Name", "Author", "Verified" };
	private ArrayList<SolutionFile> solutionFiles = new ArrayList<>();

	public void clearSolutionFiles() {
		this.solutionFiles.clear();
		this.fireTableDataChanged();
	}

	public boolean isEmpty() {
		return this.solutionFiles.size() == 0;
	}

	public void setSolutionFiles(final SolutionFile[] solutionFiles) {
		this.solutionFiles = new ArrayList<>(Arrays.asList(solutionFiles));
		this.fireTableDataChanged();
	}

	public void setSolutionFiles(final ArrayList<SolutionFile> solutionFiles) {
		this.solutionFiles = new ArrayList<>(solutionFiles);
		this.fireTableDataChanged();
	}

	public boolean addUniqueSolutionFile(final SolutionFile solutionFile) {

		if (!this.solutionFiles.contains(solutionFile)) {
			this.solutionFiles.add(solutionFile);
			this.fireTableRowsInserted(this.solutionFiles.size() - 1, this.solutionFiles.size() - 1);
			return true;
		}
		return false;
	}

	public int findSolutionFile(final SolutionFile solutionFile) {
		return this.solutionFiles.indexOf(solutionFile);
	}

	public void removeRow(final int row) {
		try {
			this.solutionFiles.remove(row);
			this.fireTableRowsDeleted(row, row);
		} catch (final IndexOutOfBoundsException e) {
		}
	}

	public void removeRows(final int[] rows) {

		if (rows.length == 0) {
			return;
		}

		final List<Integer> list = Arrays.stream(rows).boxed().collect(Collectors.toList());
		list.sort(Collections.reverseOrder());

		for (final Integer nextIndex : list) {
			try {
				this.solutionFiles.remove((int) nextIndex);

			} catch (final IndexOutOfBoundsException e) {
				continue;
			}
		}
		this.fireTableDataChanged();
	}

	public SolutionFile getRowAt(final int rowIndex) {
		return this.solutionFiles.get(rowIndex);
	}

	public void saveSolutionToFile(final File selectedFile, final String solutionName) {

		final JsonSolution sol = new JsonSolution();
		sol.SolutionName = Objects.toString(solutionName, selectedFile.getName());
		sol.SolutionDirectory = selectedFile.getParent();
		sol.VerifiedFiles = new ArrayList<>();

		for (final SolutionFile file : this.solutionFiles) {
			if (file.Author != null && !file.Author.isEmpty()) {
				sol.VerifiedFiles.add(new JsonVerifiedFile(file));
			}
		}

		try {
			sol.saveToFile(selectedFile.getAbsolutePath().toString(), "\t");
		} catch (final IOException e) {
		}
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnIndex == 1 || columnIndex == 2;
	}

	@Override
	public int getColumnCount() {
		return GuiSolutionFileTableModel.TABLE_COLUMNS.length;
	}

	@Override
	public String getColumnName(final int column) {
		return GuiSolutionFileTableModel.TABLE_COLUMNS[column];
	}

	@Override
	public int getRowCount() {
		return this.solutionFiles.size();
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Objects.toString(this.solutionFiles.get(rowIndex).FileName, "");
		case 1:
			return Objects.toString(this.solutionFiles.get(rowIndex).Author, "");
		case 2:
			return this.solutionFiles.get(rowIndex).Verified;
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		switch (columnIndex) {
		case 2:
			return Boolean.class;
		}
		return String.class;
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		switch (columnIndex) {
		case 1:
			this.solutionFiles.get(rowIndex).Author = (String) aValue;
			break;
		case 2:
			this.solutionFiles.get(rowIndex).Verified = (Boolean) aValue;
			break;
		}
	}
}
