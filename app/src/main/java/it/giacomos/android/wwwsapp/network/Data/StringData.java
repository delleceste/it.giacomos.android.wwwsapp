package it.giacomos.android.wwwsapp.network.Data;

public class StringData 
{
	public boolean isValid()
	{
		return error == null || error.isEmpty();
	}
	
	public StringData(String str)
	{
		text = str;
		error = "";
		fromCache = false;
	}
	
	public StringData(String str, String err)
	{
		text = str;
		error = err;
		fromCache = false;
	}
	
	public boolean equals(StringData other)
	{
		return other != null && other.text.equals(this.text) && 
				other.error.equals(this.error) &&
				other.fromCache == this.fromCache;
	}
	
	public String text;
	public String error;
	public boolean fromCache;
}
