package com.ssafy.backend.domain.util.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ssafy.backend.domain.algorithm.dto.response.CBojInfoResponse;

public class BojScoreEvaluator {
	public static int scoreEvaluator(CBojInfoResponse CBojInfoResponse) {
		//제출문제 - (틀린문제 / 맞은문제) * 제출 - 시도했지만 맞은 문제 * (티어 / 맞은문제)
		int score = (int)Math.ceil(
			((double)CBojInfoResponse.getFailCount() / CBojInfoResponse.getPassCount())
				* CBojInfoResponse.getSubmitCount()
				- CBojInfoResponse.getTryFailCount() * (getTierNumberFromUrl(CBojInfoResponse.getTier())
				/ (double)CBojInfoResponse.getPassCount())
				* 0.5);

		return Math.max(score, 0);
	}

	// 티어에서 숫자만 파싱하는 함수
	private static int getTierNumberFromUrl(String tier) {
		Pattern pattern = Pattern.compile("tier/(\\d+)");
		Matcher matcher = pattern.matcher(tier);

		return (matcher.find()) ? Integer.parseInt(matcher.group(1)) : 0;
	}
}