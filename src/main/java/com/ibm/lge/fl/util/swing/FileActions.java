package com.ibm.lge.fl.util.swing;

import java.awt.Desktop;
import java.io.File;

import javax.swing.JOptionPane;

public class FileActions {

	public static void launchAction(File sourceFile, Desktop.Action action) {

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop() ;
			if (desktop.isSupported(action)) {
				try {
					FileActionLauncher fal = new FileActionLauncher(sourceFile, action, desktop) ;
					fal.start(); 				
				} catch (Exception localException) {
					JOptionPane.showMessageDialog(null, action + " action error on file " + sourceFile + ":\n" + localException,
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, action + " file action is not supported on this platform", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "File actions are not supported on this platform", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static class FileActionLauncher extends Thread {
		
		private File sourceFile ;
		private Desktop.Action action ;
		private Desktop desktop ;
		
		public FileActionLauncher(File s, Desktop.Action a, Desktop d) {
			super() ;
			
			sourceFile = s ;
			action = a ;
			desktop = d ;
		}
		
		public void run() {
			try {
				switch (action) {
				case OPEN: 
					desktop.open(sourceFile);
					break;
				case EDIT:
					desktop.edit(sourceFile);
					break;
				case PRINT:
					desktop.print(sourceFile);
					break;
				default:
					JOptionPane.showMessageDialog(null, action + " is not a file action", "Error",
							JOptionPane.ERROR_MESSAGE);

					break;
				}
			} catch (Exception localException) {
				JOptionPane.showMessageDialog(null,
						action + " action error on file " + sourceFile + ":\n" + localException, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
