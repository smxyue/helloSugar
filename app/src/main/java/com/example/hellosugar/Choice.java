package com.example.hellosugar;

import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;


@Table(name="choice")
public class Choice
{
    @Id
    @NoAutoIncrement
    public int ID ;

    public int QID ;

    public String title ;

    public Boolean correct ;

    public Boolean usercheck ;

    @Foreign(column = "parentId", foreign = "id")
    public Question question;

    // Transient使这个列被忽略，不存入数据库
    @Transient
    public String willIgnore;

    public static String staticFieldWillIgnore; // 静态字段也不会存入数据库

}