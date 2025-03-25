package org.onelab.reviewcheckerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.reviewcheckerservice.entity.ForbiddenWord;
import org.onelab.reviewcheckerservice.repository.ForbiddenWordRepository;
import org.onelab.reviewcheckerservice.service.impl.ForbiddenWordServiceImpl;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForbiddenWordServiceImplTest {

    @Mock
    private ForbiddenWordRepository forbiddenWordRepository;

    @InjectMocks
    private ForbiddenWordServiceImpl forbiddenWordService;

    @Test
    void isForbidden_ShouldReturnTrueWhenForbiddenWordExists() {
        List<ForbiddenWord> forbiddenWords = List.of(
                new ForbiddenWord(1L, "bad"),
                new ForbiddenWord(2L, "evil")
        );
        when(forbiddenWordRepository.findAll()).thenReturn(forbiddenWords);

        boolean result = forbiddenWordService.isForbidden("This is BAD text");

        assertTrue(result);
    }

    @Test
    void isForbidden_ShouldReturnFalseWhenNoForbiddenWords() {
        when(forbiddenWordRepository.findAll()).thenReturn(List.of());

        boolean result = forbiddenWordService.isForbidden("Normal text");

        assertFalse(result);
    }

    @Test
    void isForbidden_ShouldReturnFalseWhenTextDoesNotContainForbiddenWords() {
        List<ForbiddenWord> forbiddenWords = List.of(
                new ForbiddenWord(1L, "bad"),
                new ForbiddenWord(2L, "evil")
        );
        when(forbiddenWordRepository.findAll()).thenReturn(forbiddenWords);

        boolean result = forbiddenWordService.isForbidden("Good text");

        assertFalse(result);
    }

    @Test
    void isForbidden_ShouldBeCaseInsensitive() {
        List<ForbiddenWord> forbiddenWords = List.of(
                new ForbiddenWord(1L, "bad")
        );
        when(forbiddenWordRepository.findAll()).thenReturn(forbiddenWords);

        assertTrue(forbiddenWordService.isForbidden("BAD word"));
        assertTrue(forbiddenWordService.isForbidden("bad word"));
        assertTrue(forbiddenWordService.isForbidden("BaD word"));
    }

    @Test
    void isForbidden_ShouldCheckAllWordsInText() {
        List<ForbiddenWord> forbiddenWords = List.of(
                new ForbiddenWord(1L, "bad"),
                new ForbiddenWord(2L, "evil")
        );
        when(forbiddenWordRepository.findAll()).thenReturn(forbiddenWords);

        assertTrue(forbiddenWordService.isForbidden("This is bad"));
        assertTrue(forbiddenWordService.isForbidden("evil things"));
        assertTrue(forbiddenWordService.isForbidden("not bad but evil"));
        assertFalse(forbiddenWordService.isForbidden("good things"));
    }
}