package com.github.freeacs.tr069.methods;

import com.github.freeacs.tr069.HTTPReqResData;
import com.github.freeacs.tr069.HTTPResData;
import com.github.freeacs.tr069.exception.TR069Exception;
import com.github.freeacs.tr069.exception.TR069ExceptionShortMessage;

import java.util.Map;

/* This class is responsible for choosing the next response in the
 * TR-069 conversation. Depending upon the request, different logic
 * applies. 
 */
public class DecisionMaker {

	public static void process(HTTPReqResData reqRes, Map<String, HTTPRequestAction> requestMap) throws TR069Exception {
		HTTPResData resData = reqRes.getResponse();
		if (reqRes.getThrowable() != null) {
			resData.setMethod(TR069Method.EMPTY);
			return;
		}
		String reqMethod;
		HTTPRequestAction reqAction;
		try {
			reqMethod = reqRes.getRequest().getMethod();
			reqAction = requestMap.get(reqMethod);
			reqAction.getDecisionMakerMethod().apply(reqRes);
		} catch (Throwable t) {
			int loopCount = 0;
			while (t.getCause() != null) {
				t = t.getCause();
				if (++loopCount > 10)
					break;
			}
			throw new TR069Exception("An error occurred in DecisionMaker: " +t.getMessage(), TR069ExceptionShortMessage.MISC, t);
		}
	}
}
