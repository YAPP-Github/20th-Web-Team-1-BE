package com.yapp.betree.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FruitType {
    DEFAULT,
    APPLE,
    PEACH,
    ORANGE,
    LEMON,
    BLUEBERRY,
    GRAPE,
    ;

    @JsonCreator
    public static FruitType create(String source){
        // json으로 들어올 때 처리 (RequestBody)
        String enumList = Stream.of(FruitType.values()).collect(Collectors.toList()).toString();
        try {
            return FruitType.valueOf(source.toUpperCase());
        } catch (Exception e) {
            throw new BetreeException(ErrorCode.ENUM_FORMAT_INVALID, source + " not in " + enumList);
        }
    }
}