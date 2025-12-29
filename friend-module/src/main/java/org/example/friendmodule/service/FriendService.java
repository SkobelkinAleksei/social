package org.example.friendmodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.friendmodule.dto.FriendDto;
import org.example.friendmodule.entity.FriendEntity;
import org.example.friendmodule.mapper.FriendMapper;
import org.example.friendmodule.repository.FriendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;

    @Transactional
    public FriendDto createFriendship(FriendEntity friendEntity) {
        FriendEntity savedEntity = friendRepository.save(friendEntity);
        return friendMapper.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<FriendDto> findAllFriendByUserId(Long userId) {
        List<FriendEntity> allFriend = friendRepository.findAllByUserId(userId);

        return allFriend.stream()
                .map(friendMapper::toDto)
                .toList();
    }
}
