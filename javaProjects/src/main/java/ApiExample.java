/**
 * Sample Java code for youtube.commentThreads.list
 * See instructions for running these code samples locally:
 * https://developers.google.com/explorer-help/guides/code_samples#java
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.CommentThreadReplies;
import com.google.common.collect.Lists;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONObject;    


public class ApiExample {
    private static final String DEVELOPER_KEY = "AIzaSyAc0zmgN6Vc0mIgPnWTZcsrB0bjqQn3UFs";
    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static int counter = 0;
    private static YouTube youtube;
    static List<String> commentsText= Lists.newArrayList();
	static BufferedWriter writer;
    private static YouTube.CommentThreads.List prepareListRequest(String videoId) throws Exception {
        return youtube.commentThreads()
                      .list("snippet,replies")
                      .setVideoId(videoId)
                      .setMaxResults(100L)
                      .setModerationStatus("published")
                      .setTextFormat("plainText");
    }

    private static void handleCommentsThreads(List<CommentThread> commentThreads) {
        try {
			for (CommentThread commentThread : commentThreads) {
			    List<Comment> comments = Lists.newArrayList();
			    
			    comments.add(commentThread.getSnippet().getTopLevelComment());
			    System.out.println(commentThread.getSnippet().getTopLevelComment().getSnippet().getTextDisplay());
			    writer.write(commentThread.getSnippet().getTopLevelComment().getSnippet().getTextDisplay()+"\n");
			    commentsText.add(commentThread.getSnippet().getTopLevelComment().getSnippet().getTextDisplay());
			    CommentThreadReplies replies = commentThread.getReplies();
			    if (replies != null){
			    	System.out.println(replies.getComments().get(0).getSnippet().getTextDisplay());
			    	writer.write(replies.getComments().get(0).getSnippet().getTextDisplay()+"\n");
			    	commentsText.add(replies.getComments().get(0).getSnippet().getTextDisplay());
			        comments.addAll(replies.getComments());
			    }
			    counter += comments.size();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void main(String[] args) throws Exception {
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
        Credential credential = Auth.authorize(scopes, "commentthreads");
        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName("AppZapp").build();
        writer = new BufferedWriter(new FileWriter("Comments.txt"));
        //String url="https://youtu.be/NXJKmlVqKHk";
        System.out.println("\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Youtube URL: ");
        
        String url=sc.nextLine();
        String videoId =url.substring(17);
        System.out.println("\nComments: ");
        writer.write("Youtube URL: "+url);
        writer.write("\n\nComments:\n");
        
//        String text = "If only that had happened while I was at camp there a month ago. XD﻿";
//		
//		SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
//		sentimentAnalyzer.initialize();
//		SentimentResult sentimentResult = sentimentAnalyzer.getSentimentResult(text);
//
//		System.out.println("Sentiment Score: " + sentimentResult.getSentimentScore());
//		System.out.println("Sentiment Type: " + sentimentResult.getSentimentType());
//		System.out.println("Very positive: " + sentimentResult.getSentimentClass().getVeryPositive()+"%");
//		System.out.println("Positive: " + sentimentResult.getSentimentClass().getPositive()+"%");
//		System.out.println("Neutral: " + sentimentResult.getSentimentClass().getNeutral()+"%");
//		System.out.println("Negative: " + sentimentResult.getSentimentClass().getNegative()+"%");
//		System.out.println("Very negative: " + sentimentResult.getSentimentClass().getVeryNegative()+"%");
//		
        // Get video comments threads
        CommentThreadListResponse commentsPage = prepareListRequest(videoId).execute();

        while (true) {
            handleCommentsThreads(commentsPage.getItems());

            String nextPageToken = commentsPage.getNextPageToken();
            if (nextPageToken == null)
                break;

            // Get next page of video comments threads
            commentsPage = prepareListRequest(videoId).setPageToken(nextPageToken).execute();
        }
        writer.close();
        //System.out.println("Total Visible Comments: " + counter);
    }
}