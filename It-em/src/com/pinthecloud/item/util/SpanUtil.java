package com.pinthecloud.item.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.UserPageActivity;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.ItSpan;

public class SpanUtil {

	public static SpannableStringBuilder getSpannedBody(Context context, String body, char prefix){
		SpannableStringBuilder spannedBody = new SpannableStringBuilder(body);
        ArrayList<int[]> spanPositions = getSpanPositions(body, prefix);
        for(int[] spanPosition : spanPositions) {
            int spanStart = spanPosition[0];
            int spanEnd = spanPosition[1];
            spannedBody.setSpan(new ItSpan(context, prefix), spanStart, spanEnd, 0);
        }
        return spannedBody;
	}
	
	
	public static List<String> getSpanList(String body, char prefix){
		List<String> spanList = new ArrayList<String>();
		SpannableStringBuilder content = new SpannableStringBuilder(body);
        ArrayList<int[]> spanPositions = getSpanPositions(body, prefix);
        for(int[] spanPosition : spanPositions) {
            int spanStart = spanPosition[0]+1;
            int spanEnd = spanPosition[1];
            spanList.add(content.subSequence(spanStart, spanEnd).toString());
        }
        return spanList;
	}
	
	
	public static ArrayList<int[]> getSpanPositions(String body, char prefix) {
        ArrayList<int[]> spans = new ArrayList<int[]>();
        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }
        return spans;
    }
	
	
	public static void setNickNameSpan(final ItActivity activity, SpannableStringBuilder spannedString, int start, int end, final String userId){
		StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
		ClickableSpan clickSpan = new ClickableSpan() {

			@Override
			public void updateDrawState(TextPaint ds) {
				ds.setColor(activity.getResources().getColor(R.color.brand_text_color));
			}
			@Override
			public void onClick(View widget) {
				Intent intent = new Intent(activity, UserPageActivity.class);
				intent.putExtra(ItUser.INTENT_KEY, userId);
				activity.startActivity(intent);
			}
		};
		
		spannedString.setSpan(boldSpan, start, end, 0);
		spannedString.setSpan(clickSpan, start, end, 0);
	}
}
