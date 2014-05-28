package com.example.acpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	
	String acPageUrl = "http://www.acfun.com/v/list63/index.htm";
	String htmlElements = "div.l div.item";
	private ListView listView;  
	ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		
		dialog = ProgressDialog.show(this, "Loading...","Please wait...");
		
		getHtml(acPageUrl);
	}
	
	public void setListView(Elements htmlEle){
		listView = (ListView)findViewById(R.id.acList);
		
		 //生成动态数组，加入数据  
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        for(Element e: htmlEle)  
        {  
        	
            HashMap<String, Object> map = new HashMap<String, Object>();  
            
            map.put("ItemTitle", JsoupHtml.getTitle(e));  
            map.put("ItemText",  JsoupHtml.getIntro(e)); 
            map.put("ItemInfo",	 JsoupHtml.getInfo(e));
            map.put("ItemUrl", JsoupHtml.getUrl(e));
            listItem.add(map);  
        }  
        //生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
            R.layout.aclist_items,//ListItem的XML实现  
            //动态数组对应的子项          
            new String[] {"ItemTitle", "ItemText","ItemInfo"},   
            new int[] {R.id.actitle,R.id.acintro,R.id.acinfo}  
        );  
        
        listView.setAdapter(listItemAdapter);
        //启动文章activity
        listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String,String> map=(HashMap<String,String>)listView.getItemAtPosition(position);
				
				Intent intent = new Intent(MainActivity.this,ShowArticle.class);
				Bundle bundle = new Bundle();
				
				
				bundle.putString("Url",map.get("ItemUrl"));
				intent.putExtras(bundle);
				startActivity(intent);
				Log.v("GetHtml",map.get("ItemUrl"));
			}
        });
	}
	
	//UI更新
	public void getHtml(final String acPageUrlc){
		ProgressDialog mypDialog=new ProgressDialog(this);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

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
			
			//TextView acPageHtmlView = (TextView)findViewById(R.id.acPageHtml);
			String acHtml = msg.getData().getString("message");
			Elements acHtmlEle = JsoupHtml.getItems(acHtml,htmlElements);
			//acPageHtmlView.setText(acHtml);
			//Log.v("GetListHtml",acHtmlEle.toString());
			Log.v("GetHtml","msg");
			setListView(acHtmlEle);
			
			//关闭进度对话框
			dialog.dismiss(); 
		}
	};
	//
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
