/* 
 * Copyright (C) 2016  Coved Oswald, Kaleb Luse, and Weston Miller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.riskybusiness.util;

import com.riskybusiness.genetic.Epoch;
import com.riskybusiness.util.PrinterHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.lang.ClassNotFoundException;
import java.lang.Object;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class GenomeGUI extends Object implements Serializable
{
	private static final long serialVersionUID = 5606245947756700803L;
	
	private final JFrame frame = new JFrame("W. A. N. T.");
	private final JTextPane console = new JTextPane();
	private final JButton start = new JButton("Start");
	private final JButton stop = new JButton("Stop");
	private final JMenuItem resume = new JMenuItem("Start");
	private final JMenuItem pause = new JMenuItem("Stop");
	private JMenuBar jmb;
	private JMenu file;
	private JMenu edit;
	private JMenu settings;
	private JMenu help;
	
	private final Epoch epoch = new Epoch(50, 30, 6, .3D, .15D, .02D);
	private final Thread t = new Thread(epoch);
	
	public GenomeGUI()
	{
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
		news.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(JOptionPane.showConfirmDialog(GenomeGUI.this.frame, "Are you sure you want to make a new population?",
					"Confirm New Population", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
				{
					GenomeGUI.this.console.setText("");
					GenomeGUI.this.stop.doClick(); //won't work if disabled anyway.
					GenomeGUI.this.start.doClick();
				}
			}
		});
		this.file.add(news);
		
		JMenuItem open = new JMenuItem("Open Population");
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		open.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				GenomeGUI.this.stop.doClick();
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Object Output Files", "txt", "gaif");
				chooser.setFileFilter(filter);
				GenomeGUI.this.stop.doClick();
				if(chooser.showOpenDialog(GenomeGUI.this.frame) == JFileChooser.APPROVE_OPTION)
				{
					//GenomeGUI.this.print("You have chosen to open this file: " + chooser.getSelectedFile().getName());
					//load GenomeFile
					try
					{
						ObjectInputStream oos = new ObjectInputStream(new FileInputStream(chooser.getSelectedFile()));
						GenomeGUI.this.epoch.mutateFromOther((Epoch)oos.readObject());
						oos.close();
						GenomeGUI.this.printSucc("Loaded " + chooser.getSelectedFile().getName() + " successfully!");
					}
					catch(IOException io)
					{
						GenomeGUI.this.printErr("Could not load file: " + io.getMessage());
					}
					catch(ClassNotFoundException cnfe)
					{
						GenomeGUI.this.printErr("Class not found: " + cnfe.getMessage());
					}
				}
			}
		});
		this.file.add(open);
		
		final JMenuItem save = new JMenuItem("Save Population"); //you'll see why it's final
		save.setMnemonic(KeyEvent.VK_A);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		save.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//save File
			}
		});
		this.file.add(save);
		
		JMenuItem saveAs = new JMenuItem("Save Population As");
		saveAs.setAccelerator(KeyStroke.getKeyStroke("control alt S"));
		saveAs.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				GenomeGUI.this.stop.doClick();
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Object Output Files", "txt", "gaif");
				chooser.setFileFilter(filter);
				if(chooser.showSaveDialog(GenomeGUI.this.frame) == JFileChooser.APPROVE_OPTION)
				{
					GenomeGUI.this.print("You have chosen to save this file as " + chooser.getSelectedFile().getName());
					save.doClick();
				}
			}
		});
		this.file.add(saveAs);
		
		JMenuItem print = new JMenuItem("Print Console");
		print.setMnemonic(KeyEvent.VK_P);
		print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
		print.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					PrinterHelper ph = new PrinterHelper(GenomeGUI.this.frame);
					ph.print(GenomeGUI.this.console.getText(), 1);
				}
				catch(Exception f)
				{
					GenomeGUI.this.printErr("Cannot print: " + f.getMessage());
				}
			}
		});
		this.file.add(print);
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
		exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		this.file.add(exit);
		
		this.jmb.add(file);
		
		//edit menu
		this.edit = new JMenu("Edit");
		this.edit.setMnemonic(KeyEvent.VK_E);
		
		pause.addActionListener(new StopAction());
		pause.setEnabled(false);
		this.edit.add(pause);
		
		this.resume.addActionListener(new StartAction());
		this.edit.add(resume);
		
		JMenuItem copy = new JMenuItem("Copy To Clipboard");
		copy.setMnemonic(KeyEvent.VK_C);
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK));
		copy.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				StringSelection ss = new StringSelection(console.getText());
				Clipboard clp = Toolkit.getDefaultToolkit().getSystemClipboard();
				clp.setContents(ss, null);
			}
		});
		this.edit.add(copy);
		
		this.jmb.add(edit);
		
		//settings menu
		this.settings = new JMenu("Settings");
		this.settings.setMnemonic(KeyEvent.VK_S);
		
		JMenuItem params = new JMenuItem("Load Parameter File");
		params.setMnemonic(KeyEvent.VK_L);
		params.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
		params.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Object Output Files", "txt", "gapf");
				chooser.setFileFilter(filter);
				GenomeGUI.this.stop.doClick();
				if(chooser.showOpenDialog(GenomeGUI.this.frame) == JFileChooser.APPROVE_OPTION)
				{
					GenomeGUI.this.print("You have chosen to load the param file " + chooser.getSelectedFile().getName());
					//load params
				}
			}
		});
		this.settings.add(params);
		
		this.jmb.add(settings);
		
		//help menu
		this.help = new JMenu("Help");
		this.help.setMnemonic(KeyEvent.VK_H);
		
		JMenuItem helpI = new JMenuItem("Help");
		helpI.setAccelerator(KeyStroke.getKeyStroke("F1"));
		helpI.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JPopupMenu jpm = new JPopupMenu("Help");
				jpm.setPopupSize(new Dimension(200, 124));
				jpm.add("This is the help menu. Have fun.");
				jpm.setLightWeightPopupEnabled(false);
				jpm.setBorderPainted(true);
				jpm.show(GenomeGUI.this.frame, GenomeGUI.this.frame.getWidth() / 2 - 100, GenomeGUI.this.frame.getHeight() / 2 - 62);
			}
		});
		this.help.add(helpI);
		
		//add and finish up menu bar
		this.jmb.add(help);
		this.frame.setJMenuBar(this.jmb);
		
		//Console area
		this.console.setEditable(false);
		JScrollPane jsp = new JScrollPane(this.console);
		jsp.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
		{
			public void adjustmentValueChanged(AdjustmentEvent e)
			{
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});
		this.frame.add(jsp, BorderLayout.CENTER);
		
		//buttons
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.stop.addActionListener(new StopAction());
		this.start.addActionListener(new StartAction());
		this.stop.setEnabled(false);
		jp.add(stop);
		jp.add(start);
		this.frame.add(jp, BorderLayout.SOUTH);
		
	}
	
	public void start()
	{
		this.frame.setVisible(true);
		epoch.createPopulation(.5D, true);
	}
	
	public void printErr(String msg)
	{
		this.append(msg, Color.RED);
	}
	
	public void print(String msg)
	{
		this.append(msg, Color.BLACK);
	}
	
	public void printSucc(String msg)
	{
		this.append(msg, Color.GREEN);
	}
	
	private void append(String msg, Color c)
	{
		this.console.setEditable(true);
		if(msg.length() >= 3)
			msg = "> " + msg;
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet set = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		
		set = sc.addAttribute(set, StyleConstants.FontFamily, "Lucida Console");
		set = sc.addAttribute(set, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		
		int len = this.console.getDocument().getLength();
		this.console.setCaretPosition(len);
		this.console.setCharacterAttributes(set, false);
		this.console.replaceSelection(msg);
		this.console.setEditable(false);
	}
	
	private class StartAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			GenomeGUI.this.start.setEnabled(false);
			GenomeGUI.this.resume.setEnabled(false);
			GenomeGUI.this.stop.setEnabled(true);
			GenomeGUI.this.pause.setEnabled(true);
			
			if(GenomeGUI.this.epoch.isRunning())
				GenomeGUI.this.epoch.switchPausedState();
			else
				GenomeGUI.this.t.start();
		}
	}
	
	private class StopAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			GenomeGUI.this.start.setEnabled(true);
			GenomeGUI.this.resume.setEnabled(true);
			GenomeGUI.this.stop.setEnabled(false);
			GenomeGUI.this.pause.setEnabled(false);
			//Do the rest
			
			epoch.switchPausedState();
		}
	}
}