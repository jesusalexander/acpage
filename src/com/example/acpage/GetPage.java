package com.example.acpage;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetPage {
	
	
	private static final String ACTIVITY_TAG = "WHAT";

	/*public static String loadHtml() throws IOException{
		Document doc = Jsoup.connect("http://na.sise.cn/err/limit.html")
				  .data("query", "Java")
				  .userAgent("Mozilla")
				  .cookie("auth", "token")
				  .timeout(3000)
				  .post();
		
		return doc.html();
		
	}*/
	
	/**
	 * get����URL��ʧ��ʱ��������
	 * 
	 * @param url
	 *            ������ַ
	 * @return ��ҳ���ݵ��ַ���
	 * @author Lai Huan
	 * @created 2013-6-20
	 */
	public static String http_get(String url) {
		final int RETRY_TIME = 2;
		HttpClient httpClient = null;
		HttpGet httpGet = null;

		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = new HttpGet(url);
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == 200) {
					// ��utf-8����ת��Ϊ�ַ���
					
					byte[] bResult = EntityUtils.toByteArray(response
							.getEntity());
					if (bResult != null) {
						responseBody = new String(bResult, "UTF-8");

					}
				}
				break;
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				e.printStackTrace();
			} finally {
				httpClient = null;
			}
		} while (time < RETRY_TIME);

		return responseBody;
	}

	private static HttpClient getHttpClient() {
		HttpParams httpParams = new BasicHttpParams();
		// �趨���ӳ�ʱ�Ͷ�ȡ��ʱʱ��
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		return new DefaultHttpClient(httpParams);
	}
}
//
