package ru.job4j.pooh;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Queue;

public class QueueService implements Service {
	private Map<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();
	private static final String REQUEST_DONE = "200";
	private static final String REQUEST_NO_DATA = "204";
	private static final String POST = "POST";
    
	@Override
    public Resp process(Req req) {
        Resp rslt = null;
		if (POST.equals(req.httpRequestType())) {
			Queue<String> tmpQueue = queue.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
			if (tmpQueue == null) {
				tmpQueue = queue.get(req.getSourceName());
			}
			tmpQueue.add(req.getParam());
			rslt = new Resp(req.getParam(), REQUEST_DONE);
		} else {
			Queue<String> tmpQueue = queue.get(req.getSourceName());
			if (tmpQueue == null) {
				rslt = new Resp("", REQUEST_NO_DATA);
			} else {
				String param = tmpQueue.poll();
				if (param == null) {
					rslt = new Resp("", REQUEST_NO_DATA);
				} else {
					rslt = new Resp(param, REQUEST_DONE);
				}
			}
		}
		return rslt;
    }
	
	/*1 - whenPostThenGetQueue*/
/*	public static void main(String[] args) {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        // Добавляем данные в очередь weather. Режим queue 
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        // Забираем данные из очереди weather. Режим queue
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        System.out.println("temperature=18".equals(result.text()));
		System.out.println("200".equals(result.status()));

    }
	*/

	/*2 - whenPostThenGetQueueTwice*/
/*	public static void main(String[] args) {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        //Добавляем данные в очередь weather. Режим queue
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        // Забираем данные из очереди weather. Режим queue
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
		result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        System.out.println("".equals(result.text()));
		System.out.println("204".equals(result.status()));
    }
*/
	/*3 - whenPostThenGetMissingQueue*/
	public static void main(String[] args) {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        //Добавляем данные в очередь weather. Режим queue
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        // Забираем данные из очереди weather. Режим queue
        Resp result = queueService.process(
                new Req("GET", "queue", "news", null)
        );
        System.out.println("".equals(result.text()));
		System.out.println("204".equals(result.status()));
    }
}
