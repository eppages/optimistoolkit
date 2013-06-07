package eu.optimis.manifestregistry.exceptions;

public class ResourceNotFound extends Exception 
{
    public ResourceNotFound()
    {
        super();
    }
    
    public ResourceNotFound(String message)
    {
        super(message);
    }
    
    public ResourceNotFound(String message, Throwable cause)
    {
		super(message, cause);
	}

	public ResourceNotFound(Throwable cause)
	{
		super(cause);
	}
}
