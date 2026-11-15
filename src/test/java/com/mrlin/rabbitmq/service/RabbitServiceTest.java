package com.mrlin.rabbitmq.service;

import com.rabbitmq.client.Channel;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class RabbitServiceTest {

    @Test
    public void listenerAcksOnlyTheHandledDelivery() throws Exception {
        RabbitService service = new RabbitService();
        Channel channel = mock(Channel.class);

        service.createListener("order-7".getBytes(StandardCharsets.UTF_8), 42L, channel);

        verify(channel).basicAck(42L, false);
        verify(channel, never()).basicNack(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyBoolean(),
                org.mockito.ArgumentMatchers.anyBoolean());
    }
}
