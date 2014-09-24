import java.util.Date;
import java.io.File; 
import java.util.Date; 
import java.text.SimpleDateFormat;

import jxl.*; 
import jxl.write.Number;
import jxl.write.*; 

import facebook4j.PagableList;
import facebook4j.Like;
import facebook4j.Comment;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.PagableList;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import facebook4j.*;
import facebook4j.internal.org.json.*;

public class Comments {

	/**
	 * A simple Facebook4J client.
	 * 
	 * 
	 * @param args
	 * @throws FacebookException 
	 */
	public static void main(String[] args) throws Exception{
	
		int size = 0;
		int reply_size = 0;
		int sum = 0;

		String target = "8103318119";

		// Generate facebook instance.
		Facebook facebook = new FacebookFactory().getInstance();
		// Use default values for oauth app id.
		facebook.setOAuthAppId("618937894850468", "8dd5e2baad050240c8022f8928c3b27e");
		// Get an access token from: 
		// https://developers.facebook.com/tools/explorer
		// Copy and paste it below.
		String accessTokenString = "CAACEdEose0cBAFIxIVAzmi4lGOzqYDJl4KZBP2PRRUxZAw7ZAZBh9qZCJ8RGXeJ1hSAlVpwUo3DSab9EpKRYpMA3aZAzjcybXlPUOS3KgqbLsisLF82qWFYJhqQA44KHZBOrDvGzm8LNCWwL83pSXQF2W2dc4HJkQDzCZAa9eHBKLZBnvJPmTVUcFSAZBkRiaGi12PBNDC5pJjUHXUYNT6rXuY";
		AccessToken at = new AccessToken(accessTokenString);
		// Set access token.
		facebook.setOAuthAccessToken(at);

		// We're done.
		// Write some stuff to your wall.

//		System.out.println("From " + k + "To " + (k+50));
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
		String end_time_str = "01-01-2014 00-00-00";
		String start_time_str = "01-10-2013 00-00-00";
		
		Date start_time = formatter.parse(start_time_str);
		Date end_time = formatter.parse(end_time_str);

		ResponseList<Post> feeds = facebook.getPosts(target, new Reading().limit(100).since(start_time).until(end_time));

		String xls_file = "comments.xls";
		File xls = new File(xls_file);
		WritableSheet sheet = null;
		WritableWorkbook workbook = null;
		workbook = Workbook.createWorkbook(xls);
		sheet = workbook.createSheet("First Sheet", 0);
		
		sum = 0;		
		int loop = 1;

		Label label1, label2, label3, label4, label5;
		Number num1;
		Comment com;
		while ((feeds!=null) && (loop == 1)) {

			for (int i = 0; i < feeds.size(); i++) {
	
				// Get post.
				Post post = feeds.get(i);

				if (post.getCreatedTime().compareTo(start_time)<0) {
					loop = 0;
					break;
				}

				System.out.println(post.getId());

				//3. # of comments
	
				PagableList<Comment> comments = post.getComments();
	
				while (comments!=null) {
	
					for (int j = 0; j < comments.size(); j++) {
						com = comments.get(j);
						RawAPIResponse res = facebook.callGetAPI(com.getId());
						JSONObject jsonObject = res.asJSONObject();
						String username = jsonObject.getJSONObject("from").getString("name");
						
						String postid = post.getId();
						String commentid = com.getId();
						String time = com.getCreatedTime().toString();
						String message = com.getMessage();
						int like_count = com.getLikeCount();
						label1 = new Label(0, sum, postid); 
						label2 = new Label(1, sum, commentid); 
						label3 = new Label(2, sum, username);
						label4 = new Label(3, sum, time); 
						num1 = new Number(4, sum, like_count);
						label5 = new Label(5, sum, message); 

						sheet.addCell(label1);					
						sheet.addCell(label2);					
						sheet.addCell(label3);					
						sheet.addCell(label4);					
						sheet.addCell(label5);					
						sheet.addCell(num1);					
						sum++;
						System.out.println("Finish the " + sum + " comment");

						RawAPIResponse res1 = facebook.callGetAPI(com.getId()+"/comments");
						JSONObject jsonObject1 = res1.asJSONObject();
						JSONArray json_reply = jsonObject1.getJSONArray("data");
						for (int k = 0; k < json_reply.length(); k++) {
							label1 = new Label(0, sum, postid);
							label2 = new Label(1, sum, commentid);
							label3 = new Label(2, sum, json_reply.getJSONObject(k).getJSONObject("from").getString("name"));
							label4 = new Label(3, sum, json_reply.getJSONObject(k).getString("created_time"));
							label5 = new Label(5, sum, json_reply.getJSONObject(k).getString("message"));
							num1 = new Number(4, sum, json_reply.getJSONObject(k).getInt("like_count"));
							sheet.addCell(label1);					
							sheet.addCell(label2);					
							sheet.addCell(label3);					
							sheet.addCell(label4);					
							sheet.addCell(label5);					
							sheet.addCell(num1);					
							sum++;
							System.out.println("Finish the " + sum + " comment");
						}
					}				
	
					if (comments.getPaging() == null) {
						comments = null;
					}
					else {
						comments = facebook.fetchNext(comments.getPaging());
					}
				}
	
			}

			if (feeds.getPaging() == null) {
				feeds = null;
			}
			else {
			feeds = facebook.fetchNext(feeds.getPaging());
			}
		}

		workbook.write();
		workbook.close();

    }
}
