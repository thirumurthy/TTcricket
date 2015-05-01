package com.example.ttcricket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import Common.JSON;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MainActivity extends Activity {

	TextView t1;
	Button ref;
	ProgressBar p1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		t1=(TextView) findViewById(R.id.score);
		ref=(Button) findViewById(R.id.ref);
		p1=(ProgressBar) findViewById(R.id.p1);
		
		ref.setVisibility(View.GONE);
		
		ref.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				ref.setVisibility(View.GONE);
				p1.setVisibility(View.VISIBLE);
				DownloadWebPageTask task = new DownloadWebPageTask();
				 task.execute(new String[] { "hi"});
			
			}
		});
		t1.setText("Start getting score...");
		try {
			
			p1.setVisibility(View.VISIBLE);
			DownloadWebPageTask task = new DownloadWebPageTask();
			 task.execute(new String[] { "hi"});
		
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
	    
		
		@SuppressWarnings("deprecation")
		@Override
	    protected String doInBackground(String... urls) {
	      String response = "",result="",siteurl="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20cricket.scorecard.live.summary&format=json&diagnostics=true&env=store%3A%2F%2F0TxIGQMQbObzvU4Apia0V0&callback=";
	     
	      // Check internet status.
	      ConnectivityManager cm=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	      NetworkInfo ni=cm.getActiveNetworkInfo();
	      if(ni!=null)
	      {
	            
	      
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet(siteurl);
	      
	        try {
	        	
	      	        	HttpResponse execute = null;
	        	try
	          {
	        		execute= client.execute(httpGet);
	          }
	          catch(Exception exx)
	          {
	        	  result="4";
	        	  	          }
	          InputStream content = execute.getEntity().getContent();
	          response=getStringResponse(content);
	         if(response!=null)
	         {
	        	 result=response;
	        	   
		        
	         }
	          
	          
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      
	      }
	      else
	      {
	    	  result="3";
	      }
	      return result;
	    }
	    
	   

		@Override
	    protected void onPostExecute(String result) {
			
			String displaytext="";
			try
			{
				//YahooCricket obj=new YahooCricket();
				if(result!=null&&result.trim()!="")
				{
					
					JsonObject jobj=JSON.strToJson(result);
					
					if(jobj!=null)
					{
						JsonObject jscore=jobj.get("query").getAsJsonObject().get("results").getAsJsonObject().get("Scorecard").getAsJsonObject();
						displaytext+="Match:"+jscore.get("mn").getAsString()+"\n";
						displaytext+="Series:"+jscore.get("series").getAsJsonObject().get("series_name").getAsString()+"\n"+
						"Match Status:"+jscore.get("ms").getAsString()+"\n";
						displaytext+="Team:\n";
					
					
						for(JsonElement slgjson:jscore.get("teams").getAsJsonArray() )
						{
							displaytext+=slgjson.getAsJsonObject().get("fn").getAsString()+"("+slgjson.getAsJsonObject().get("sn").getAsString()+") Vs ";
							
						}
						displaytext=displaytext.substring(0, displaytext.length()-3);
						
						displaytext+="\n Run:"+jscore.get("past_ings").getAsJsonObject().get("s").getAsJsonObject().get("a").getAsJsonObject().get("r").getAsString()+"/"+jscore.get("past_ings").getAsJsonObject().get("s").getAsJsonObject().get("a").getAsJsonObject().get("w").getAsString()+"("+jscore.get("past_ings").getAsJsonObject().get("s").getAsJsonObject().get("a").getAsJsonObject().get("o").getAsString()+")\nRR - "+jscore.get("past_ings").getAsJsonObject().get("s").getAsJsonObject().get("a").getAsJsonObject().get("cr").getAsString();
						
						displaytext+="\n----------------------------\n";
						
						if(jscore.get("past_ings").getAsJsonObject().get("d").getAsJsonObject().get("a").getAsJsonObject().get("t").isJsonArray())
						{
							for(JsonElement slgjson:jscore.get("past_ings").getAsJsonObject().get("d").getAsJsonObject().get("a").getAsJsonObject().get("t").getAsJsonArray())
							{
								DecimalFormat df=new DecimalFormat("#.##");
								Double sr=Double.parseDouble(slgjson.getAsJsonObject().get("sr").getAsString());
								
								
								displaytext+=slgjson.getAsJsonObject().get("name").getAsString()+" "+slgjson.getAsJsonObject().get("r").getAsString()+"("+slgjson.getAsJsonObject().get("b").getAsString()+") SR:"+df.format(sr)+"\n 4s:"+slgjson.getAsJsonObject().get("four").getAsString()+" 6s:"+
										slgjson.getAsJsonObject().get("six").getAsString()+"\n\n";
							}
						}
						else
						{
							JsonObject slgjson=jscore.get("past_ings").getAsJsonObject().get("d").getAsJsonObject().get("a").getAsJsonObject().get("t").getAsJsonObject();
							DecimalFormat df=new DecimalFormat("#.##");
							Double sr=Double.parseDouble(slgjson.getAsJsonObject().get("sr").getAsString());
							displaytext+=slgjson.getAsJsonObject().get("name").getAsString()+" "+slgjson.getAsJsonObject().get("r").getAsString()+"("+slgjson.getAsJsonObject().get("b").getAsString()+") SR:"+df.format(sr)+"\n 4s:"+slgjson.getAsJsonObject().get("four").getAsString()+" 6s:"+
									slgjson.getAsJsonObject().get("six").getAsString()+"\n\n";
						}
						
					}
					/*obj=(YahooCricket) JSON.deserialize(result, YahooCricket.class.getName());
					if(obj!=null)
					{
						displaytext+="Match:"+obj.query.results.scorecard.mn+"\n";
						displaytext+="Series:"+obj.query.results.scorecard.series.series_name+"\n"+
						"Match Status"+obj.query.results.scorecard.ms+"\n";
						displaytext+="Team:\n";
						
						for(Teams slgteam:obj.query.results.scorecard.teams )
						{
							displaytext+=slgteam.fn+"("+slgteam.sn+") Vs ";
							
						}
						displaytext=displaytext.substring(0, displaytext.length()-3);
						
						displaytext+="Run:\n"+obj.query.results.scorecard.past_ings.s.a.r+"/"+obj.query.results.scorecard.past_ings.s.a.w+"("+obj.query.results.scorecard.past_ings.s.a.o+")\nRR"+obj.query.results.scorecard.past_ings.s.a.cr;
						
						displaytext+="--------------------------------\n";
						for(T slgt: obj.query.results.scorecard.past_ings.d.a.t)
						{
							displaytext+=slgt.name+" "+slgt.r+"("+slgt.b+") SR"+slgt.sr+" 4s"+slgt.four+" 6s"+slgt
									.six+"";
						}
					}
				*/}
				t1.setText(displaytext);
			}
			catch (Exception e) {
				e.printStackTrace();
				t1.setText("Error Occured");
				p1.setVisibility(View.INVISIBLE);
			}
			ref.setVisibility(View.VISIBLE);
			p1.setVisibility(View.INVISIBLE);
			
	    /*	int ststuscode=Integer.parseInt(result);
	    //	p1.setVisibility(View.GONE);
	    	switch(ststuscode)
	    	{
	    	case 1:
	    		error.setText("SMS Sent Successfully...!");
	    		Toast.makeText(getApplicationContext(), "SMS Sent Successfully...!", BIND_IMPORTANT).show();
	    		break;

	    	case 2:
	    		error.setText("SMS cannot send");
	    		Toast.makeText(getApplicationContext(), "SMS cannot send", BIND_IMPORTANT).show();
	    		break;
	    	case 3:
	    		error.setText("Please Switch on the Mobile Data.. !");
	    		Toast.makeText(getApplicationContext(), "Please Switch on the Mobile Data.. !", BIND_IMPORTANT).show();
	    		break;
	    	case 4:
	    		error.setText("Server Failure. Please Contact admin.");
	    		Toast.makeText(getApplicationContext(), "Server Failure. Please Contact admin.", BIND_IMPORTANT).show();
	    		break;
	    		default:
	    			error.setText("Something went wrong");
	    			Toast.makeText(getApplicationContext(), "Something went wrong", BIND_IMPORTANT).show();
	    	}*/
	    	
	    }
	    
		    public  String getStringResponse(InputStream content)
		    {
		    	String response="";
	
		    	try
		    	{
		          BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		          String s = "";
		          while ((s = buffer.readLine()) != null) {
		            response += s;
		          }
		    	}
		    	catch(Exception exp)
		    	{
		    		exp.printStackTrace();
		    	}
		    	return response;
		    }
	    private String getRequriedString(String source,String startstr, String endstr) {
			try
			{
				if(source!=null)
				{
					return source.split(startstr)[1].split(endstr)[0];
				}
			}
			catch(Exception exp)
			{
				exp.printStackTrace();
			}
			return null;
		}
 
	  }
}
