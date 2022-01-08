package com.fyp.server.droneUserService;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyp.server.domain.Drone;
import com.fyp.server.domain.DroneTelemetry;
import com.fyp.server.domain.DroneTelemetry_;
import com.fyp.server.repository.DroneRepository;
import com.fyp.server.repository.DroneTelemetryRepository;
import com.fyp.server.service.dto.DroneDTO;
import com.fyp.server.service.dto.DroneTelemetryDTO;

import tech.jhipster.service.QueryService;

@Service
@Transactional
public class DroneTelemetryQueryService extends QueryService<DroneTelemetry>{
	private final Logger log = LoggerFactory.getLogger(DroneTelemetryQueryService.class);
    private final DroneTelemetryRepository repository;


    public DroneTelemetryQueryService(
    		DroneTelemetryRepository repository
    ) {
        this.repository = repository;
       
    }
    
//    public List<MerchantOrderDTO> getTodayOrder(Long merchantId, Boolean preOrder){
//    	DateTime today = new DateTime().withTimeAtStartOfDay();
//    	DateTime tomorrow = today.plusDays(1);
//    	
//    	OrderCriteria orderCriteria = new OrderCriteria();
//    	
//    	InstantFilter startDate = new InstantFilter();
//    	startDate.setGreaterOrEqualThan(today.toDate().toInstant());
//    	
//    	InstantFilter endDate = new InstantFilter();
//    	endDate.setLessOrEqualThan(tomorrow.toDate().toInstant());
//    	
//    	if(preOrder) {
//    		BooleanFilter preOrderFilter = new BooleanFilter();
//    		preOrderFilter.setEquals(true);
//    		orderCriteria.setPreOrder(preOrderFilter);
//    	}
//    	orderCriteria.setCreatedEndDate(endDate);
//    	orderCriteria.setCreatedStartDate(startDate);
//    	
//    	Specification<Order> orderSpecification = createSpecification(orderCriteria);
//    	List<Order> orderList = repository.findAll(orderSpecification);
//    	List<MerchantOrderDTO> merchantOrderDTOList = new ArrayList<>();
//    	
//    	for(Order order: orderList) {
//    		OrderUser orderUser = orderUserValidationService.requireUserById(order.getOrderUserId());
//    		List<OrderFoodItemDTO> orderFoodItemDTOs = orderFoodItemQueryService.read(order.getMerchantId(), order.getId());
//    		OrderStatus orderStatus = orderStatusQueryService.readByOrderId(order.getId());
//    		merchantOrderDTOList.add(new MerchantOrderDTO(
//                    order,
//                    orderUser,
//                    orderFoodItemDTOs,
//                    orderStatus
//                ));
//    	}
//    	return merchantOrderDTOList;
//    }

    public List<DroneTelemetryDTO> read(
        DroneTelemetryCriteria droneTelemetryCriteria, Sort sort) {
    	
    	List<DroneTelemetry> droneTelemetryList = repository.findAll(createSpecification(droneTelemetryCriteria), sort);

//        .

//        for (Order order : page.getContent()) {
//            OrderUser orderUser = orderUserValidationService.requireUserById(order.getOrderUserId());
//            List<OrderFoodItemDTO> orderFoodItemDTOs = orderFoodItemQueryService.read(order.getMerchantId(), order.getId());
//            OrderStatus orderStatus = orderStatusQueryService.readByOrderId(order.getId());
//
//            merchantOrderDTOs.add(new MerchantOrderDTO(
//                order,
//                orderUser,
//                orderFoodItemDTOs,
//                orderStatus
//            ));
//        }
    	List<DroneTelemetryDTO> droneTelemetryDTOList = new ArrayList<>();
    	
    	for(DroneTelemetry droneTelemetry : droneTelemetryList) {
    		DroneTelemetryDTO droneTelemetryDTO = new DroneTelemetryDTO(droneTelemetry);
    		
    		droneTelemetryDTOList.add(droneTelemetryDTO);
    	}

        return droneTelemetryDTOList;
    }

//    private Specification<Drone> getSpec(DroneCriteria c) {
//        Specification<Drone> s = Specification.where(null);
//        if (c != null) {
//            Specification<Drone> or = Specification.where(null);
//            if (c.getMerchantId() != null) {
//                or = or.and(buildSpecification(c.getMerchantId(), Order_.merchantId));
//            }
//            s = s.and(or);
//        }
//        return s;
//    }
//    
	private Specification<DroneTelemetry> createSpecification(DroneTelemetryCriteria criteria) {
		Specification<DroneTelemetry> specification = Specification.where(null);
		if (criteria != null) {
			
			if(criteria.getDroneId() != null) {
				specification = specification.and(buildSpecification(criteria.getDroneId(), DroneTelemetry_.droneId));
			}

			if(criteria.getCreatedDateStart() != null) {
				specification = specification.and(buildRangeSpecification(criteria.getCreatedDateStart(), DroneTelemetry_.createdDate));
			}
			
			if(criteria.getCreatedDateEnd() != null) {
				specification = specification.and(buildRangeSpecification(criteria.getCreatedDateEnd(), DroneTelemetry_.createdDate));
			}
//
//			if (criteria.getMerchantId() != null) {
//				specification = specification.and(buildSpecification(criteria.getMerchantId(), Order_.merchantId));
//			}
//			
//			if(criteria.getPreOrder() != null) {
//				specification = specification.and(buildSpecification(criteria.getPreOrder(), Order_.preOrder));
//			}

		}
		return specification;
	}
}
