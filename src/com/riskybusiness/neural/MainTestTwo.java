import java.lang.InterruptedException;
import java.lang.Math;
import java.lang.Object;
import java.util.Scanner;

public class MainTestTwo extends Object
{
	public static void main(String... args) throws InterruptedException
	{
		long start, end; // For timing
		StepNeuron ptron = new StepNeuron(.5F, 2); //Let's train a threshold neuron (.5 threshold) with two inputs to learn an AND gate.
		Trainer[] training = new Trainer[100]; //train with 100 points at a time
		Scanner z = new Scanner(System.in);
		
		int first, second;
		for(int i = 0 ; i < training.length; i++)
		{
			first = (Math.random() < .5D) ? 0b0 : 0b1;
			second = (Math.random() < .5D) ? 0b0 : 0b1;
			training[i] = new Trainer((float)(first & second), first, second);
		}
		
		start = System.currentTimeMillis();
		System.out.printf("Training to %d points...%n", training.length);
		boolean trained = false;
		int round = 0;
		int completed;
		while(!trained)
		{
			completed = 0;
			for(Trainer t : training)
				ptron.train(t.getAnswer(), t.getInputs());
			
			for(Trainer t : training)
				if(ptron.fire(t.getInputs()) == t.getAnswer())
					completed++;
			if(completed == training.length)
				trained = true;
			
			System.out.printf("Completed Round " + ++round + " with: %.2f  percent correct.\r", (100F * completed / (float)(training.length)));
			Thread.sleep(500);
		}
		end = System.currentTimeMillis();
		
		System.out.printf("Completed successful training in %d rounds with a time of %.3f seconds.\n", round, (float)((end - start) / 1000F));
		
		System.out.println("\nGive me two positive integers (preferably 0 and 1) that need to be analyzed. Enter a negative number to exit.");
		while(first >= 0 && second >= 0)
		{
			first = z.nextInt();
			second = z.nextInt();
			System.out.printf("The network says: %d.%n", (int)(ptron.fire(first, second)));
		}
	}
}