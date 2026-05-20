package vn.aeoc.ecom.dto.request;

import lombok.Data;

@Data
public class CreateReviewRequest {
    private Integer rating;
    private String comment;
}
