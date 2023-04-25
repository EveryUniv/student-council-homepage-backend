package com.dku.council.domain.ticket.repository.impl;

import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketEventMemoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dku.council.global.config.redis.RedisKeys.TICKET_EVENTS_KEY;

@Repository
@RequiredArgsConstructor
public class TicketEventRedisRepository implements TicketEventMemoryRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<TicketEventDto> findAll() {
        return redisTemplate.opsForHash().values(TICKET_EVENTS_KEY)
                .stream()
                .map(o -> (String) o)
                .map(s -> {
                    try {
                        return objectMapper.readValue(s, TicketEventDto.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketEventDto> saveAll(List<TicketEvent> events) {
        List<TicketEventDto> list = events.stream()
                .map(TicketEventDto::new)
                .collect(Collectors.toList());
        Map<String, String> map = new HashMap<>();

        for (TicketEventDto dto : list) {
            try {
                map.put(dto.getId().toString(), objectMapper.writeValueAsString(dto));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        redisTemplate.opsForHash().putAll(TICKET_EVENTS_KEY, map);
        return list;
    }

    @Override
    public TicketEventDto save(TicketEvent event) {
        TicketEventDto dto = new TicketEventDto(event);
        String value;
        try {
            value = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForHash().put(TICKET_EVENTS_KEY, event.getId().toString(), value);
        return dto;
    }

    @Override
    public Optional<TicketEventDto> findById(Long id) {
        Object value = redisTemplate.opsForHash().get(TICKET_EVENTS_KEY, id.toString());
        if (value == null) {
            return Optional.empty();
        }
        try {
            TicketEventDto result = objectMapper.readValue((String) value, TicketEventDto.class);
            return Optional.ofNullable(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        redisTemplate.opsForHash().delete(TICKET_EVENTS_KEY, id.toString());
    }
}
