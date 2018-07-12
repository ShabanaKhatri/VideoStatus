package com.videoStatus.parse;

/**
 * @author Kishan H Dhamat , Email: kishan.dhamat105@gmail.com
 */
public interface AsyncTaskCompleteListener {
	void onTaskCompleted(String response, int serviceCode);
}
