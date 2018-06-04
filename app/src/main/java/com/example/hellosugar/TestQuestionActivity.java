package com.example.hellosugar;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TestQuestionActivity extends Activity  implements OnClickListener
{

	TextView txt= null;
	LinearLayout choiceLayout = null;
	DbUtils db = null;
	List<Choice> choices = new ArrayList<Choice>();
	List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
	Question question = null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.testquestion);

		txt = (TextView)findViewById(R.id.textViewTitle);
		choiceLayout = (LinearLayout)findViewById(R.id.linearLayoutChoices);

		Intent intent = this.getIntent();
		int qid = intent.getExtras().getInt("questionId");
		db = DbUtils.create(this);
		getQuestion(qid);

		ImageButton bt = (ImageButton)findViewById(R.id.imageButtonOk);
		bt.setOnClickListener(this);

		bt = (ImageButton)findViewById(R.id.imageButtonCancel);
		bt.setOnClickListener(this);
		bt.setOnLongClickListener(new OnLongClickListener()
		{

			@Override
			public boolean onLongClick(View v)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(question.title);
				sb.append("\n\n");
				for (Choice c : choices)
				{
					sb.append("〇");
					sb.append(c.title);
					if (c.correct)
						sb.append("√");
					sb.append("\n");
				}
				AlertDialog.Builder dialog = new AlertDialog.Builder(TestQuestionActivity.this);
				dialog.setTitle("看答案");
				dialog.setMessage(sb.toString());
				dialog.show();
				return false;
			}
		});
	}

	//获取并显示试题
	private void getQuestion(int id)
	{
		try
		{
			question = db.findById(Question.class, id);
			if (question != null)
			{
				this.setTitle("[" + question.subject + "]");
				choices = db.findAll(Selector.from(Choice.class).where("qid","=",question.ID));
				for(Choice c : choices)
				{
					CheckBox chk = new CheckBox(this);
					chk.setText(c.title);
					checkBoxes.add(chk);
					choiceLayout.addView(chk);
				}
				txt.setText("【" + question.catalog + "】 \n" + question.title );
			}
			else
			{
				Toast.makeText(getApplicationContext(), "无法找到试题:" + id, Toast.LENGTH_LONG).show();
				this.finish();
			}
		}
		catch (DbException e)
		{
			Toast.makeText(getApplicationContext(), "获取试题失败", Toast.LENGTH_LONG).show();
			this.finish();
		}

	}
	//检查回答
	public int checkAnswer()
	{
		if (choices.size()<=1)
		{
			return -1;
		}
		int i=0;
		for(CheckBox chk:checkBoxes)
		{
			if (chk.isChecked())
				i ++;
		}
		if (i==0)
		{
			Toast.makeText(getApplicationContext(), "请选择答案！", Toast.LENGTH_LONG).show();
			return -1;
		}
		if ((question.catalog.equals("单选") || question.equals("判断")) && i > 1)
		{
			Toast.makeText(getApplicationContext(), "单选（判断）题只能选择一个答案!", Toast.LENGTH_LONG).show();
			return -1;
		}
		for(i=0;i <choices.size(); i++)
		{
			if (choices.get(i).correct != checkBoxes.get(i).isChecked())
			{
				setUserProfile(question.ID,question.title,false);
				return 0;
			}

		}
		setUserProfile(question.ID, question.title, true);
		return 1;
	}

	//保存答题记录
	public void setUserProfile(int qid, String title, boolean isRight)
	{
		boolean isAdd = false;
		Profile p = null;
		try
		{
			p = db.findFirst(Selector.from(Profile.class).where("QID","=",qid));
		}
		catch (DbException e)
		{
		}
		if (p==null)
		{
			isAdd = true;
			p = new Profile();
			p.QID = qid;
			p.title = title;
			p.right = 0;
			p.wrong = 0;
		}
		if (isRight)
		{
			p.right ++;
		}
		else
		{
			p.wrong ++;
		}
		try
		{
			if (isAdd)
				db.save(p);
			else
				db.update(p);
		}
		catch (DbException e)
		{
			Toast.makeText(getApplicationContext(), "保存答题记录失败:" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch(id)
		{
			case R.id.imageButtonOk:
				int resultCode = checkAnswer();
				String msg = "回答错误!";
				if (resultCode == 1)
					msg = "回答正确!";
				if (resultCode != -1)
				{
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
					this.finish();
				}

				break;
			case R.id.imageButtonCancel:
				this.finish();
		}
	}
}
