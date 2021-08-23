package main.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.json.JsonSolution;
import main.solution.Solution;
import main.solution.SolutionFile;

public class GuiSolution {

	private JFrame frame;
	private GuiSolutionFileTableModel tableModle;
	private String solutionName;

	public GuiSolution() {
		this(new Dimension(800, 600));
	}

	public GuiSolution(final Dimension frameSize) {
		this.initializeFrame(frameSize);
		this.initializeMenuBar();
		this.initializeTable();
		this.createNewSolution();
	}

	public void setVisible(final boolean visible) {
		this.frame.setVisible(visible);
	}

	public void createNewSolution() {
		this.tableModle.clearSolutionFiles();
		this.solutionName = "New Solution";
		this.frame.setTitle(this.solutionName);
	}

	public void loadNewSolution() {
		if (!GuiSolution.this.tableModle.isEmpty()) {
			final int result = JOptionPane.showConfirmDialog(null, "Discard unsaved progress?", "Discard Confirmation",
					JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
			if (result != JOptionPane.YES_OPTION) {
				return;
			}
		}

		final JFileChooser chooser = new JFileChooser();
		chooser.setDoubleBuffered(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle("Select solution file");
		chooser.setFileFilter(new FileNameExtensionFilter("Solution file (.json)", "json"));
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(GuiSolution.this.frame) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file;
		Solution solution;
		try {
			file = chooser.getSelectedFile();
			solution = new Solution(JsonSolution.loadFromFile(file),
					file.getAbsoluteFile().getAbsolutePath().toString());
		} catch (final IOException e1) {
			JOptionPane.showConfirmDialog(null, "Failed to load Solution", "Solution load error message",
					JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		this.tableModle.setSolutionFiles(solution.Files);
		this.solutionName = Objects.toString(solution.SolutionName, "");
		this.frame.setTitle(this.solutionName);
	}

	private void initializeFrame(final Dimension frameSize) {
		this.frame = new JFrame();
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.frame.setBounds((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2,
				frameSize.width, frameSize.height);
	}

	private void initializeMenuBar() {
		final JMenuBar menuBar = new JMenuBar();

		final JMenu fileMenu = new JMenu("File");
		final JMenuItem openButton = new JMenuItem("Open");
		final JMenuItem saveButton = new JMenuItem("Save");
		final JMenuItem exitButton = new JMenuItem("Exit");

		openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				GuiSolution.this.loadNewSolution();
			}
		});

		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser chooser = new JFileChooser();
				chooser.setDoubleBuffered(true);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileFilter(new FileNameExtensionFilter("Solution file (.json)", "json"));
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setDialogTitle("Safe solution to file");
				if (chooser.showSaveDialog(GuiSolution.this.frame) == JFileChooser.APPROVE_OPTION) {
					GuiSolution.this.tableModle.saveSolutionToFile(chooser.getSelectedFile(),
							GuiSolution.this.solutionName);
					GuiSolution.this.frame.setTitle(chooser.getSelectedFile().getName());
				}
			}
		});

		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				System.exit(0);
			}
		});

		fileMenu.add(openButton);
		fileMenu.add(saveButton);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitButton);
		menuBar.add(fileMenu);
		this.frame.setJMenuBar(menuBar);
	}

	private void initializeTable() {
		this.tableModle = new GuiSolutionFileTableModel();
		final JTable tabel = new JTable(this.tableModle);
		tabel.setFillsViewportHeight(true);
		tabel.addMouseListener(this.getMouseReleaseEvent());
		tabel.setDoubleBuffered(true);

		final JScrollPane scrollPane = new JScrollPane(tabel);
		this.frame.add(scrollPane);
	}

	private MouseAdapter getMouseReleaseEvent() {
		return new MouseAdapter() {

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (!SwingUtilities.isRightMouseButton(e) || !(e.getSource() instanceof JTable)) {
					return;
				}

				final JTable source = (JTable) e.getSource();

				final int clickedRow = source.rowAtPoint(e.getPoint());
				final int clickedColumn = source.columnAtPoint(e.getPoint());

				final JMenuItem addNewItem = new JMenuItem("Add");
				addNewItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						final JFileChooser chooser = new JFileChooser();
						chooser.setDoubleBuffered(true);
						chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						chooser.setMultiSelectionEnabled(false);
						chooser.setDialogTitle("Select solution file");
						if (chooser.showOpenDialog(GuiSolution.this.frame) == JFileChooser.APPROVE_OPTION) {
							final SolutionFile next = new SolutionFile(chooser.getSelectedFile(), null, false);
							final int index = GuiSolution.this.tableModle.findSolutionFile(next);
							if (index >= 0) {
								source.changeSelection(index, 0, false, false);
							} else {
								GuiSolution.this.tableModle.addUniqueSolutionFile(next);
							}
						}
					}
				});

				final JPopupMenu popupMenu = new JPopupMenu();

				if (clickedRow < 0 || clickedColumn < 0) {
					popupMenu.add(addNewItem);
				} else {
					if (!source.isRowSelected(clickedRow)) {
						source.changeSelection(clickedRow, clickedColumn, false, false);
					}

					final JMenuItem openItem = new JMenuItem("Edit");
					final JMenuItem deleteItem = new JMenuItem("Delete");

					openItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {
							final SolutionFile row = GuiSolution.this.tableModle.getRowAt(clickedRow);
							try {
								Desktop.getDesktop().open(row.FileName);
							} catch (final IOException e1) {
							}
						}
					});

					deleteItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {
							final int result = JOptionPane.showConfirmDialog(null,
									"Do you really want to delete the selected data?", "Delete Confirmation",
									JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
							if (result == JOptionPane.YES_OPTION) {
								final JPopupMenu popupMenu = (JPopupMenu) ((JMenuItem) e.getSource()).getParent();
								final JTable table = (JTable) (popupMenu.getInvoker());
								GuiSolution.this.tableModle.removeRows(table.getSelectedRows());
							}
						}
					});

					popupMenu.add(openItem);
					popupMenu.add(new JSeparator());
					popupMenu.add(addNewItem);
					popupMenu.add(deleteItem);
				}

				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		};
	}

}
