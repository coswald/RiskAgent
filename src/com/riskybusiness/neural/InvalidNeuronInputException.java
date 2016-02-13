import java.lang.ArrayIndexOutOfBoundsException;

public class InvalidNeuronInputException extends ArrayIndexOutOfBoundsException
{
	private static final long serialVersionUID = 7703107280930494895L;
	
	public InvalidNeuronInputException()
	{
		super();
	}
	
	public InvalidNeuronInputException(String message)
	{
		super(message);
	}
}