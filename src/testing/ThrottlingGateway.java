package testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ThrottlingGateway {

    private ThrottlingGateway() {}

    public static int droppedRequests(List<Integer> requestTime) {
        Map<Integer, Long> requestPerTimeMap = requestTime.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<RequestHelper> list = convertMapToList(requestPerTimeMap);
        Map<Integer, RequestHelper> collect = list.stream().collect(Collectors.toMap(RequestHelper::getNumber, Function.identity()));

        return list.stream().map(requestHelper -> getDroppedCount(collect, requestHelper)).reduce(0L, Long::sum).intValue();
    }

    private static List<RequestHelper> convertMapToList(Map<Integer, Long> map) {
        List<RequestHelper> list = new ArrayList<>();
        RequestHelper previous = null;
        for (Map.Entry<Integer, Long> entry : map.entrySet()) {
            RequestHelper requestHelper = new RequestHelper(entry.getKey(), entry.getValue());
            if (previous == null) {
                requestHelper.setPreviousSum(0);
            } else requestHelper.setPreviousSum(previous.previousSum + previous.requestCount);
            list.add(requestHelper);
            previous = requestHelper;
        }
        return list;
    }

    private static long getDroppedCount(Map<Integer, RequestHelper> map, RequestHelper requestHelper) {
        long droppedBy3 = droppedBy3(requestHelper);
        long droppedBy10 = droppedBy(requestHelper, map, 10, 20L);
        long droppedBy60 = droppedBy(requestHelper, map, 60, 60L);

        return LongStream.of(droppedBy3, droppedBy60, droppedBy10).max().getAsLong();
    }

    private static long droppedBy3(RequestHelper requestHelper) {
        return requestHelper.requestCount > 3 ? requestHelper.requestCount - 3L : 0L;
    }

    private static long droppedBy(RequestHelper requestHelper, Map<Integer, RequestHelper> map, int timePeriod, long maxRequestPerTimePeriod) {
        long droppedBy10 = 0L;
        long tempPreviousSum = requestHelper.number <= timePeriod ? requestHelper.previousSum : requestHelper.previousSum - getNearestPrevious(map, requestHelper.number - timePeriod);

        if (tempPreviousSum >= maxRequestPerTimePeriod) {
            droppedBy10 = requestHelper.requestCount;
        } else if (tempPreviousSum + requestHelper.requestCount > maxRequestPerTimePeriod)
            droppedBy10 = tempPreviousSum + requestHelper.requestCount - maxRequestPerTimePeriod;
        return droppedBy10;
    }

    private static long getNearestPrevious(Map<Integer, RequestHelper> map, int key) {
        int tempKey = key;
        RequestHelper response = null;
        while (response == null && tempKey > 0) {
            response = map.get(tempKey);
            tempKey--;
        }
        return response == null ? 0L : response.previousSum == 0L ? response.requestCount : response.previousSum;
    }

    private static class RequestHelper {
        private final int number;
        private final long requestCount;
        private long previousSum;

        public RequestHelper(int number, long requestCount) {
            this.number = number;
            this.requestCount = requestCount;
        }

        public int getNumber() {
            return number;
        }

        public void setPreviousSum(long previousSum) {
            this.previousSum = previousSum;
        }
    }
}
