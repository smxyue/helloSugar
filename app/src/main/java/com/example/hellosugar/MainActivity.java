package com.example.hellosugar;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{
	TextView txt = null;
	private static int lastId = 0;
	DbUtils db = null;
	boolean isRun = false;
	Handler handler = new Handler();
	int currentQId = 0;
	List<Profile> errorList = null;
	int currentErrorIndex = 0;
	private String testSubject;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txt = (TextView)findViewById(R.id.textView1);
		db =  DbUtils.create(this);

		currentQId = ConfigHelper.getKeyInt(getApplicationContext(), "currentQId");
		testSubject = ConfigHelper.getKey(getApplicationContext(), "subject");

		if (testSubject.equals(""))
			testSubject = "农残大比武";


		ImageButton bt = (ImageButton)findViewById(R.id.imageButtonSequnceTest);
		bt.setOnClickListener(this);
		bt = (ImageButton)findViewById(R.id.imageButtonRandomTest);
		bt.setOnClickListener(this);
		bt = (ImageButton)findViewById(R.id.imageButtonErrorTest);
		bt.setOnClickListener(this);
		bt = (ImageButton)findViewById(R.id.imageButtonProfileList);
		bt.setOnClickListener(this);

	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		ConfigHelper.setKeyInt(getApplicationContext(), "currentQId", currentQId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		int id = item.getItemId();
		Intent intent ;
		switch (id)
		{
			case R.id.menuQuestionList:
				intent = new Intent(this,QuestionListActivity.class);
				startActivity(intent);
				break;
			case R.id.menuInit:
				clearDB();
				break;
			case R.id.menuSync:
				handler.post(runnable);
				item.setEnabled(false);
				break;
			case R.id.menuClearProfile:
				try
				{
					db.deleteAll(Profile.class);
					Toast.makeText(getApplicationContext(), "答题记录清除完成!", Toast.LENGTH_LONG).show();
				}
				catch (DbException e)
				{
					Toast.makeText(getApplicationContext(), "清除答题记录失败:" + e.getMessage(), Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.menuRestartSequnceIndex:
				currentQId = 0;
				makeSequnceTest();
				break;
			case R.id.menuSubject:
				intent = new Intent(this,SysSetup.class);
				startActivity(intent);
				break;
			case R.id.menuAbout:
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("关于我们");
				dialog.setMessage("\n作者：张跃\n欢迎使用夙地工作室作品\n www.sudisoft.com\n13839806667");
				dialog.show();
		}

		return super.onOptionsItemSelected(item);
	}

	private void makeRandomTest()
	{
		try
		{
			List<Question> list = db.findAll(Selector.from(Question.class).where("subject","=",testSubject));
			if (list.size() <=0)
			{
				Toast.makeText(getApplicationContext(), "没有题库!", Toast.LENGTH_LONG).show();
				return;
			}
			Random random = new Random();
			int qid = random.nextInt(list.size());
			qid = list.get(qid).ID;
			Intent intent = new Intent(this,TestQuestionActivity.class);
			intent.putExtra("questionId",qid);
			startActivity(intent);
		}
		catch (DbException e)
		{
			e.printStackTrace();
		}
	}
	private void makeSequnceTest()
	{
		try
		{
			Question q = db.findFirst(Selector.from(Question.class).orderBy("ID").where("subject","=",testSubject).and("ID",">",currentQId));
			if (q != null)
			{
				currentQId = q.ID;
				Intent intent = new Intent(this,TestQuestionActivity.class);
				intent.putExtra("questionId",currentQId);
				startActivity(intent);
			}
			else
			{
				Toast.makeText(getApplicationContext(), "没有找到下一个试题!", Toast.LENGTH_LONG).show();
				currentQId = 0;
				return;
			}

		}
		catch (DbException e)
		{
			e.printStackTrace();
		}
	}
	private void makeErrorTest()
	{
		if (errorList == null || errorList.size() == 0)
		{
			try
			{
				errorList = db.findAll(Selector.from(Profile.class).where("wrong - right",">",0).orderBy("wrong", true));
			} catch (DbException e)
			{
				Toast.makeText(getApplicationContext(), "获取错题列表失败:" + e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			}
			if (errorList == null || errorList.size() == 0)
			{
				Toast.makeText(getApplicationContext(), "没有答题记录或者没有错题(答对次数不小于答错次数的题目不再认为是错题)！", Toast.LENGTH_LONG).show();
				return;
			}
		}
		if (currentErrorIndex >= errorList.size())
		{
			currentErrorIndex = 0;
		}
		Intent intent = new Intent(this,TestQuestionActivity.class);
		intent.putExtra("questionId", errorList.get(currentErrorIndex).QID);
		currentErrorIndex ++;
		startActivity(intent);
	}
	Runnable runnable = new Runnable()
	{
		@Override
		public void run()
		{
			if (!isRun)
			{
				isRun = true;
				getNext(lastId);
			}

			handler.postDelayed(this, 1000);
		}
	};
	@Override
	public void onClick(View v)
	{
		RotateAnimation anim=new RotateAnimation(0.0f, +360.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(500);
		v.startAnimation(anim);

		int btid = v.getId();
		switch (btid)
		{
			case R.id.imageButtonRandomTest:
				makeRandomTest();
				break;
			case R.id.imageButtonSequnceTest:
				makeSequnceTest();
				break;
			case R.id.imageButtonErrorTest:
				makeErrorTest();
				break;
			case R.id.imageButtonProfileList:
				Intent intent = new Intent(this,ProfileListActivity.class);
				startActivity(intent);
				break;
		}

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
				StringBuilder sb = new StringBuilder();

				Question q = new Question();
				try
				{
					JSONObject obj = new JSONObject(responseInfo.result);
					try
					{
						if (obj.getString("error").equals("not found!"))
						{

							handler.removeCallbacks(runnable);
							txt.setText(new Date().toString() + "获取完成!");
							isRun = false;
							return;
						}
					}
					catch(Exception e)
					{

					}
					q.ID = obj.getInt("ID");
					q.subject = obj.getString("subject");
					q.catalog = obj.getString("catalog");
					q.tip = obj.getString("tip");
					q.title = obj.getString("title");
					if (db.findById(Question.class, q.ID) ==null)
					{
						if (q.title != null && !q.title.equals(""))
						{
							db.save(q);
						}
					}
					else
						db.update(q);
					lastId = q.ID;

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
				Date now = new Date();
				txt.setText("获取进度:\n" + now.toString() + "\n" +  sb.toString());
				isRun = false;
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
	private void clearDB()
	{
		try
		{
			db.deleteAll(Question.class);
			db.deleteAll(Choice.class);
			db.deleteAll(MyLog.class);
			db.deleteAll(Profile.class);
			lastId = 0;
		} catch (DbException e)
		{
			Toast.makeText(getApplicationContext(), "初始化出错:" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}
