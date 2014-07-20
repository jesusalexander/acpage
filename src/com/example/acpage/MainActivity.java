package com.example.acpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

@SuppressLint("InlinedApi")
public class MainActivity extends ActionBarActivity {

	SwipeRefreshLayout refreshLayout;
	String acPageUrl = "http://www.acfun.tv/v/list110/";
	int morePageUrl = 2;// �������ظ��࣬��һ�ŷ�ҳΪ index_2.htm;
	String htmlElements = "div.l div.item";

	String lastPageID = null;

	private ListView listView;
	private View mFooterView;
	ProgressDialog dialog;

	SimpleAdapter listItemAdapter;
	ArrayList<HashMap<String, Object>> listItem;
	int lastVisibleIndex;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_red_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				getHtml("index.htm", 1);
			}

		});

		dialog = ProgressDialog.show(this, "Loading...", "Please wait...");

		getHtml("index.htm", 0);
	}

	public void setListView(Elements htmlEle) {
		// listview �ײ���ͼ
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		mFooterView = layoutInflater.inflate(R.layout.list_footer, null);

		listView = (ListView) findViewById(R.id.acList);

		// ��������
		// getHtml(String url,int method)
		listView.addFooterView(mFooterView);

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				// ����ͼ����&&���һ��itemʱ
				case OnScrollListener.SCROLL_STATE_IDLE:
					if (view.getLastVisiblePosition()+1 == (view.getCount())) {

						getHtml("index_" + morePageUrl + ".htm", 2);// index_morePageUrl.htm ���ط�ҳ
																
						morePageUrl++;
						Toast.makeText(getBaseContext(), "�������",
								Toast.LENGTH_SHORT).show();
						//listView.removeFooterView(mFooterView);
					}
					break;
				/*
				 * case OnScrollListener.SCROLL_STATE_FLING:
				 * Toast.makeText(getBaseContext(), "����", Toast.LENGTH_SHORT)
				 * .show(); break; case
				 * OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				 * Toast.makeText(getBaseContext(), "����ing", Toast.LENGTH_SHORT)
				 * .show(); break;
				 */
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}

		});

		// ��������ID
		lastPageID = JsoupHtml.getUrl(htmlEle.first()).substring(5);
		Log.v("LastID", lastPageID);
		// ���ɶ�̬���飬��������

		listItem = new ArrayList<HashMap<String, Object>>();
		for (Element e : htmlEle) {

			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("ItemTitle", JsoupHtml.getTitle(e));
			map.put("ItemText", JsoupHtml.getIntro(e));
			map.put("ItemInfo", JsoupHtml.getInfo(e));
			map.put("ItemUrl", JsoupHtml.getUrl(e));
			listItem.add(map);
		}

		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		listItemAdapter = new SimpleAdapter(this,
				listItem,// ����Դ
				R.layout.aclist_items,// ListItem��XMLʵ��
				// ��̬�����Ӧ������
				new String[] { "ItemTitle", "ItemText", "ItemInfo" },
				new int[] { R.id.actitle, R.id.acintro, R.id.acinfo });

		listView.setAdapter(listItemAdapter);

		Toast.makeText(getBaseContext(), "setListView", Toast.LENGTH_SHORT)
				.show();

		// ��������activity
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, String> map = (HashMap<String, String>) listView
						.getItemAtPosition(position);

				Intent intent = new Intent(MainActivity.this, ShowArticle.class);
				Bundle bundle = new Bundle();
				bundle.putString("Title", map.get("ItemTitle"));
				bundle.putString("Url", map.get("ItemUrl"));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	public void reSetListView(final Elements htmlEle) {

		new Handler().postDelayed(new Runnable() {
			public void run() {
				// �ڶ��μ���ʱ�ж��Ƿ�Ϊ������
				String acPageID;
				for (Element e : htmlEle) {
					acPageID = JsoupHtml.getUrl(e).substring(5);

					if (Integer.parseInt(acPageID) > Integer
							.parseInt(lastPageID)) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						Log.v("pageID",
								acPageID + ',' + Integer.parseInt(acPageID)
										+ ',' + Integer.parseInt(lastPageID));
						map.put("ItemTitle", JsoupHtml.getTitle(e));
						map.put("ItemText", JsoupHtml.getIntro(e));
						map.put("ItemInfo", JsoupHtml.getInfo(e));
						map.put("ItemUrl", acPageID);
						listItem.add(0, map);

						lastPageID = acPageID;
					}
				}
				listItemAdapter.notifyDataSetChanged();
				refreshLayout.setRefreshing(false);
				Toast.makeText(getBaseContext(), "reSetListView",
						Toast.LENGTH_SHORT).show();
				
			}
		}, 500);
		
	}

	// �������ظ���
	public void moreSetListView(final Elements htmlEle) {
		new Handler().postDelayed(new Runnable() {
			public void run() {

				listView.addFooterView(mFooterView);
				for (Element e : htmlEle) {

					HashMap<String, Object> map = new HashMap<String, Object>();

					map.put("ItemTitle", JsoupHtml.getTitle(e));
					map.put("ItemText", JsoupHtml.getIntro(e));
					map.put("ItemInfo", JsoupHtml.getInfo(e));
					map.put("ItemUrl", JsoupHtml.getUrl(e));
					listItem.add(map);
				}
				listItemAdapter.notifyDataSetChanged();
				Toast.makeText(getBaseContext(), "moreSetListView",
						Toast.LENGTH_SHORT).show();
				
			}
		}, 500);
	}



	// UI����
	// method 0Ϊ���μ���,1Ϊ����ˢ��,2Ϊ�������ظ���
	public void getHtml(final String acPageUrlc, final int method) {

		ProgressDialog mypDialog = new ProgressDialog(this);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		new Thread(new Runnable() {
			public void run() {
				String acPageHtml = GetPage.http_get(acPageUrl + acPageUrlc);
				Log.v("GetHtml", "HTML");

				Message msg = Message.obtain();
				Bundle data = new Bundle();

				data.putString("message", acPageHtml);
				data.putInt("method", method);
				msg.setData(data);
				handler.sendMessage(msg);
				Log.v("GetHtml", "send msg");

			}
		}).start();

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			// TextView acPageHtmlView =
			// (TextView)findViewById(R.id.acPageHtml);
			String acHtml = msg.getData().getString("message");
			int getMethod = msg.getData().getInt("method");
			Elements acHtmlEle = JsoupHtml.getItems(acHtml, htmlElements);
			// acPageHtmlView.setText(acHtml);
			// Log.v("GetListHtml",acHtmlEle.toString());
			Log.v("GetHtml", "msg");

			// ����ˢ�µ���reSetListView
			// �������ص���moreSetListView
			Log.v("getMethod", Integer.toString(getMethod));
			if (getMethod == 0) {

				setListView(acHtmlEle);

			} else if (getMethod == 1) {
				reSetListView(acHtmlEle);

			} else if (getMethod == 2) {
				moreSetListView(acHtmlEle);
				listView.removeFooterView(mFooterView);

			}
			// �رս��ȶԻ���
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
