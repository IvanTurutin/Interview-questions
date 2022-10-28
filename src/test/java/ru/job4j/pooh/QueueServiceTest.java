package ru.job4j.pooh;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class QueueServiceTest {

    @Test
    public void whenPostThenGetQueue() {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        /* Добавляем данные в очередь weather. Режим queue */
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        /* Забираем данные из очереди weather. Режим queue */
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text()).isEqualTo("temperature=18");
        assertThat(result.status()).isEqualTo("200");
    }

    @Test
    public void whenPostThenGetQueueTwice() {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
		result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text()).isEqualTo("");
        assertThat(result.status()).isEqualTo("204");
    }

    @Test
    public void whenPostThenGetMissingQueue() {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        Resp result = queueService.process(
                new Req("GET", "queue", "news", null)
        );
        assertThat(result.text()).isEqualTo("");
        assertThat(result.status()).isEqualTo("204");
    }

}