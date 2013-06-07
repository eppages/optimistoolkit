/*
 Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis.sd.schemas.st;


/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */

public class NormalStatus extends Status
{
	private String component;
	private String operation;
	private boolean operationDone;
	private int componentProgress; // <0-100>
	private boolean _isRoot = false;
	
	
	/**
	 * Create a NormalStatus Object, _component is not a root component.
	 * @param _component
	 * @param _operation
	 * @param _operationDone
	 * @param _componentProgress
	 */
	public NormalStatus(String _component, String _operation,
			boolean _operationDone, int _componentProgress)
	{
		super(Status.NORMAL);
		this.component = _component;
		this.operation = _operation;
		this.operationDone = _operationDone;
		this.componentProgress = _componentProgress;
	}
	
	/**
	 * @param _component
	 * @param _operation
	 * @param _operationDone
	 * @param _componentProgress
	 * @param isRoot true if _component is a root component
	 */
	public NormalStatus(String _component, String _operation,
			boolean _operationDone, int _componentProgress, boolean isRoot)
	{
		super(Status.NORMAL);
		this.component = _component;
		this.operation = _operation;
		this.operationDone = _operationDone;
		this.componentProgress = _componentProgress;
		this._isRoot = isRoot;
	}

	public String getComponent()
	{
		return component;
	}

	public void setComponent(String component)
	{
		this.component = component;
	}

	public String getOperation()
	{
		return operation;
	}

	public void setOperation(String operation)
	{
		this.operation = operation;
	}


	public boolean isOperationDone()
	{
		return operationDone;
	}

	public void setOperationDone(boolean operationDone)
	{
		this.operationDone = operationDone;
	}

	public int getComponentProgress()
	{
		return componentProgress;
	}

	public void setComponentProgress(int componentProgress)
	{
		this.componentProgress = componentProgress;
	}
	public boolean isRoot()
	{
		return this._isRoot;
	}
}
