package com.pinthecloud.item.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.SpannableString;

import com.pinthecloud.item.view.HashTagSpan;

public class TextUtil {

	public static SpannableString getBody(Context context, String body){
        ArrayList<int[]> hashTagSpans = getSpans(body, '#');
        SpannableString content = new SpannableString(body);
        for(int i=0 ; i<hashTagSpans.size() ; i++) {
            int[] span = hashTagSpans.get(i);
            int hashTagStart = span[0];
            int hashTagEnd = span[1];
            content.setSpan(new HashTagSpan(context), hashTagStart, hashTagEnd, 0);
        }
        return content;
	}
	
	
	public static List<String> getSpanBodyList(String body){
		List<String> spanBodyList = new ArrayList<String>();
		ArrayList<int[]> hashTagSpans = getSpans(body, '#');
        SpannableString content = new SpannableString(body);
        for(int i=0 ; i<hashTagSpans.size() ; i++) {
            int[] span = hashTagSpans.get(i);
            int hashTagStart = span[0]+1;
            int hashTagEnd = span[1];
            spanBodyList.add(content.subSequence(hashTagStart, hashTagEnd).toString());
        }
        return spanBodyList;
	}
	
	
	public static ArrayList<int[]> getSpans(String body, char prefix) {
        ArrayList<int[]> spans = new ArrayList<int[]>();
        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }
        return  spans;
    }
}
