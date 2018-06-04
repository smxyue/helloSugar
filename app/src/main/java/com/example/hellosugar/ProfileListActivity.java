package com.example.hellosugar;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ProfileListActivity extends ListActivity 
{
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.profilelist);
		
		DbUtils db = DbUtils.create(this);
		
		try 
		{
			List<Profile> list = db.findAll(Selector.from(Profile.class).orderBy("wrong", true));
			List<String> data = new ArrayList<String>();
			for(Profile m : list)
			{
				data.add(m.toString());
			}
			this.setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, data)); 
		} 
		catch (DbException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
