package main;

import javax.swing.UIManager;

import main.gui.GuiSolution;

public class Main {

	public static void main(final String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
		}

		final GuiSolution sol = new GuiSolution();
		sol.setVisible(true);
	}

}
