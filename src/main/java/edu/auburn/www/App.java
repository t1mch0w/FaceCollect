import java.util.Date;
import java.io.File; 
import java.util.Date; 

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

public class App {

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
		String accessTokenString =
"CAACEdEose0cBAGgjIAuXm9ggzc1pRpfrw2ZClZBFBYTL3ZAQyqXKaXD722caSbdMbXXAivxXbeTmBuapVIlHaqffQC6kX6Mhy0vzDYVXk68XsajEVoAwcce5Hl0QHxMRH1nwgZAMIn5G21SHPM1ZBU0NkXNLmMFvfFXZACC8kDcZAWQ8t3yfvqQy985jCxyAMUOLJBZCHAGM9NQK9pNBMygF";
		AccessToken at = new AccessToken(accessTokenString);
		// Set access token.
		facebook.setOAuthAccessToken(at);

		// We're done.
		// Write some stuff to your wall.

//		System.out.println("From " + k + "To " + (k+50));
		ResponseList<Post> feeds = facebook.getPosts(target, new Reading().limit(100));

		String xls_file = "target.xls";
		File xls = new File(xls_file);
		WritableSheet sheet = null;
		WritableWorkbook workbook = null;
		workbook = Workbook.createWorkbook(xls);
		sheet = workbook.createSheet("First Sheet", 0);

		sum = 0;		

		while (feeds!=null) {

			for (int i = 0; i < feeds.size(); i++) {
	
			    // Get post.
			    Post post = feeds.get(i);
			    // Get (string) message.
			                    // Print out the message.
				//1. Date
				Date date = post.getCreatedTime();
				Label label1 = new Label(0, sum, date.toString()); 
				sheet.addCell(label1);
				
				//2. Type
				String type = post.getType();
				Label label2 = new Label(1, sum, type); 
				sheet.addCell(label2);
	
				//3. # of comments
				PagableList<Comment> comments = post.getComments();

				Label label5;

				size = 0;
				RawAPIResponse res = facebook.callGetAPI(post.getId()+"/comments?summary=1");
				JSONObject jsonObject = res.asJSONObject();
				if(jsonObject.has("summary")) {
					if(jsonObject.getJSONObject("summary").has("total_count"))
					{
						size = jsonObject.getJSONObject("summary").getInt("total_count");	
					}
				}
				Number number1 = new Number(2, sum, size); 
				sheet.addCell(number1);
	
				//4. # of shares
				size = post.getSharesCount()==null?0:post.getSharesCount();
				Number number2 = new Number(3, sum, size); 
				sheet.addCell(number2);
	
				//5. # of likes

				RawAPIResponse res1 = facebook.callGetAPI(post.getId()+"/likes?summary=1");
				JSONObject jsonObject1 = res1.asJSONObject();

				size = 0;
				if(jsonObject1.has("summary")) {
					if(jsonObject1.getJSONObject("summary").has("total_count"))
					{
						size = jsonObject1.getJSONObject("summary").getInt("total_count");	
					}
				}

				Number number3 = new Number(4, sum, size); 
				sheet.addCell(number3);
				
				Label label3 = new Label(5, sum, post.getName()); 
				sheet.addCell(label3);
	
				//6. Message
				Label label4 = new Label(6, sum, post.getMessage()); 
				sheet.addCell(label4);
	
			    // Write in excel file
	
				sum++;
				System.out.println("Finish the " + sum + " post");
	
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
