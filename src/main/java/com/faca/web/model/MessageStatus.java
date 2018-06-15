package com.faca.web.model;

public interface MessageStatus {

    String INVALID_TOKEN = "토큰이 유효하지 않습니다.";

    String SUCCESS_ADD_CATEGORY = "카테고리가 추가되었습니다.";

    String UNKNOWN_SERVER_ERROR = "서버에서 에러가 발생하였습니다.";

    String SUCCESS_ADD_CATEGORY_TO_FEED = "게시물에 카테고리가 추가되었습니다.";

    String SUCCESS_DELETE_CATEGORY_TO_FEED = "게시물에 카테고리가 삭제되었습니다.";

    String NOT_FOUND_CATEGORY = "카테고리가 존재하지 않습니다.";

    String SUCCESS_DELETE_CATEGORY = "카테고리가 삭제되었습니다.";
}
