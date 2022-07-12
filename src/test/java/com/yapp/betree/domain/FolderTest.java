package com.yapp.betree.domain;

import com.yapp.betree.dto.response.TreeResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static org.assertj.core.api.Assertions.assertThat;

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
            .opening(false)
            .user(TEST_SAVE_USER)
            .build();
    public static Folder TEST_DEFAULT_TREE = Folder.builder()
            .fruit(FruitType.DEFAULT)
            .name("기본")
            .level(0L)
            .opening(false)
            .user(TEST_SAVE_USER)
            .build();
    public static Folder TEST_SAVE_APPLE_TREE = Folder.builder()
            .id(1L)
            .fruit(FruitType.APPLE)
            .name("사과나무")
            .level(0L)
            .opening(true)
            .user(TEST_SAVE_USER)
            .build();
    public static Folder TEST_SAVE_DEFAULT_TREE = Folder.builder()
            .id(2L)
            .fruit(FruitType.DEFAULT)
            .name("기본")
            .level(0L)
            .opening(false)
            .user(TEST_SAVE_USER)
            .build();

    @Test
    @DisplayName("TreeResponseDTO 변환 테스트")
    void TreeResponseDtoTest() {
        TreeResponseDto treeDto = TreeResponseDto.of(TEST_SAVE_APPLE_TREE);

        assertThat(treeDto.getId()).isEqualTo(TEST_SAVE_APPLE_TREE.getId());
        assertThat(treeDto.getName()).isEqualTo(TEST_SAVE_APPLE_TREE.getName());
    }

    @Test
    @DisplayName("override 메서드 테스트")
    void overrideTest() {
        TreeResponseDto of = TreeResponseDto.of(TEST_SAVE_APPLE_TREE);
        TreeResponseDto build = TreeResponseDto.builder()
                .id(TEST_SAVE_APPLE_TREE.getId())
                .name(TEST_SAVE_APPLE_TREE.getName())
                .fruit(TEST_SAVE_APPLE_TREE.getFruit())
                .build();
        assertThat(of).isEqualTo(build);
        assertThat(of.hashCode()).isEqualTo(build.hashCode());
    }
}
