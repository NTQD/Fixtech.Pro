package vn.aeoc.ecom.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.aeoc.base.constant.ErrorCode;
import vn.aeoc.base.exception.AppException;
import vn.aeoc.entity.data.mysql.RepairBooking;
import vn.aeoc.entity.data.mysql.RepairBookingItem;
import vn.aeoc.entity.data.mysql.RepairBookingNote;
import vn.aeoc.entity.data.mysql.RepairBookingStatusHistory;
import vn.aeoc.entity.data.mysql.RepairReview;
import vn.aeoc.entity.data.mysql.RepairService;
import vn.aeoc.entity.data.mysql.User;
import vn.aeoc.ecom.dto.request.AssignTechnicianRequest;
import vn.aeoc.ecom.dto.request.BookingCancelRequest;
import vn.aeoc.ecom.dto.request.ChangeBookingStatusRequest;
import vn.aeoc.ecom.dto.request.CreateBookingItemRequest;
import vn.aeoc.ecom.dto.request.CreateBookingRequest;
import vn.aeoc.ecom.dto.request.CreateReviewRequest;
import vn.aeoc.ecom.dto.response.BookingDetailResponse;
import vn.aeoc.ecom.repository.RepairBookingItemRepository;
import vn.aeoc.ecom.repository.RepairBookingNoteRepository;
import vn.aeoc.ecom.repository.RepairBookingRepository;
import vn.aeoc.ecom.repository.RepairBookingStatusHistoryRepository;
import vn.aeoc.ecom.repository.RepairReviewRepository;
import vn.aeoc.ecom.repository.RepairServiceRepository;
import vn.aeoc.ecom.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@AllArgsConstructor
public class RepairBookingFlowService {
    private final RepairBookingRepository bookingRepository;
    private final RepairBookingItemRepository itemRepository;
    private final RepairBookingStatusHistoryRepository historyRepository;
    private final RepairBookingNoteRepository noteRepository;
    private final RepairReviewRepository reviewRepository;
    private final RepairServiceRepository repairServiceRepository;
    private final UserRepository userRepository;

    @Transactional
    public RepairBooking createBooking(CreateBookingRequest request, User currentUser) {
        validateCreateBooking(request);

        String bookingCode = "BK_TEMP_" + System.currentTimeMillis();
        AtomicLong totalPrice = new AtomicLong(0);
        AtomicLong totalMinutes = new AtomicLong(0);

        for (CreateBookingItemRequest item : request.getItems()) {
            RepairService service = requireService(item.getServiceId());
            long qty = normalizeQuantity(item.getQuantity());
            totalPrice.addAndGet((service.getBasePrice() == null ? 0L : service.getBasePrice()) * qty);
            totalMinutes.addAndGet((service.getEstimatedMinutes() == null ? 0 : service.getEstimatedMinutes()) * qty);
        }

        RepairBooking booking = new RepairBooking();
        booking.setBookingCode(bookingCode);
        booking.setCustomerId(currentUser != null ? currentUser.getId() : null);
        booking.setCustomerName(request.getCustomerName());
        booking.setCustomerPhone(request.getCustomerPhone());
        booking.setCustomerEmail(request.getCustomerEmail());
        booking.setDeviceType(request.getDeviceType());
        booking.setDeviceBrand(request.getDeviceBrand());
        booking.setDeviceModel(request.getDeviceModel());
        booking.setIssueDescription(request.getIssueDescription());
        booking.setPreferredDate(request.getPreferredDate() != null ? request.getPreferredDate().toString() : null);
        booking.setPreferredTimeSlot(request.getPreferredTimeSlot());
        booking.setAddress(request.getAddress());
        booking.setNote(request.getNote());
        booking.setStatus(BookingStatusService.PENDING);
        booking.setTotalEstimatedPrice(totalPrice.get());
        booking.setTotalEstimatedMinutes((int) totalMinutes.get());
        booking.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        booking.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        RepairBooking saved = bookingRepository.insertReturning(booking);
        
        saved.setBookingCode(String.format("BK%06d", saved.getId()));
        bookingRepository.update(saved.getId(), saved);
        for (CreateBookingItemRequest item : request.getItems()) {
            RepairService service = requireService(item.getServiceId());
            long qty = normalizeQuantity(item.getQuantity());
            RepairBookingItem bookingItem = new RepairBookingItem();
            bookingItem.setBookingId(saved.getId());
            bookingItem.setServiceId(service.getId());
            bookingItem.setServiceName(service.getName());
            bookingItem.setQuantity((int) qty);
            bookingItem.setEstimatedPrice(service.getBasePrice());
            bookingItem.setFinalPrice(service.getBasePrice());
            itemRepository.insert(bookingItem);
        }

        historyRepository.insert(createHistory(saved.getId(), null, BookingStatusService.PENDING, "Tạo booking"));
        return saved;
    }

    @Transactional
    public void cancelBooking(Integer bookingId, BookingCancelRequest request) {
        RepairBooking booking = requireBooking(bookingId);
        if (!BookingStatusService.PENDING.equals(booking.getStatus())) {
            throw new AppException("Chỉ có thể hủy đơn hàng đang chờ xử lý", 400);
        }
        BookingStatusService.validateTransition(booking.getStatus(), BookingStatusService.CANCELLED);
        bookingRepository.updateField(bookingId, vn.entity.backend.tables.RepairBooking.REPAIR_BOOKING.STATUS, BookingStatusService.CANCELLED);
        historyRepository.insert(createHistory(bookingId, booking.getStatus(), BookingStatusService.CANCELLED,
                request != null && request.getReason() != null ? request.getReason() : "Hủy booking"));
    }

    @Transactional
    public void changeStatus(Integer bookingId, ChangeBookingStatusRequest request) {
        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        RepairBooking booking = requireBooking(bookingId);
        bookingRepository.updateField(bookingId, vn.entity.backend.tables.RepairBooking.REPAIR_BOOKING.STATUS, request.getStatus());
        historyRepository.insert(createHistory(bookingId, booking.getStatus(), request.getStatus(), request.getNote()));
    }

    @Transactional
    public void assignTechnician(Integer bookingId, AssignTechnicianRequest request) {
        if (request == null || request.getTechnicianId() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        RepairBooking booking = requireBooking(bookingId);
        BookingStatusService.validateTransition(booking.getStatus(), BookingStatusService.ASSIGNED);
        bookingRepository.updateField(bookingId, vn.entity.backend.tables.RepairBooking.REPAIR_BOOKING.TECHNICIAN_ID, request.getTechnicianId());
        bookingRepository.updateField(bookingId, vn.entity.backend.tables.RepairBooking.REPAIR_BOOKING.STATUS, BookingStatusService.ASSIGNED);
        historyRepository.insert(createHistory(bookingId, booking.getStatus(), BookingStatusService.ASSIGNED, request.getNote()));
    }

    @Transactional
    public RepairReview createReview(Integer bookingId, CreateReviewRequest request, User currentUser) {
        if (request == null || request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        RepairBooking booking = requireBooking(bookingId);
        Integer serviceId = findFirstServiceIdByBookingId(bookingId);
        if (serviceId == null) {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }
        RepairReview review = new RepairReview();
        review.setBookingId(bookingId);
        review.setServiceId(serviceId);
        review.setCustomerId(currentUser != null ? currentUser.getId() : booking.getCustomerId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return reviewRepository.insertReturning(review);
    }

    @Transactional
    public void addNote(Integer bookingId, String note, Integer createdBy) {
        if (note == null || note.isBlank()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        requireBooking(bookingId);
        RepairBookingNote bookingNote = new RepairBookingNote();
        bookingNote.setBookingId(bookingId);
        bookingNote.setCreatedBy(createdBy);
        bookingNote.setNote(note);
        noteRepository.insert(bookingNote);
    }

    public BookingDetailResponse getDetail(Integer bookingId) {
        RepairBooking booking = requireBooking(bookingId);
        BookingDetailResponse response = new BookingDetailResponse();
        response.setBooking(booking);
        response.setItems(itemRepository.getListByCriteria(vn.entity.backend.tables.RepairBookingItem.REPAIR_BOOKING_ITEM.BOOKING_ID.eq(bookingId)));
        response.setStatusHistory(historyRepository.getListByCriteria(vn.entity.backend.tables.RepairBookingStatusHistory.REPAIR_BOOKING_STATUS_HISTORY.BOOKING_ID.eq(bookingId)));
        return response;
    }

    private RepairBooking requireBooking(Integer bookingId) {
        RepairBooking booking = bookingRepository.getById(bookingId);
        if (booking == null) {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }
        return booking;
    }

    private RepairService requireService(Integer serviceId) {
        RepairService service = repairServiceRepository.getById(serviceId);
        if (service == null) {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }
        return service;
    }

    private long normalizeQuantity(Integer quantity) {
        return quantity == null || quantity < 1 ? 1 : quantity;
    }

    private void validateCreateBooking(CreateBookingRequest request) {
        if (request == null) throw new AppException(ErrorCode.BAD_REQUEST);
        if (request.getCustomerName() == null || request.getCustomerName().isBlank()) throw new AppException(ErrorCode.BAD_REQUEST);
        if (request.getCustomerPhone() == null || request.getCustomerPhone().isBlank()) throw new AppException(ErrorCode.BAD_REQUEST);
        if (request.getItems() == null || request.getItems().isEmpty()) throw new AppException(ErrorCode.BAD_REQUEST);
        
        String timeSlot = request.getPreferredTimeSlot();
        if (timeSlot != null && !timeSlot.isBlank()) {
            if (!timeSlot.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                throw new AppException("Định dạng giờ không hợp lệ (vd: 09:00)", ErrorCode.BAD_REQUEST.getCode());
            }
            String[] parts = timeSlot.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            if (hour < 8 || hour > 22 || (hour == 22 && minute > 0)) {
                throw new AppException("Chỉ cho phép đặt lịch trong giờ hành chính từ 08:00 đến 22:00", ErrorCode.BAD_REQUEST.getCode());
            }
            
            java.time.LocalDate date = request.getPreferredDate();
            if (date != null) {
                Long techniciansCount = userRepository.countByRole("TECHNICIAN");
                Long overlappingBookings = bookingRepository.countOverlappingBookings(date, timeSlot);
                if (techniciansCount != null && overlappingBookings != null && overlappingBookings >= techniciansCount) {
                    throw new AppException("Thời gian này các thợ đã kín lịch, vui lòng chọn thời gian khác", ErrorCode.BAD_REQUEST.getCode());
                }
            }
        }
    }


    private RepairBookingStatusHistory createHistory(Integer bookingId, String from, String to, String note) {
        RepairBookingStatusHistory history = new RepairBookingStatusHistory();
        history.setBookingId(bookingId);
        history.setFromStatus(from);
        history.setToStatus(to);
        history.setNote(note);
        return history;
    }

    private Integer findFirstServiceIdByBookingId(Integer bookingId) {
        List<RepairBookingItem> items = itemRepository.getListByCriteria(vn.entity.backend.tables.RepairBookingItem.REPAIR_BOOKING_ITEM.BOOKING_ID.eq(bookingId));
        return items.isEmpty() ? null : items.get(0).getServiceId();
    }
}
