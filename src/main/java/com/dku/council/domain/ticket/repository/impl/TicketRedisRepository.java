package com.dku.council.domain.ticket.repository.impl;

import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketMemoryRepository;
import com.dku.council.global.config.redis.RedisKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.dku.council.global.config.redis.RedisKeys.*;

@Repository
@RequiredArgsConstructor
public class TicketRedisRepository implements TicketMemoryRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<TicketEventDto> findAllEvents() {
        String value = redisTemplate.opsForValue().get(TICKET_EVENTS_KEY);
        if (value == null) {
            return null;
        }
        try {
            CollectionType type = objectMapper.getTypeFactory().constructCollectionType(List.class, TicketEventDto.class);
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TicketEventDto> saveEvents(List<TicketEvent> events) {
        List<TicketEventDto> list = events.stream()
                .map(TicketEventDto::new)
                .collect(Collectors.toList());
        String value;
        try {
            value = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForValue().set(TICKET_EVENTS_KEY, value);
        return list;
    }

    @Override
    public int enroll(Long userId, Long ticketEventId) {
        String nextNumberKey = RedisKeys.combine(TICKET_NUMBER_KEY, ticketEventId);
        Long size = redisTemplate.opsForValue().increment(nextNumberKey);

        String key = RedisKeys.combine(TICKET_KEY, ticketEventId);
        if (!redisTemplate.opsForHash().putIfAbsent(key, userId, size)) {
            redisTemplate.opsForValue().decrement(nextNumberKey);
            return -1;
        }

        return size.intValue();
    }

    @Override
    public int getMyTicket(Long userId, Long ticketEventId) {
        String key = RedisKeys.combine(TICKET_KEY, ticketEventId);
        Object value = redisTemplate.opsForHash().get(key, userId);
        if (value == null) {
            return -1;
        }
        return Integer.parseInt((String) value);
    }

    @Override
    public int saveMyTicket(Long userId, Long ticketEventId, int turn) {
        String key = RedisKeys.combine(TICKET_KEY, ticketEventId);
        redisTemplate.opsForHash().put(key, userId, turn);
        return turn;
    }
}
