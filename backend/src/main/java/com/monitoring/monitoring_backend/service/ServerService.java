package com.monitoring.monitoring_backend.service;

import com.monitoring.monitoring_backend.dto.ServerDto;
import com.monitoring.monitoring_backend.entity.Server;
import com.monitoring.monitoring_backend.exception.ServerNotFoundException;
import com.monitoring.monitoring_backend.repository.ServerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServerService {

    private static final Logger logger = LoggerFactory.getLogger(ServerService.class);

    private final ServerRepository serverRepository;

    private final RedisTemplate<String, String> redisTemplate;


    public ServerService(ServerRepository serverRepository,RedisTemplate<String ,String> redisTemplate) {
        this.redisTemplate=redisTemplate;
        this.serverRepository = serverRepository;
    }
    public boolean isAlive(Long serverId) {
        Boolean exists = redisTemplate.hasKey("heartbeat:" + serverId);
        return Boolean.TRUE.equals(exists);
    }

    public List<ServerDto> getAllServers()
    {
        logger.info("Fetching all servers");
        List<Server> servers= serverRepository.findAll();

        return servers.stream().map(server -> convertToDto(server)).toList();
    }

    public ServerDto getServer(Long id) {
        logger.info("Fetching server with id: {}", id);
        Server server= serverRepository.findById(id).orElseThrow( () -> new ServerNotFoundException( "Server with id "+id+" not found"));
        return convertToDto(server);
    }



    private ServerDto convertToDto(Server server) {
        String liveStatus = isAlive(server.getId()) ? "UP" : "DOWN";
        return new ServerDto(server.getHostname(), liveStatus, server.getId());
    }
}
