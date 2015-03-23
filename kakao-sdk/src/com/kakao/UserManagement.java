/**
 * Copyright 2014 Daum Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao;

import java.util.ArrayList;
import java.util.Map;

import android.os.Bundle;

import com.kakao.helper.ServerProtocol;
import com.kakao.http.HttpRequestTask;
import com.kakao.rest.APIHttpRequestTask;

/**
 * UserManagement API 요청을 담당한다.
 * @author MJ
 */
public class UserManagement {

	/**
	 * 사용자정보 요청
	 * @param responseHandler me 요청 결과에 대한 handler
	 */
	public static void requestMe(final MeResponseCallback responseHandler) {
		requestMe(responseHandler, null, null);
	}

	/**
	 * 사용자 정보 일부나 이미지 리소스를 https로 받고 싶은 경우의 사용자정보 요청
	 * @param responseHandler me 요청 결과에 대한 handler
	 * @param propertyKeys 사용자 정보의 키 리스트
	 * @param secureResource 이미지 url을 https로 반환할지 여부
	 */
	public static void requestMe(final MeResponseCallback responseHandler, final ArrayList<String> propertyKeys, final Boolean secureResource) {
		final String url = HttpRequestTask.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.USER_ME_PATH);

		final Bundle params = new Bundle();
		if(propertyKeys != null && !propertyKeys.isEmpty()) {
			params.putStringArrayList(ServerProtocol.PROPERTY_KEYS_KEY, propertyKeys);
		}
		if(secureResource != null) {
			params.putBoolean(ServerProtocol.SECURE_RESOURCE_KEY, secureResource);
		}

		APIHttpRequestTask.requestGet(responseHandler, Map.class, url, params);
	}

	/**
	 * 로그아웃 요청
	 * @param responseHandler logout 요청 결과에 대한 handler
	 */
	public static void requestLogout(final LogoutResponseCallback responseHandler) {
		final String url = HttpRequestTask.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.USER_LOGOUT_PATH);
		APIHttpRequestTask.requestPost(responseHandler, Map.class, url, null);
	}
}
