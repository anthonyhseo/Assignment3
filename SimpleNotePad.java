import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;

/*
 * Code Smell Refactoring:
 * -Changed the variables to private instead of public
 * -Changed variable names at the beginning of class to make it more readable
 * -Line 145: Removed undo feature since it is not in the design specs.
 * -Split the file and edit menu items to separate methods to make it more
 * 	readable.
 * -Removed the println in the paste block. 
 * -Moved some of the code from the if-else block in the actionPerformed method
 *  to separate methods to make it more readable.
 * 
 * -Added new features in the assignment spec
 * 
 */

public class SimpleNotePad extends JFrame implements ActionListener{
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, openRecentFile;
	private JTextPane d = new JTextPane();
	private JMenuItem newFile, saveFile, printFile, openFile;
	private JMenuItem copy, paste, simpleReplace;
	private JMenuItem[] recent = new JMenuItem[5];
	private File[] recentFile = {null, null, null, null, null};
	
    public SimpleNotePad() {
        setTitle("A Simple Notepad Tool");
        menuBar = new JMenuBar();
        initFileMenu();
        initEditMenu();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
        add(new JScrollPane(d));
        setPreferredSize(new Dimension(600,600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }
    
    private void initFileMenu() {
    	fileMenu = new JMenu("File");
    	openRecentFile = new JMenu("Open Recent File");
    	
    	openFile = new JMenuItem("Open File");
    	newFile = new JMenuItem("New File");
    	saveFile = new JMenuItem("Save File");
    	printFile = new JMenuItem("Print File");
    	
    	fileMenu.add(openFile);
    	fileMenu.addSeparator();
    	fileMenu.add(newFile);
        fileMenu.addSeparator();
        fileMenu.add(saveFile);
        fileMenu.addSeparator();
        fileMenu.add(printFile);
        fileMenu.addSeparator();
        fileMenu.add(openRecentFile);
    	
        openFile.addActionListener(this);
        openFile.setActionCommand("open");
        newFile.addActionListener(this);
        newFile.setActionCommand("new");
        saveFile.addActionListener(this);
        saveFile.setActionCommand("save");
        printFile.addActionListener(this);
        printFile.setActionCommand("print");
        openRecentFile.addActionListener(this);
        openRecentFile.setActionCommand("open_recent");
    }
    
    private void initEditMenu() {
    	editMenu = new JMenu("Edit");
    	simpleReplace = new JMenuItem("Replace");
    	copy = new JMenuItem("Copy");
    	paste = new JMenuItem("Paste");
    	
    	editMenu.add(simpleReplace);
    	editMenu.add(copy);
    	editMenu.add(paste);
    	
    	simpleReplace.addActionListener(this);
    	simpleReplace.setActionCommand("simple_replace");
    	copy.addActionListener(this);
    	copy.setActionCommand("copy");
    	paste.addActionListener(this);
    	paste.setActionCommand("paste");
    	
    }
    
    private void addFileToRecent(File file) {
    	openRecentFile.removeAll();
    	for (int i = 4; i > 0; i--) {
    		recentFile[i] = recentFile[i - 1];
    	}
    	recentFile[0] = file;
    	for (int i = 0; i < 5; i++) {
    		if (recentFile[i] == null) break;
    		recent[i] = new JMenuItem(recentFile[i].toString());
    		openRecentFile.add(recent[i]);
    		recent[i].addActionListener(this);
    		recent[i].setActionCommand("recent" + i);
    	}
    }
    
    private void openFile(File fileToOpen) {
		System.out.println(fileToOpen.toString());
		addFileToRecent(fileToOpen);
		try {
			StringBuilder sb = new StringBuilder();
			Scanner openScanner = new Scanner(fileToOpen);
			while (openScanner.hasNextLine()) {
				sb.append(openScanner.nextLine());
			}
			System.out.println("string: " + sb.toString());
			d.setText(sb.toString());
			openScanner.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    }
    
    
    // brought the print function out of the actionPerformed to make it more readable.
    private void printFunction() {
    	try {
            PrinterJob pjob = PrinterJob.getPrinterJob();
            pjob.setJobName("Sample Command Pattern");
            pjob.setCopies(1);
            pjob.setPrintable(new Printable() {
                public int print(Graphics pg, PageFormat pf, int pageNum) {
                    if (pageNum>0)
                        return Printable.NO_SUCH_PAGE;
                    pg.drawString(d.getText(), 500, 500);
                    paint(pg);
                    return Printable.PAGE_EXISTS;
                }
            });

            if (pjob.printDialog() == false)
                return;
            pjob.print();
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(null,
                    "Printer error" + pe, "Printing error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveFunction() {
    	File fileToWrite = null;
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	fileToWrite = fc.getSelectedFile();
            try {
                PrintWriter out = new PrintWriter(new FileWriter(fileToWrite));
                out.println(d.getText());
                JOptionPane.showMessageDialog(null, "File is saved successfully...");
                out.close();
            } catch (IOException ex) {
            }
        }
    }
    
    public static void main(String[] args) {
        SimpleNotePad app = new SimpleNotePad();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	String actionCommand = e.getActionCommand();
        if (actionCommand.equals("open")) {
        	File fileToOpen = null;
        	JFileChooser fileChooser = new JFileChooser();
        	int returnVal = fileChooser.showOpenDialog(null);
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
        		openFile(fileChooser.getSelectedFile());
        	}
        } else if (actionCommand.equals("new")) {
            d.setText("");
        } else if (actionCommand.equals("save")) {
            saveFunction();
        } else if (actionCommand.equals("print")) {
            printFunction();
        } else if (actionCommand.equals("recent0")) {
        	openFile(recentFile[0]);
        } else if (actionCommand.equals("recent1")) {
        	openFile(recentFile[1]);
    	} else if (actionCommand.equals("recent2")) {
        	openFile(recentFile[2]);
    	} else if (actionCommand.equals("recent3")) {
        	openFile(recentFile[3]);
    	} else if (actionCommand.equals("recent4")) {
        	openFile(recentFile[4]);
    	} else if (actionCommand.equals("copy")) {
            d.copy();
        } else if (actionCommand.equals("paste")) {
            StyledDocument doc = d.getStyledDocument();
            Position position = doc.getEndPosition();
            d.paste();
        } else if (actionCommand.equals("simple_replace")) {
        	String replace = JOptionPane.showInputDialog(null, "Replace or insert with:", "Input", JOptionPane.INFORMATION_MESSAGE);
        	d.replaceSelection(replace);
        }
    }
}