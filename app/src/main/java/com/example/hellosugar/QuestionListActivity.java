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

public class QuestionListActivity extends ListActivity 
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.questionlistlayout);
		
		DbUtils db = DbUtils.create(this);
		
		try 
		{
			List<DbModel> titles = db.findDbModelAll(Selector.from(Question.class).select("title"));
			List<String> data = new ArrayList<String>();
			for(DbModel m : titles)
			{
				data.add(m.getString("title"));
			}
			this.setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, data)); 
		} 
		catch (DbException e) 
		{
			e.printStackTrace();
		}
		
	}

}
