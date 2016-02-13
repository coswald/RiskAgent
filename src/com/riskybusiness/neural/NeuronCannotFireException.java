import java.lang.Exception;

public class NeuronCannotFireException extends Exception
{
	private static final long serialVersionUID = 8703764965465370861L;
	
	public NeuronCannotFireException()
	{
		super();
	}
	
	public NeuronCannotFireException(String message)
	{
		super(message);
	}
}