package eu.optimis.trustedinstance.exceptions;

public class ResourceTypeUnknown extends Exception 
{
    public ResourceTypeUnknown() 
    {
        super();
    }

    public ResourceTypeUnknown(String message)
    {
        super(message);
    }
    
    public ResourceTypeUnknown(String message, Throwable cause)
    {
		super(message, cause);
	}

	public ResourceTypeUnknown(Throwable cause)
	{
		super(cause);
	}

}
