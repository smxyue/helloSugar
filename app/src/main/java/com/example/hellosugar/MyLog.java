package com.example.hellosugar;

import java.util.Date;

import com.lidroid.xutils.db.annotation.NoAutoIncrement;

public class MyLog  
{
	@NoAutoIncrement
	public int id;
	public Date logTime;
	
	public MyLog()
	{
		
	}
}
