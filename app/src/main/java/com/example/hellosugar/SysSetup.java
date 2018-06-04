package com.example.hellosugar;

import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SysSetup extends Activity  implements OnClickListener
{
	private String testSubject;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.syssetup);

		testSubject = ConfigHelper.getKey(getApplicationContext(), "subject");

		RadioGroup subjectGroup = (RadioGroup)findViewById(R.id.radioGroupSubject);

		DbUtils db = DbUtils.create(getApplicationContext());

		SqlInfo sql = new SqlInfo();
		sql.setSql("select distinct subject from question");
		try
		{
			List<DbModel> slist = db.findDbModelAll(sql);
			for(DbModel m : slist)
			{
				RadioButton r = new RadioButton(this);
				String val = m.getString("subject");
				r.setText(val);
				if (testSubject.equals(val))
					r.setChecked(true);
				else
					r.setChecked(false);
				r.setOnClickListener(this);
				subjectGroup.addView(r);
			}
		} catch (DbException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v)
	{
		RadioButton r = (RadioButton)v;
		String val =  r.getText().toString();
		ConfigHelper.setKey(getApplicationContext(), "subject", val);

		Toast.makeText(getApplicationContext(), "你已经切换到科目：" + val + "! 请重启启动程序开始测验，顺序测验需要清除上次位置，请在主菜单内设置", Toast.LENGTH_LONG).show();
		this.finish();
	}
}
