package com.example.hellosugar;

import com.lidroid.xutils.db.annotation.Id;

public class Profile
{
	@Id
    public int ID;

    public String username ;

    public int QID ;

    public String title ;

    public int right ;
    
    public int wrong ;
    
    public String toString()
    {
    	return "[" + this.right + "/" + this.wrong + "]"+  this.title ;
    }
}