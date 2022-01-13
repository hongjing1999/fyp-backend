package com.fyp.server.droneUserService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyp.server.domain.Drone;
import com.fyp.server.domain.Drone_;
import com.fyp.server.repository.DroneRepository;
import com.fyp.server.service.dto.DroneDTO;

import tech.jhipster.service.QueryService;

@Service
@Transactional
public class DroneQueryService extends QueryService<Drone>{
	private final Logger log = LoggerFactory.getLogger(DroneQueryService.class);
    private final DroneRepository repository;


    public DroneQueryService(
    		DroneRepository repository
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

    public Page<DroneDTO> read(
        DroneCriteria droneCriteria,
        Pageable pageable) {
    	
    	Page<Drone> dronePage = repository.findAll(createSpecification(droneCriteria), pageable);

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
    	List<DroneDTO> droneDTOList = new ArrayList<>();
    	
    	for(Drone drone : dronePage.getContent()) {
    		DroneDTO droneDTO = new DroneDTO();
    		droneDTO.setDroneUserId(drone.getDroneUserId());
    		if(drone.getIpAddress()!=null) {
    			droneDTO.setIpAddress(drone.getIpAddress());
    		}
    		droneDTO.setId(drone.getId());
    		droneDTO.setName(drone.getName());
    		if(drone.getImage() != null) {
    			droneDTO.setImage(drone.getImage());
    		}
    		if(drone.getLastHeartBeatTime()!=null) {
    			if(ChronoUnit.SECONDS.between(drone.getLastHeartBeatTime(), Instant.now()) > 15) {
        			droneDTO.setOnline(false);
            	}
            	else {
            		droneDTO.setOnline(true);
            	}
    		}
    		else {
    			droneDTO.setOnline(false);
    		}
    		
    		
    		
    		droneDTOList.add(droneDTO);
    	}

        return new PageImpl<>(droneDTOList, pageable, dronePage.getTotalElements());
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
	private Specification<Drone> createSpecification(DroneCriteria criteria) {
		Specification<Drone> specification = Specification.where(null);
		if (criteria != null) {
			
			if(criteria.getDroneUserFilter() != null) {
				specification = specification.and(buildSpecification(criteria.getDroneUserFilter(), Drone_.droneUserId));
			}

//			if(criteria.getCreatedStartDate() != null) {
//				specification = specification.and(buildRangeSpecification(criteria.getCreatedStartDate(), Order_.createdDate));
//			}
//			
//			if(criteria.getCreatedEndDate() != null) {
//				specification = specification.and(buildRangeSpecification(criteria.getCreatedEndDate(), Order_.createdDate));
//			}
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
