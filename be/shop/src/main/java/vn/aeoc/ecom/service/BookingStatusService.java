package vn.aeoc.ecom.service;

import vn.aeoc.base.constant.ErrorCode;
import vn.aeoc.base.exception.AppException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class BookingStatusService {
    private BookingStatusService() {}

    public static final String PENDING = "PENDING";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String ASSIGNED = "ASSIGNED";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String WAITING_PARTS = "WAITING_PARTS";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED";
    public static final String REJECTED = "REJECTED";

    private static final Set<String> ALL = new HashSet<>(Arrays.asList(
            PENDING, CONFIRMED, ASSIGNED, IN_PROGRESS,
            WAITING_PARTS, COMPLETED, CANCELLED, REJECTED
    ));

    public static void validate(String status) {
        if (status == null || !ALL.contains(status)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
    }

    public static void validateTransition(String from, String to) {
        validate(to);
        if (from == null) {
            return;
        }
        if (CANCELLED.equals(from) || COMPLETED.equals(from) || REJECTED.equals(from)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        if (CANCELLED.equals(to) || REJECTED.equals(to)) {
            return;
        }
        if (PENDING.equals(from) && !(CONFIRMED.equals(to) || ASSIGNED.equals(to))) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        if (CONFIRMED.equals(from) && !(ASSIGNED.equals(to) || IN_PROGRESS.equals(to) || CANCELLED.equals(to))) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        if (ASSIGNED.equals(from) && !(IN_PROGRESS.equals(to) || WAITING_PARTS.equals(to) || COMPLETED.equals(to) || CANCELLED.equals(to))) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        if (IN_PROGRESS.equals(from) && !(WAITING_PARTS.equals(to) || COMPLETED.equals(to) || CANCELLED.equals(to))) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        if (WAITING_PARTS.equals(from) && !(IN_PROGRESS.equals(to) || COMPLETED.equals(to) || CANCELLED.equals(to))) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
    }
}
