package com.cinar.textile.dto.response;

import com.cinar.textile.dto.UserDto;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private List<UserDto> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}