package eu.optimis.manifestregistry.exceptions;

public class ResourceInvalid extends Exception 
{
    public ResourceInvalid()
    {
    }
    
    public ResourceInvalid(String message)
    {
        super(message);
    }
    
    public ResourceInvalid(String message, Throwable cause)
    {
		super(message, cause);
	}

	public ResourceInvalid(Throwable cause)
	{
		super(cause);
	}

}
