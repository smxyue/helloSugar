package com.example.hellosugar;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Finder;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.sqlite.FinderLazyLoader;

@Table(name="question" , execAfterTableCreated = "CREATE UNIQUE INDEX index_title ON question(title)")
public class Question
{
	@Id
	@NoAutoIncrement
	@Column(column="id")
    public int ID ;

    public String subject;

    public String catalog ;

    public String title ;

    public String tip ;

    public int status ;
    
    @Finder(valueColumn = "id", targetColumn = "QID")
    public FinderLazyLoader<Choice> choices; 
}