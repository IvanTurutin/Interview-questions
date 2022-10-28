package ru.job4j.pooh;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;

public class TopicService implements Service {
    private Map<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> queue = new ConcurrentHashMap<>();
    private static final String REQUEST_DONE = "200";
    private static final String REQUEST_NO_DATA = "204";
    private static final String POST = "POST";

    @Override
    public Resp process(Req req) {
        Resp rslt = null; //можно удалить, когда все ветви if else будут осуществлять возврат
        if (POST.equals(req.httpRequestType())) {
            /*BiFunction<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>, 
								ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> updateQueues = 
								(sourceName, tmpMapOfQueues) -> {
				for (Map.Entry<String, ConcurrentLinkedQueue<String>> entry : tmpMapOfQueues.entrySet()) {
                    entry.getValue().add(req.getParam());
                }
				return tmpMapOfQueues;
			};
			
			return queue.computeIfPresent(req.getSourceName(), updateQueues) != null ? 
					new Resp(req.getParam(), REQUEST_DONE) :
					new Resp("", REQUEST_NO_DATA);
			*/
			/*
			if (queue.computeIfPresent(req.getSourceName(), updateQueues) != null) {
				rslt = new Resp(req.getParam(), REQUEST_DONE);
			} else {
                rslt = new Resp("", REQUEST_NO_DATA);
            }*/
			
			
			Map<String, ConcurrentLinkedQueue<String>> tmpMapOfQueues = queue.get(req.getSourceName());

            if (tmpMapOfQueues != null) {
                for (Map.Entry<String, ConcurrentLinkedQueue<String>> entry : tmpMapOfQueues.entrySet()) {
                    entry.getValue().add(req.getParam());
                }
                rslt = new Resp(req.getParam(), REQUEST_DONE);

            } else {
                rslt = new Resp("", REQUEST_NO_DATA);
            }

        } else {
			Map<String, ConcurrentLinkedQueue<String>> tmpMapOfQueues =
                    queue.putIfAbsent(req.getSourceName(), new ConcurrentHashMap<>());
            if (tmpMapOfQueues == null) {
                queue.get(req.getSourceName()).put(req.getParam(), new ConcurrentLinkedQueue<>());
				rslt = new Resp("", REQUEST_NO_DATA);
            } else {
                Queue<String> tmpUserQueue =
                    tmpMapOfQueues.putIfAbsent(req.getParam(), new ConcurrentLinkedQueue<>());
				if (tmpUserQueue == null) {
					rslt = new Resp("", REQUEST_NO_DATA);
				} else {
					String param = tmpUserQueue.poll();
					if (param == null) {
						rslt = new Resp("", REQUEST_NO_DATA);
					} else {
						rslt = new Resp(param, REQUEST_DONE);
					}
				}
            }
        }
        return rslt;
    }
	
	//Test 1
	//whenTopic
	/*public static void main(String[] args) {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        // Режим topic. Подписываемся на топик weather. client407.
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        // Режим topic. Добавляем данные в топик weather.
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        // Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407.
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        // Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565.
        //Очередь отсутствует, т.к. еще не был подписан - получит пустую строку
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
        //assertThat(result1.text(), is("temperature=18"));
		System.out.println("temperature=18".equals(result1.text()));
		//assertThat(result2.text(), is(""));
		System.out.println("".equals(result2.text()));
    }*/
	
	//Test 2 whenTwoClientsGetQueue
	/*public static void main(String[] args) {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
		System.out.println("".equals(result1.text()));
		System.out.println("".equals(result2.text()));
		
		// Режим topic. Подписываемся на топик weather. client407.
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
		System.out.println("".equals(result1.text()));
        // Режим topic. Подписываемся на топик weather. client6565.
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
		System.out.println("".equals(result2.text()));
        // Режим topic. Добавляем данные в топик weather.
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
		
        // Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407.
        result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        // Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565.
        result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
		
        //assertThat(result1.text(), is("temperature=18"));
		System.out.println("temperature=18".equals(result1.text()));
		//assertThat(result2.text(), is(""));
		System.out.println("temperature=18".equals(result2.text()));
    }*/

	
	//Test 3 whenTwoClientsGetDifferentQueues
	public static void main(String[] args) {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForPublisher2 = "temperature=20";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
		
		// Режим topic. Подписываемся на топик weather. client407.
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        // Режим topic. Добавляем данные в топик weather.
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
		
        // Режим topic. Подписываемся на топик weather. client6565.
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
		
        // Режим topic. Добавляем данные в топик weather.
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher2)
        );
		
        // Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407.
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        // Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565.
		Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );

		// Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407.
		result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
		);
		// Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565.
        result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
		
        //assertThat(result1.text(), is("temperature=18"));
		System.out.println("temperature=20".equals(result1.text()));
		//assertThat(result2.text(), is(""));
		System.out.println("".equals(result2.text()));
    }
}
