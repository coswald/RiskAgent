package com.riskybusiness.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.Object;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class GenomeGUI extends Object
{
	private JFrame frame;
	private JTextArea console;
	private JButton start;
	private JButton stop;
	private JMenuBar jmb;
	private JMenu file;
	private JMenu edit;
	private JMenu settings;
	private JMenu help;
	
	public GenomeGUI()
	{
		this.frame = new JFrame("W. A. N. T.");
		
		this.init();
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void init()
	{
		this.frame.setLayout(new BorderLayout());
		this.frame.setSize(new Dimension(600, 371));
		
		this.jmb = new JMenuBar();
		
		//file menu
		this.file = new JMenu("File");
		this.file.setMnemonic(KeyEvent.VK_F);
		JMenuItem news = new JMenuItem("New Population");
		news.setMnemonic(KeyEvent.VK_N);
		news.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		this.file.add(news);
		JMenuItem open = new JMenuItem("Open Population");
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		this.file.add(open);
		JMenuItem save = new JMenuItem("Save Population");
		save.setMnemonic(KeyEvent.VK_A);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		this.file.add(save);
		JMenuItem saveAs = new JMenuItem("Save Population As");
		saveAs.setAccelerator(KeyStroke.getKeyStroke("control alt S"));
		this.file.add(saveAs);
		JMenuItem print = new JMenuItem("Print Console");
		print.setMnemonic(KeyEvent.VK_P);
		print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
		this.file.add(print);
		JMenuItem exit = new JMenuItem("Exit");
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
		this.file.add(exit);
		
		this.jmb.add(file);
		
		//edit menu
		this.edit = new JMenu("Edit");
		this.edit.setMnemonic(KeyEvent.VK_E);
		JMenuItem pause = new JMenuItem("Pause");
		pause.setEnabled(false);
		this.edit.add(pause);
		JMenuItem resume = new JMenuItem("Start");
		this.edit.add(resume);
		JMenuItem copy = new JMenuItem("Copy To Clipboard");
		copy.setMnemonic(KeyEvent.VK_C);
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
		this.edit.add(copy);
		
		this.jmb.add(edit);
		
		//settings menu
		this.settings = new JMenu("Settings");
		this.settings.setMnemonic(KeyEvent.VK_S);
		JMenuItem params = new JMenuItem("Load Parameter File");
		params.setMnemonic(KeyEvent.VK_G);
		params.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
		this.settings.add(params);
		
		this.jmb.add(settings);
		
		//help menu
		this.help = new JMenu("Help");
		this.help.setMnemonic(KeyEvent.VK_H);
		JMenuItem helpI = new JMenuItem("Help");
		this.help.add(helpI);
		
		//add and finish up menu bar
		this.jmb.add(help);
		this.frame.setJMenuBar(this.jmb);
		
		//Console area
		this.console = new JTextArea();
		this.console.setEditable(false);
		this.frame.add(console, BorderLayout.CENTER);
		
		//buttons
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		this.start = new JButton("Start");
		this.stop = new JButton("Stop");
		jp.add(stop, BorderLayout.WEST);
		jp.add(start, BorderLayout.EAST);
		this.frame.add(jp, BorderLayout.SOUTH);
		
	}
	
	public void start()
	{
		this.frame.setVisible(true);
	}
	
	public static void main(String... args)
	{
		GenomeGUI ggui = new GenomeGUI();
		ggui.start();
	}
}