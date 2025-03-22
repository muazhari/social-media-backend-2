package com.muazhari.socialmediabackend2.inners.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatUseCase {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ChatRepository chatRepository;
}
