package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByItemOwnerId(Long ownerId);

    List<Booking> findAllByBookerIdAndStatusAndStartBeforeAndEndAfterOrderByStart(Long bookerId,
                                                                                  Status status,
                                                                                  Timestamp startBefore,
                                                                                  Timestamp endAfter);

    List<Booking> findAllByBookerIdAndStatusAndStartAfterOrderByStart(Long bookerId,
                                                                      Status status,
                                                                      Timestamp startAfter);

    List<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStart(Long bookerId,
                                                                     Status status,
                                                                     Timestamp endBefore);

    List<Booking> findAllByBookerIdOrderByStart(Long bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStart(Long bookerId, Status status);

    List<Booking> findAllByItemOwnerIdAndStatusAndStartBeforeAndEndAfterOrderByStart(Long ownerId,
                                                                                     Status status,
                                                                                     Timestamp startBefore,
                                                                                     Timestamp endAfter);

    List<Booking> findAllByItemOwnerIdAndStatusAndStartAfterOrderByStart(Long ownerId,
                                                                         Status status,
                                                                         Timestamp startAfter);

    List<Booking> findAllByItemOwnerIdAndStatusAndEndBeforeOrderByStart(Long ownerId,
                                                                        Status status,
                                                                        Timestamp endBefore);

    List<Booking> findAllByItemOwnerIdOrderByStart(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStart(Long ownerId, Status status);

    List<Booking> findAllByItemIdOrderByStart(Long itemId);

    List<Booking> findAllByItemIdInOrderByStart(Iterable<Long> itemIds);

    boolean existsByItemIdAndBookerIdAndStatusAndStartBefore(Long itemId, Long bookerId,
                                                             Status status, Timestamp startBefore);
}
