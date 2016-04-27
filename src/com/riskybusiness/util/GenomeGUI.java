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
import java.awt.event.KeyEvent;

import java.io.Serializable;

import java.lang.Object;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

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
	
	public GenomeGUI()
	{
		console.setText("none");
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
		this.console.setEditable(false);
		this.frame.add(console, BorderLayout.CENTER);
		
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
		msg += "\n";
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
	
	public static void main(String... args)
	{
		GenomeGUI ggui = new GenomeGUI();
		ggui.start();
	}
	
	private class StartAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			GenomeGUI.this.start.setEnabled(false);
			GenomeGUI.this.resume.setEnabled(false);
			GenomeGUI.this.stop.setEnabled(true);
			GenomeGUI.this.pause.setEnabled(true);
			//Do the rest
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
		}
	}
}