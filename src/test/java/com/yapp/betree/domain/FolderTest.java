package com.yapp.betree.domain;

import com.yapp.betree.dto.response.TreeResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static org.assertj.core.api.Assertions.*;

@DisplayName("폴더(나무) 도메인, DTO 테스트")
public class FolderTest {

    /**
     * 테스트용 기본 데이터
     * TEST_{FRUITTYPE}_TREE : save하기 전 (id값 없음)
     * TEST_SAVE_{FRUITTYPE}_TREE : save (id생성된상태)
     */
    public static Folder TEST_APPLE_TREE = Folder.builder()
            .fruit(FruitType.APPLE)
            .name("사과나무")
            .level(0L)
            .user(TEST_SAVE_USER)
            .build();
    public static Folder TEST_DEFAULT_TREE = Folder.builder()
            .fruit(FruitType.DEFAULT)
            .name("기본")
            .level(0L)
            .user(TEST_SAVE_USER)
            .build();
    public static Folder TEST_SAVE_APPLE_TREE = Folder.builder()
            .id(1L)
            .fruit(FruitType.APPLE)
            .name("사과나무")
            .level(0L)
            .user(TEST_SAVE_USER)
            .build();
    public static Folder TEST_SAVE_DEFAULT_TREE = Folder.builder()
            .id(2L)
            .fruit(FruitType.DEFAULT)
            .name("기본")
            .level(0L)
            .user(TEST_SAVE_USER)
            .build();

    @Test
    @DisplayName("TreeResponseDTO 변환 테스트")
    void TreeResponseDtoTest() {
        TreeResponseDto treeDto = TreeResponseDto.of(TEST_SAVE_APPLE_TREE);

        assertThat(treeDto.getId()).isEqualTo(TEST_SAVE_APPLE_TREE.getId());
        assertThat(treeDto.getName()).isEqualTo(TEST_SAVE_APPLE_TREE.getName());
    }
}
