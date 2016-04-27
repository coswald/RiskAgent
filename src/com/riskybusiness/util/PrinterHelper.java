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

import java.awt.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.lang.InterruptedException;

import java.nio.charset.StandardCharsets;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;

import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import javax.swing.JOptionPane;

public final class PrinterHelper extends PrintJobAdapter
{
	private boolean done = false;
	private Component parent;
	
	public PrinterHelper(Component parent)
	{
		this.parent = parent;
	}
	
	public void printJobCanceled(PrintJobEvent pje)
	{
		this.finishPrinting(pje);
	}
	
	public void printJobCompleted(PrintJobEvent pje)
	{
		this.finishPrinting(pje);
	}
	
	public void printJobFailed(PrintJobEvent pje)
	{
		this.finishPrinting(pje);
	}
	
	public void printJobNoMoreEvents(PrintJobEvent pje)
	{
		this.finishPrinting(pje);
	}
	
	private void finishPrinting(PrintJobEvent pje)
	{
		synchronized(PrinterHelper.this)
		{
			this.done = true;
			switch(pje.getPrintEventType())
			{
				case PrintJobEvent.JOB_CANCELED:
					JOptionPane.showMessageDialog(this.parent, "The job has been cancled!", "Error: Cancled!", JOptionPane.ERROR_MESSAGE);
					break;
				case PrintJobEvent.JOB_COMPLETE:
					JOptionPane.showMessageDialog(this.parent, "The job has been completed.", "Success!", JOptionPane.OK_OPTION);
					break;
				case PrintJobEvent.JOB_FAILED:
					JOptionPane.showMessageDialog(this.parent, "The job has failed to print!", "Erro: Failure!", JOptionPane.ERROR_MESSAGE);
					break;
				case PrintJobEvent.NO_MORE_EVENTS:
				default:
					JOptionPane.showMessageDialog(this.parent, "Cannot give more information.", "No more Events!", JOptionPane.OK_OPTION);
					break;
			}
			this.notify();
		}
	}
	
	public synchronized void waitForDone()
	{
		try
		{
			while(!done)
				this.wait();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			System.err.println(e);
			System.exit(1);
		}
	}
	
	public void print(File fileName, int numCopies) throws PrintException, IOException
	{
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		FileInputStream in = new FileInputStream(fileName);
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(numCopies));
		
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc doc = new SimpleDoc(in, flavor, null);
		
		DocPrintJob job = service.createPrintJob();
		job.addPrintJobListener(this);
		job.print(doc, pras);
		this.waitForDone();
		in.close();
		
		//ejects the page
		InputStream ff = new ByteArrayInputStream("\f".getBytes());
		Doc docff = new SimpleDoc(ff, flavor, null);
		DocPrintJob jobff = service.createPrintJob();
		jobff.print(docff, null);
		this.waitForDone();
	}
	
	public void print(String toPrint, int numCopies) throws PrintException, IOException
	{
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		InputStream in = new ByteArrayInputStream(toPrint.getBytes(StandardCharsets.UTF_8));
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(numCopies));
		
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc doc = new SimpleDoc(in, flavor, null);
		
		DocPrintJob job = service.createPrintJob();
		job.addPrintJobListener(this);
		job.print(doc, pras);
		this.waitForDone();
		in.close();
		
		//ejects the page
		InputStream ff = new ByteArrayInputStream("\f".getBytes());
		Doc docff = new SimpleDoc(ff, flavor, null);
		DocPrintJob jobff = service.createPrintJob();
		jobff.print(docff, null);
		this.waitForDone();
	}
}