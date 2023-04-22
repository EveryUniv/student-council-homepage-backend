package com.dku.council.domain.ticket.repository.impl;

import com.dku.council.domain.ticket.model.dto.TicketDto;
import com.dku.council.domain.ticket.repository.TicketMemoryRepository;
import com.dku.council.global.config.redis.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dku.council.global.config.redis.RedisKeys.*;

@Repository
@RequiredArgsConstructor
public class TicketRedisRepository implements TicketMemoryRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public int enroll(Long userId, Long ticketEventId) {
        String nextNumberKey = RedisKeys.combine(TICKET_NUMBER_KEY, ticketEventId);
        Long size = redisTemplate.opsForValue().increment(nextNumberKey);

        if (size == null) {
            redisTemplate.opsForValue().set(nextNumberKey, "1");
            size = 1L;
        }

        String key = RedisKeys.combine(TICKET_KEY, ticketEventId);
        if (!redisTemplate.opsForHash().putIfAbsent(key, userId.toString(), size.toString())) {
            redisTemplate.opsForValue().decrement(nextNumberKey);
            return -1;
        } else {
            redisTemplate.opsForSet().add(TICKET_CACHE_SET_KEY, ticketEventId.toString());
        }

        return size.intValue();
    }

    @Override
    public int getMyTicket(Long userId, Long ticketEventId) {
        String key = RedisKeys.combine(TICKET_KEY, ticketEventId);
        Object value = redisTemplate.opsForHash().get(key, userId.toString());
        if (value == null) {
            return -1;
        }
        return Integer.parseInt((String) value);
    }

    @Override
    public int saveMyTicket(Long userId, Long ticketEventId, int turn) {
        String key = RedisKeys.combine(TICKET_KEY, ticketEventId);
        redisTemplate.opsForHash().put(key, userId.toString(), String.valueOf(turn));
        return turn;
    }

    @Override
    public List<TicketDto> flushAllTickets() {
        List<TicketDto> tickets = new ArrayList<>();
        Set<String> members = redisTemplate.opsForSet().members(TICKET_CACHE_SET_KEY);

        if (members == null) {
            return tickets;
        }

        for (String member : members) {
            Long ticketEventId = Long.parseLong(member);
            String key = RedisKeys.combine(TICKET_KEY, ticketEventId);
            for (Map.Entry<Object, Object> entry : redisTemplate.opsForHash().entries(key).entrySet()) {
                Long userId = Long.parseLong((String) entry.getKey());
                int turn = Integer.parseInt((String) entry.getValue());
                tickets.add(new TicketDto(userId, ticketEventId, turn));
            }
            redisTemplate.delete(key);
        }
        redisTemplate.delete(TICKET_CACHE_SET_KEY);

        return tickets;
    }
}
