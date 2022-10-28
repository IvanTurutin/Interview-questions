package ru.job4j.pooh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    private final Map<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> queue = new ConcurrentHashMap<>();
    private static final String REQUEST_DONE = "200";
    private static final String REQUEST_NO_DATA = "204";
    private static final String POST = "POST";

    @Override
    public Resp process(Req req) {
        Resp rslt;
        if (POST.equals(req.httpRequestType())) {
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
}
