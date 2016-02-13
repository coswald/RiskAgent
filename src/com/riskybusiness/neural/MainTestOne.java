import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.InterruptedException;
import java.lang.Math;
import java.lang.Runnable;
import java.util.Random;
import java.util.ArrayList;

public class MainTestOne extends Applet implements Runnable
{
	private Random rand;
	private StepNeuron ptron;
	private Trainer[] training;
	private int count;
	private boolean running;
	private Thread runner;
	private ArrayList<Integer> toDraw;
	
	private float f(float x)
	{
		return 2 * x + 1;
	}
	
	public void init()
	{
		rand = new Random();
		training = new Trainer[2000];
		ptron = new StepNeuron(.5F, 2);
		count = 0;
		toDraw = new ArrayList<Integer>();
		running = false;
	}
	
	public void run()
	{
		while(running)
		{
			ptron.train(training[count].getAnswer(), training[count].getInputs());
			
			toDraw = new ArrayList<Integer>(3);
			toDraw.add(new Integer((int)training[count].getInputs()[0]));
			toDraw.add(new Integer((int)training[count].getInputs()[0]));
			toDraw.add(new Integer((ptron.fire(training[count].getInputs())
						> .5F) ? 0 : 1));
			
			count = (count + 1) % training.length;
			try
			{
				//this.repaint();
				this.repaint((toDraw.get(0)).intValue(), (toDraw.get(1)).intValue(), 5, 5)
				Thread.sleep(250);
			}
			catch(InterruptedException ie)
			{
				System.err.println(ie);
				System.exit(1);
			}
		}
	}
	
	public void start()
	{
		float x, y, answer;
		for(int i = 0; i < training.length; i++)
		{
			x = (rand.nextFloat() * rand.nextInt(this.getWidth())) - (float)this.getWidth() / 2.0F;
			y = (rand.nextFloat() * rand.nextInt(this.getHeight())) - (float)this.getHeight() / 2.0F;
			answer = (y < f(x)) ? 0F : 1F;
			training[i] = new Trainer(answer, x, y);
		}
		this.running = true;
		runner = new Thread(this);
		runner.start();
	}
	
	public void stop()
	{
		this.running = false;
		this.count = 0;
		ptron = new StepNeuron(.5F, 2);
		toDraw.clear();
	}
	
	public void destroy()
	{
	}
	
	public void paint(Graphics g)
	{
		g.translate(this.getWidth() / 2, this.getHeight() / 2);
		g.drawLine(-this.getWidth(), (int)f(-this.getWidth()), this.getWidth(), (int)f(this.getWidth()));
		
		//g.setColor(Color.GRAY);
		//g.drawLine(-this.getWidth(), (int)ptron.fire(-this.getWidth()), this.getWidth(), (int)ptron.fire(this.getWidth()));
		//g.setColor(Color.BLACK);
		
		if((toDraw.get(2)).intValue() == 0)
		    g.drawOval((toDraw.get(0)).intValue(), (toDraw.get(1)).intValue(), 5, 5);
		else
		    g.fillOval((toDraw.get(0)).intValue(), (toDraw.get(1)).intValue(), 5, 5);
	}
}
