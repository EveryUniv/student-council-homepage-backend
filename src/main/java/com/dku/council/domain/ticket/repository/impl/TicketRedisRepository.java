package com.dku.council.domain.ticket.repository.impl;

import com.dku.council.domain.ticket.exception.AlreadyRequestedTicketException;
import com.dku.council.domain.ticket.model.dto.TicketDto;
import com.dku.council.domain.ticket.repository.TicketMemoryRepository;
import com.dku.council.global.config.redis.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.dku.council.global.config.redis.RedisKeys.*;

@Repository
@RequiredArgsConstructor
public class TicketRedisRepository implements TicketMemoryRepository {

    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    @Override
    public int enroll(Long userId, Long ticketEventId, Duration expiresNextKeyAfter) {
        String key = RedisKeys.combine(TICKET_KEY, ticketEventId);
        RLock lock = redissonClient.getLock(key + ":lock");

        try {
            if (lock != null && !lock.tryLock(20, 3, TimeUnit.SECONDS))
                throw new RuntimeException("It waited for 20 seconds, but can't acquire lock");

            String nextIdKey = RedisKeys.combine(TICKET_NEXT_KEY, ticketEventId);
            Long size = redisTemplate.opsForValue().increment(nextIdKey);

            if (size == null) {
                size = 1L;
                redisTemplate.opsForValue().set(nextIdKey, size.toString());
            }
            redisTemplate.expire(nextIdKey, expiresNextKeyAfter);

            if (!redisTemplate.opsForHash().putIfAbsent(key, userId.toString(), Long.toString(size))) {
                throw new AlreadyRequestedTicketException();
            } else {
                redisTemplate.opsForSet().add(TICKET_RESERVATION_SET_KEY, ticketEventId.toString());
            }

            return size.intValue();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
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
        Set<String> members = redisTemplate.opsForSet().members(TICKET_RESERVATION_SET_KEY);

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

        redisTemplate.delete(TICKET_RESERVATION_SET_KEY);
        return tickets;
    }
}
