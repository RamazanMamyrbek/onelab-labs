package org.onelab.reviewcheckerservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.onelab.reviewcheckerservice.entity.ForbiddenWord;
import org.onelab.reviewcheckerservice.repository.ForbiddenWordRepository;
import org.onelab.reviewcheckerservice.service.ForbiddenWordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ForbiddenWordServiceImpl implements ForbiddenWordService {
    private final ForbiddenWordRepository forbiddenWordRepository;


    @Override
    public boolean isForbidden(String text) {
        List<ForbiddenWord> forbiddenWords = forbiddenWordRepository.findAll();
        String[] words = text.toLowerCase().split("\\s+");

        return Arrays.stream(words)
                .anyMatch(word -> forbiddenWords.stream()
                        .map(fw -> fw.getWord().toLowerCase())
                        .anyMatch(word::equals));
    }
}
