package ru.job4j.pooh;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Queue;

public class QueueService implements Service {
	private final Map<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();
	private static final String REQUEST_DONE = "200";
	private static final String REQUEST_NO_DATA = "204";
	private static final String POST = "POST";
	private static final String NOT_IMPLEMENTED = "501";

	@Override
	public Resp process(Req req) {
		Resp rslt = new Resp("", NOT_IMPLEMENTED);
		if (POST.equals(req.httpRequestType())) {
			queue.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
			queue.get(req.getSourceName()).add(req.getParam());
			rslt = new Resp(req.getParam(), REQUEST_DONE);
		} else {
			String param = queue.getOrDefault(req.getSourceName(), new ConcurrentLinkedQueue<>()).poll();
			if (param == null) {
				rslt = new Resp("", REQUEST_NO_DATA);
			} else {
				rslt = new Resp(param, REQUEST_DONE);
			}
		}
		return rslt;
	}
}
