package com.example.acpage;

import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData.Item;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowArticle extends Activity {
	private ActionBar  bar = null; 
	ProgressDialog dialog;
	
	String acArticleUrl ="http://www.acfun.com/";
	String htmlElements = "#area-player";
	private TextView acArtTV;
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_article);
		
		bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		acArticleUrl = acArticleUrl+bundle.getString("Url");
		dialog = ProgressDialog.show(this, "文章加载中...","有种打我啊...");
		getHtml(acArticleUrl);
	}
	
	//UI更新
	public void getHtml(final String acPageUrlc){
		new Thread(new Runnable(){
			public void run(){
				String acPageHtml = GetPage.http_get(acPageUrlc);
				Log.v("GetHtml","HTML");
				
				Message msg = Message.obtain();
				Bundle data = new Bundle();
				data.putString("message", acPageHtml);
				msg.setData(data);
				handler.sendMessage(msg);
				Log.v("GetHtml","send msg");

			}
		}).start();
		
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);

			String acHtml = msg.getData().getString("message");
			//解析并获取htmlElements元素
			Elements acHtmlEle = JsoupHtml.getItems(acHtml,htmlElements);
			//acPageHtmlView.setText(acHtml);
			Log.v("GetHtml","msg");
			String acHtmlStr = JsoupHtml.getArticle(acHtmlEle.first());
			
			acArtTV = (TextView)findViewById(R.id.acContent);
			acArtTV.setText(Html.fromHtml(acHtmlStr));
			//滚动条
			//acArtTV.setMovementMethod(ScrollingMovementMethod.getInstance());
						
			//关闭进度对话框
			dialog.dismiss();
		}
	};
	//
	 public boolean onOptionsItemSelected(MenuItem item){
		 switch(item.getItemId()){ 
		 	case android.R.id.home:
		 		finish();
		 		return true; 
		 	default :
		 		break;
		 }
		return super.onOptionsItemSelected(item);
		 
	 }


}
