package com.twinkle.shopapp.responses;

import com.twinkle.shopapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class UserListResponse {
    private List<User> users;

    private int totalPage;
}
