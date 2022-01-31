package com.aeduard.soccerOnline.repository;

import com.aeduard.soccerOnline.model.TransferListRow;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransferListRepository extends PagingAndSortingRepository<TransferListRow, Long> {
}
