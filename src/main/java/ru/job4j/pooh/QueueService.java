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
		Resp rslt;
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
}
