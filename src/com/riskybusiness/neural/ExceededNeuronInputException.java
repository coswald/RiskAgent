import java.lang.Exception;

public class ExceededNeuronInputException extends Exception
{
	private static final long serialVersionUID = 6796323911306049985L;
	
	public ExceededNeuronInputException()
	{
		super();
	}
	
	public ExceededNeuronInputException(String message)
	{
		super(message);
	}
}