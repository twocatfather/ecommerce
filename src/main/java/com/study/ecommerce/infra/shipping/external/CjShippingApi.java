package com.study.ecommerce.infra.shipping.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CJ대한통운 외부 API (실제 API 호출 시뮬레이션)
 */
@Slf4j
@Component
public class CjShippingApi {
    /**
     * CJ대한통운 배송 등록
     */
    public CjShippingResponse registerDelivery(CjShippingRequest request) {
        // TODO
        String trackingNumber = "CJ" + System.currentTimeMillis();

        return CjShippingResponse.builder()
                .resultCode("0000")
                .resultMessage("성공")
                .invoiceNo(trackingNumber)
                .orderNo(request.orderNo())
                .deliveryCharge(calculateDeliveryCharge(request))
                .build();
    }

    /**
     * 배송 상태 조회
     */
    public CjTrackingResponse getTrackingInfo(String invoiceNo) {
        // TODO
        return CjTrackingResponse.builder()
                .resultCode("0000")
                .resultMessage("성공")
                .invoiceNo(invoiceNo)
                .deliveryStatus("30")
                .deliveryStatusName("배송중")
                .build();
    }

    /**
     * 배송 취소
     */
    public CjShippingResponse cancelDelivery(String invoiceNo, String cancelReason) {
        log.info("CJ 대한통운 API 호출 - 배송 취소 {} - {}", invoiceNo, cancelReason);
        // TODO
        return CjShippingResponse.builder()
                .resultCode("0000")
                .resultMessage("취소 완료")
                .invoiceNo(invoiceNo)
                .build();
    }

    /**
     * 배송비 계산 (CJ 고유 로직)
     */
    private int calculateDeliveryCharge(CjShippingRequest request) {
        int baseCharge = 3000; // 기본 배송비

        // 무게에 따른 추가 요금
        // 5kg 초과
        if (request.weight() > 5000) {
            baseCharge += 2000;
        }

        // 제주도/도서산간 추가 요금
        if (request.receiverZipCode().startsWith("63")) {
            baseCharge += 3000;
        }

        return baseCharge;
    }
}
