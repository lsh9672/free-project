package com.ssafy.backend.domain.github.service;

import static com.ssafy.backend.global.response.exception.CustomExceptionStatus.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.entity.Github;
import com.ssafy.backend.domain.entity.GithubRepo;
import com.ssafy.backend.domain.entity.User;
import com.ssafy.backend.domain.github.dto.GithubDetailLanguage;
import com.ssafy.backend.domain.github.dto.GithubDetailResponse;
import com.ssafy.backend.domain.github.dto.ReadmeResponse;
import com.ssafy.backend.domain.github.repository.GithubRepoRepository;
import com.ssafy.backend.domain.github.repository.GithubRepository;
import com.ssafy.backend.domain.github.repository.querydsl.GithubLanguageQueryRepository;
import com.ssafy.backend.domain.user.dto.NicknameListResponseDTO;
import com.ssafy.backend.domain.user.repository.UserQueryRepository;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.global.response.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubService {
	private final GithubRepoRepository githubRepoRepository;
	private final GithubLanguageQueryRepository githubLanguageQueryRepository;
	private final GithubRepository githubRepository;
	private final UserQueryRepository userQueryRepository;
	private final UserRepository userRepository;

	public List<NicknameListResponseDTO> getNicknameList(String nickname) {
		List<User> userList = userQueryRepository.findByNickname(nickname);
		return userList.stream()
			.map(u -> NicknameListResponseDTO.create(u.getId(), u.getNickname()))
			.collect(Collectors.toList());
	}

	public GithubDetailResponse getDetails(long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
		Github github = githubRepository.findByUser(user).orElseThrow(() -> new CustomException(NOT_FOUND_GITHUB));

		List<GithubDetailLanguage> githubLanguages = githubLanguageQueryRepository.findByGithub(github);
		log.info(github.toString());
		return new GithubDetailResponse(github, githubLanguages);
	}

	public ReadmeResponse getReadme(long githubId, long repositoryId) {
		GithubRepo githubRepo = githubRepoRepository.findById(repositoryId).orElseThrow(() -> {
			throw new CustomException(NOT_FOUND_REPOSITORY);
		});

		validateReadme(githubId, githubRepo);

		return new ReadmeResponse(githubRepo);

	}

	private void validateReadme(long githubId, GithubRepo githubRepo) {
		if (githubRepo.getGithub().getId() != githubId) {
			throw new CustomException(INCONSISTENT_GITHUB_ID);
		}
	}

}