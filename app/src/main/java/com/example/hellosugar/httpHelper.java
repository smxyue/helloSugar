package com.example.hellosugar;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class httpHelper 
{
	private Context ctx = null;
	public httpHelper(Context _ctx)
	{
		ctx = _ctx;
	}
	public void getNext(int id)
	{
		final String BASE_URL = "http://nctest.sudisoft.com/Questions/jsonNext/";
		String url = BASE_URL + id;
				
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() 
		{
			@Override
			public void onLoading(long total, long current, boolean isUploading) 
			{}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) 
			{
				DbUtils db = DbUtils.create(ctx);
				StringBuilder sb = new StringBuilder();
				
				Question q = new Question();
				try
				{
					JSONObject obj = new JSONObject(responseInfo.result);
					q.ID = obj.getInt("ID");
					q.subject = obj.getString("subject");
					q.catalog = obj.getString("catalog");
					q.tip = obj.getString("tip");
					q.title = obj.getString("title");
					if (db.findById(Question.class, q.ID) ==null)
					{
						db.save(q);
					}
					else
						db.update(q);
					sb.append("获取到试题:" + q.ID + " " + q.title);
					JSONArray array = obj.getJSONArray("choices");
					for (int i=0; i <array.length(); i++)
					{
						JSONObject objc = (JSONObject) array.get(i);
						Choice c = new Choice();
						c.ID = objc.getInt("ID");
						c.QID = objc.getInt("QID");
						c.title = objc.getString("title");
						c.correct = objc.getBoolean("correct");
						if (db.findById(Choice.class, c.ID) == null)
						{
							db.save(c);
						}
						else
						{
							db.update(c);
						}
						sb.append("\n" + c.title);
						if (c.correct)
							sb.append("√");
					}
				}
				catch(Exception e)
				{
					Log.v("xxxxxx","Json Error:" + e.getMessage());
				}
				Toast.makeText(ctx, sb.toString(), Toast.LENGTH_LONG).show();
				db.close();
			}

			@Override
			public void onStart() 
			{}

			@Override
			public void onFailure(HttpException error, String msg) 
			{
				Log.v("xxxxxx", "http utils is failed:" + msg);
			}
		});
	}
}
